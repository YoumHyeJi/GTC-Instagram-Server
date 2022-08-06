package com.garit.instagram.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.domain.member.Member;
import com.garit.instagram.domain.member.MemberRepository;
import com.garit.instagram.domain.member.dto.LoginResDTO;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.garit.instagram.config.base.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class OAuthService {

    @Value("${kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;
    private final LoginService loginService;

    /**
     * [KAKAO 소셜 로그인]
     * code를 기반으로 accessToken 받아오기
     */
    public String getKakaoAccessToken(String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + KAKAO_CLIENT_ID); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=" + KAKAO_REDIRECT_URI); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }


    /**
     * [KAKAO 소셜 로그인]
     * accessToken을 기반으로 로그인 진행하기
     */
    public LoginResDTO kakaoLogin(HttpServletResponse response, String kakaoAccesstoken, String deviceTokenValue) throws BaseException {

        try {
            // accessToken을 기반으로 회원정보(kakakoMemberId) 받아오기
            Long kakaoMemberId = getKakaoMemberId(kakaoAccesstoken);


            Member member = memberRepository.findMemberByKakakoMemberId(kakaoMemberId).orElse(null);

            // 로그인
            if (member != null) {
                return loginService.afterLoginSuccess(response, member, deviceTokenValue);
            }
            // 회원가입 필요
            else {
                throw new BaseException(NEED_TO_KAKAO_SIGNUP);
            }

        } catch (BaseException e) {
            throw e;
        } catch (Exception e){
            log.error("kakaoLogin() : memberRepository.findMemberByKakakoMemberId() 실행 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * [KAKAO 소셜 로그인]
     * accessToken을 기반으로 회원정보(kakakoMemberId) 받아오기
     */
    public Long getKakaoMemberId(String kakaoAccesstoken) throws BaseException {

        try {
            // HTTP Header 생성
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + kakaoAccesstoken);
            headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

            // HTTP 요청 보내기
            HttpEntity<MultiValueMap<String, String>> kakaoMemberInfoRequest = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.POST,
                    kakaoMemberInfoRequest,
                    String.class
            );

            // responseBody에 있는 정보를 꺼냄
            String responseBody = response.getBody();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            Long kakaoMemberId = jsonNode.get("id").asLong();
            log.info("kakaoMemberId = " + kakaoMemberId);

            // String email = jsonNode.get("kakao_account").get("email").asText();
            // log.info("email = " + email);

            return kakaoMemberId;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new BaseException(FAIL_TO_GET_KAKAO_MEMBER_ID);
        } catch (Exception e){
            e.printStackTrace();
            throw new BaseException(FAIL_TO_GET_KAKAO_MEMBER_ID);
        }
    }
}
