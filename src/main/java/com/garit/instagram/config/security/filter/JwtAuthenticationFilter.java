package com.garit.instagram.config.security.filter;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.config.security.principalDetails.PrincipalDetails;
import com.garit.instagram.domain.deviceToken.DeviceToken;
import com.garit.instagram.domain.member.Member;
import com.garit.instagram.domain.member.dto.LoginReqDTO;
import com.garit.instagram.domain.member.dto.LoginResDTO;
import com.garit.instagram.service.DeviceTokenService;
import com.garit.instagram.service.HttpResponseService;
import com.garit.instagram.service.JwtService;
import com.garit.instagram.service.LoginService;
import com.garit.instagram.utils.ValidationRegex;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.garit.instagram.config.base.BaseResponseStatus.*;

/**
 * body에 email과 password를 담아 POST /login 요청을 하면,
 * 스프링 시큐리티의 UsernamePasswordAuthenticationFilter가 동작함.
 */
@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final String ACCESS_TOKEN_HEADER_NAME = "Jwt-Access-Token";
    private static final String REFRESH_TOKEN_HEADER_NAME = "Jwt-Refresh-Token";

    // AuthenticationManager를 통해 로그인 진행
    private final AuthenticationManager authenticationManager;
    private final HttpResponseService httpResponseService;
    private final ObjectMapper objectMapper;
    private final LoginService loginService;

    // /login 요청이 오면, 로그인 시도를 위해서 자동으로 실행되는 함수

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        try {
            log.info("attemptAuthentication() : 로그인 시도 중");

            // 1. body에 담겨있는 JSON을 파싱해서, id, password, deviceToken 얻기
            LoginReqDTO loginReqDTO = objectMapper.readValue(request.getInputStream(), LoginReqDTO.class);

            if (validateLoginReqDTO(response, loginReqDTO) == false) {
                log.error("로그인 시도 중, request validation 실패");
                return null;
            }

            log.info("로그인 시도 사용자 이름 : " + loginReqDTO.getUsername());

            // 2. UsernamePasswordAuthenticationToken 생성하기
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginReqDTO.getUsername(), loginReqDTO.getPassword());

            // AuthenticationManger로 로그인 시도 => PrincipalDetailsService의 loadUserByUsername() 함수가 자동으로 실행됨
            // 로그인 시도가 정상적으로 완료되면 Authentication 객체 반환, 비정상적으로 완료되면 AuthenticationException 발생
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            log.info("로그인 성공 => 입력으로 받은 Device Token을 User에 저장중");
            ((PrincipalDetails) authentication.getPrincipal()).getMember().setDeviceTokenValue(loginReqDTO.getDeviceTokenValue());

            // authentication 객체가 session 영역에 저장을 해야되야하고, 그 방법이 return!
            return authentication;

        } catch (IOException e) {
            try {
                log.error("로그인 시도 중에 IOException 발생");
                httpResponseService.errorRespond(response, IO_EXCEPTION);
                e.printStackTrace();
            } catch (BaseException ex) {
                ex.printStackTrace();
            }
        } catch (AuthenticationException e) {
            try {
                log.error("로그인 시도 중에 AuthenticationException 발생");
                httpResponseService.errorRespond(response, INVALID_USERNAME_OR_PWD);
                e.printStackTrace();
            } catch (BaseException ex) {
                ex.printStackTrace();
            }
        } catch (BaseException e){
            try {
                httpResponseService.errorRespond(response, e.getStatus());
            } catch (BaseException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        try {
            log.info("로그인 완료, 토큰 반환 중");

            PrincipalDetails principalDetails = (PrincipalDetails) authResult.getPrincipal();
            Member loginMember = principalDetails.getMember();

            LoginResDTO loginResDTO = loginService.afterLoginSuccess(response, loginMember, loginMember.getDeviceTokenValue());
            httpResponseService.successRespond(response, loginResDTO);
        }
        catch (BaseException e){
            try {
                httpResponseService.errorRespond(response, e.getStatus());
            }
            catch (BaseException ex){
            }
        }
    }

    private boolean validateLoginReqDTO(HttpServletResponse response, LoginReqDTO loginReqDTO) throws BaseException {

        if (loginReqDTO.getUsername() == null || loginReqDTO.getPassword() == null || loginReqDTO.getDeviceTokenValue() == null) {
            log.error("username, password, deviceTokenValue를 모두 입력하지 않았습니다.");
            httpResponseService.errorRespond(response, NOT_EXIST_LOGIN_REQ_DTO);
            return false;
        }

        if (ValidationRegex.isRegexUsername(loginReqDTO.getUsername()) == false) {
            log.error("username 형식이 올바르지 않습니다. (소문자 + 숫자 + '_' + '.'  3~20자)");
            httpResponseService.errorRespond(response, INVALID_USERNAME_FORM);
            return false;
        }

        if (ValidationRegex.isRegexPassword(loginReqDTO.getPassword()) == false) {
            log.error("password 형식이 올바르지 않습니다. (영문 + 숫자 + 특수문자 6~20자 => 특수문자 한 개 이상 포함)");
            httpResponseService.errorRespond(response, INVALID_PWD_FORM);
            return false;
        }

        return true;

    }
}
