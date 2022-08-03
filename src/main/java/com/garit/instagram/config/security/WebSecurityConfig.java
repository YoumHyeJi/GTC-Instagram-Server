package com.garit.instagram.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garit.instagram.config.security.filter.JwtAuthenticationFilter;
import com.garit.instagram.config.security.filter.JwtAuthorizationFilter;
import com.garit.instagram.service.DeviceTokenService;
import com.garit.instagram.service.HttpResponseService;
import com.garit.instagram.service.JwtService;
import com.garit.instagram.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SpringSecurity 사용을 위한 어노테이션
 * => 기본적으로 CSRF 활성화, @Configuration 어노테이션(스프링 설정 클래스를 선언하는 어노테이션)이 포함됨
 * => SpringSecurity란, Spring기반의 애플리케이션의 보안(인증, 권한, 인가 등)을 담당하는 Spring 하위 프레임워크
 */
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
public class WebSecurityConfig{

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    private final ObjectMapper objectMapper;
    private final HttpResponseService httpResponseService;
    private final JwtService jwtService;
    private final MemberService memberService;
    private final DeviceTokenService deviceTokenService;

    /**
     * SpringSecurity 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("WebSecurityConfig.filterChain() 호출");
        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));
        // REST API 서버는 stateless하게 개발하기 때문에 사용자 정보를 Session에 저장 안함
        // jwt 토큰을 Cookie에 저장하지 않는다면, CSRF에 어느정도는 안전.
        http
                .csrf().disable()   // csrf 보안 토큰 disable 처리

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)     // 토큰 기반 인증이므로 세션 역시 사용하지 않음
                .and()

                .formLogin().disable()
                .httpBasic().disable()      // rest api 만을 고려하여 기본 설정은 해제
                .addFilter(new JwtAuthenticationFilter(authenticationManager, httpResponseService, objectMapper, jwtService, deviceTokenService))
                .addFilter(new JwtAuthorizationFilter(authenticationManager, httpResponseService, jwtService, memberService))

                .authorizeRequests()    // 요청에 대한 사용권한 체크
                .antMatchers("/role-member/api/**").access("hasRole('ROLE_MEMBER') or hasRole('ROLE_ADMIN')")
                .antMatchers("/role-admin/api/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();

        return http.build();
    }


}
