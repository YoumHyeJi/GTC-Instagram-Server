package com.garit.instagram.config.security.filter;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.config.security.principalDetails.PrincipalDetails;
import com.garit.instagram.domain.member.Member;
import com.garit.instagram.service.HttpResponseService;
import com.garit.instagram.service.JwtService;
import com.garit.instagram.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.garit.instagram.config.base.BaseResponseStatus.*;

@Log4j2
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final String MEMBER_ID_HEADER_NAME = "Member-Id";

    private final AuthenticationManager authenticationManager;
    private final HttpResponseService httpResponseService;
    private final JwtService jwtService;
    private final MemberService memberService;

    @Autowired
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, HttpResponseService httpResponseService, JwtService jwtService, MemberService memberService) {
        super(authenticationManager);
        this.authenticationManager = authenticationManager;
        this.httpResponseService = httpResponseService;
        this.jwtService = jwtService;
        this.memberService = memberService;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        /**
         * guest 권한이 필요한 요청인 경우
         */
        if (request.getRequestURI().startsWith("/role-guest/api") || request.getRequestURI().startsWith("/favicon.ico")){
            log.info("guest 권한이 필요한 주소가 요청이 됨 => " + request.getRequestURI());
            chain.doFilter(request, response);
            return;
        }

        /**
         * member 혹은 admin 권한이 필요한 요청인 경우
         */
        log.info("member 혹은 admin 권한이 필요한 주소가 요청이 됨 => " + request.getRequestURI());
        // 헤더에서 userId와 jwtAccessToken 받아오기
        String memberId = request.getHeader(MEMBER_ID_HEADER_NAME);
        String jwtAccessToken = jwtService.getJwtAccessToken(request);

        if (checkMemberIdInHeader(response, memberId) && checkJwtAccessTokenInHeader(response, jwtAccessToken)){
            Member member = findMember(response, memberId, jwtAccessToken);

            if(member != null){
                PrincipalDetails principalDetails = new PrincipalDetails(member);

                // JWT 토큰 서명을 통해서, 서명이 정상이면 Authentication 객체를 만들어준다.
                Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

                // 강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장.
                SecurityContextHolder.getContext().setAuthentication(authentication);

                chain.doFilter(request, response);
            }
        }
    }

    // header에서 userId를 뽑아, 유효한 userId인지 검사
    private boolean checkMemberIdInHeader(HttpServletResponse response, String memberId) throws BaseException {
        if (memberId == null) {
            log.error("memberId를 헤더에 입력해주세요");
            httpResponseService.errorRespond(response, NOT_EXIST_MEMBER_ID_IN_HEADER);
            return false;
        }

        try {
            Long.valueOf(memberId);
        } catch (NumberFormatException e) {
            log.error("유효하지 않은 memberId입니다. 숫자형태로 입력해주세요.");
            httpResponseService.errorRespond(response, INVALID_MEMBER_ID);
            return false;
        }

        return true;
    }

    // header에서 jwtAccessToken을 뽑아, 유효한 jwtAccessToken인지 검사
    private boolean checkJwtAccessTokenInHeader(HttpServletResponse response, String jwtAccessToken) throws BaseException {
        if(jwtAccessToken == null){
            log.error("access token을 헤더에 입력해주세요.");
            httpResponseService.errorRespond(response, NOT_EXIST_ACCESS_TOKEN_IN_HEADER);
            return false;
        }

        if (jwtService.validateToken(jwtAccessToken) == false){
            log.error("만료된 access token입니다.");
            httpResponseService.errorRespond(response, INVALID_ACCESS_TOKEN);
            return false;
        }

        return true;
    }

    private Member findMember(HttpServletResponse response, String memberIdInHeader, String jwtAccessToken) throws BaseException {
        try {
            String memberIdInJWT = jwtService.getMemberIdInJwtAccessToken(jwtAccessToken);

            if (memberIdInJWT == null) {
                log.error("access token에 subject가 존재하지 않습니다.");
                httpResponseService.errorRespond(response, NOT_EXIST_ACCESS_TOKEN_SUBJECT);
                return null;
            }

            if (!memberIdInJWT.equals(memberIdInHeader)) {
                log.error("accessToken의 memberId와 header의 memberId가 일치하지 않습니다.");
                httpResponseService.errorRespond(response, NOT_EQUAL_MEMBER_ID);
                return null;
            }

            return memberService.findMemberById(Long.valueOf(memberIdInHeader));
        }
        catch (BaseException e){
            httpResponseService.errorRespond(response, e.getStatus());
            return null;
        }
    }
}
