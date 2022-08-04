package com.garit.instagram.service;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.domain.deviceToken.DeviceToken;
import com.garit.instagram.domain.member.LoginType;
import com.garit.instagram.domain.member.Member;
import com.garit.instagram.domain.member.MemberRepository;
import com.garit.instagram.domain.member.dto.JoinReqDTO;
import com.garit.instagram.domain.member.dto.LoginResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

import java.util.Random;

import static com.garit.instagram.config.base.BaseResponseStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginService loginService;
    private final Random random;
    private final RedisService redisService;
    private final OAuthService oAuthService;

    @Value("${jwt.access-token-header-name}")
    private String ACCESS_TOKEN_HEADER_NAME;

    @Value("${jwt.refresh-token-header-name}")
    private String REFRESH_TOKEN_HEADER_NAME;

    /**
     * memberId로 Member 엔티티 조회
     */
    public Member findMemberById(Long memberId) throws BaseException {
        try {
            return memberRepository.findMemberById(memberId)
                    .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
        } catch (Exception e) {
            log.error("findMemberById() : memberRepository.findMemberById() 실행 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * kakaoMemberId로 Member 엔티티 조회
     */
/*    public Member findMemberByKakaoId(Long kakaoMemberId) throws BaseException {
        try {
            return memberRepository.findMemberByKakakoMemberId(kakaoMemberId)
                    .orElse(null);
        } catch (Exception e) {
            log.error("findMemberByKakaoId() : memberRepository.findMemberByKakakoMemberId() 실행 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }*/

    /**
     * 회원가입
     */
    @Transactional(rollbackFor = {Exception.class, BaseException.class})
    public LoginResDTO join(HttpServletResponse response, JoinReqDTO reqDTO) throws BaseException {

        try {
            // 사용자이름 중복 검사
            if (isExistUsername(reqDTO.getUsername())) {
                throw new BaseException(ALREADY_EXIST_USERNAME);
            }

            // 전화번호 중복 검사
            if (isExistPhoneNumber(reqDTO.getPhoneNumber())) {
                throw new BaseException(ALREADY_EXIST_PHONE_NUMBER);
            }

            Long kakaoMemberId = null;
            String password = null;
            // 카카오 로그인인 경우
            if (reqDTO.getLoginType().equals(LoginType.KAKAO.name())){
                kakaoMemberId = oAuthService.getKakaoMemberId(reqDTO.getKakaoAccessToken());
            }
            // 일반 로그인인 경우
            else{
                password = passwordEncoder.encode(reqDTO.getPassword());
            }

            // 멤버 엔티티 생성
            Member member = Member.createMember(
                    LoginType.valueOf(reqDTO.getLoginType()),
                    kakaoMemberId,
                    password,
                    reqDTO.getName(),
                    reqDTO.getUsername(),
                    reqDTO.getPhoneNumber(),
                    reqDTO.getBirthDate());
            save(member);


            // access token, refresh token 생성해서 헤더에 담기
            return loginService.afterLoginSuccess(response, member, reqDTO.getDeviceTokenValue());

        } catch (BaseException e) {
            throw e;
        }
    }

    /**
     * phoneNumber 존재 여부 검사
     */
    public boolean isExistPhoneNumber(String phoneNumber) throws BaseException {
        try {
            return memberRepository.existsByPhoneNumber(phoneNumber);
        } catch (Exception e) {
            log.error("isExistPhoneNumber() : memberRepository.existsByPhoneNumber() 실행 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * username 존재 여부 검사
     */
    public boolean isExistUsername(String username) throws BaseException {
        try {
            return memberRepository.existsByUsername(username);
        } catch (Exception e) {
            log.error("isExistUsername() : memberRepository.existsByUsername() 실행 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * member 엔티티 저장
     */
    public void save(Member member) throws BaseException {
        try {
            memberRepository.save(member);
        } catch (Exception e) {
            log.error("save() : memberRepository.save(member) 함수 실행 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 해당 휴대폰 번호로 인증번호 발송
     */
    public void sendAuthNum(String phoneNumber) throws BaseException {
        try{
            // 이메일 중복 검사
            if(isExistPhoneNumber(phoneNumber)){
                throw new BaseException(ALREADY_EXIST_PHONE_NUMBER);
            }

            // 인증번호 전송
            String authNum = createRandomAuthNum(phoneNumber);
            //smsService.smsSend();

            // 인증번호 Redis에 저장
            redisService.setSignupAuthNumInRedis(phoneNumber, authNum);
        }
        catch (BaseException e){
            throw e;
        }
    }

    /**
     * 6자리 랜덤 인증번호 생성
     */
    public String createRandomAuthNum(String phoneNumber){
        String authNum = "";
        random.setSeed(System.currentTimeMillis() + Long.valueOf(phoneNumber));

        for(int i = 0; i < 6; i++) {
            authNum += String.valueOf(random.nextInt(10));
        }

        return authNum;
    }

    /**
     * 휴대폰 인증번호 검증
     */
    public void checkAuthNum(String phoneNumber, String authNum) throws BaseException{
        try{
            // 이메일 중복 검사
            if(isExistPhoneNumber(phoneNumber)){
                throw new BaseException(ALREADY_EXIST_PHONE_NUMBER);
            }

            boolean result = redisService.compareSignupAuthNumInRedis(phoneNumber, authNum);
            if (result == false){
                throw new BaseException(INVALID_AUTH_NUM);
            }
        }
        catch (BaseException e){
            throw e;
        }
    }
}
