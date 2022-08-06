package com.garit.instagram.service;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.domain.deviceToken.DeviceToken;
import com.garit.instagram.domain.member.Member;
import com.garit.instagram.domain.member.dto.LoginResDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {Exception.class, BaseException.class})
public class LoginService {

    @Value("${jwt.access-token-header-name}")
    private String ACCESS_TOKEN_HEADER_NAME;

    @Value("${jwt.refresh-token-header-name}")
    private String REFRESH_TOKEN_HEADER_NAME;

    private final DeviceTokenService deviceTokenService;
    private final JwtService jwtService;

    public LoginResDTO afterLoginSuccess(HttpServletResponse response, Member loginMember, String deviceTokenValue) throws BaseException {
        try {
            // request로 받은 device token을 DB에 저장하기
            deviceTokenService.createDeviceToken(loginMember, deviceTokenValue);

            // access token, refresh token 생성해서 헤더에 담기
            DeviceToken deviceToken = deviceTokenService.findDeviceToken(loginMember, deviceTokenValue);
            String jwtAccessToken = jwtService.createJwtAccessToken(loginMember.getId().toString(), loginMember.getRole());
            String jwtRefreshToken = jwtService.createJwtRefreshToken(deviceToken.getId().toString());

            response.addHeader(ACCESS_TOKEN_HEADER_NAME, "Bearer " + jwtAccessToken);
            response.addHeader(REFRESH_TOKEN_HEADER_NAME, "Bearer " + jwtRefreshToken);

            // 성공 메시지 리턴하기
            return LoginResDTO.builder()
                    .memberId(loginMember.getId())
                    .loginType(loginMember.getLoginType().name())
                    .role(loginMember.getRole().name())
                    .username(loginMember.getUsername())
                    .name(loginMember.getName())
                    .phoneNumber(loginMember.getPhoneNumber())
                    .email(loginMember.getEmail())
                    .birthDate(loginMember.getBirthDate())
                    .age(loginMember.getAge())
                    .profilePhoto(loginMember.getProfilePhoto())
                    .website(loginMember.getWebsite())
                    .introduce(loginMember.getIntroduce())
                    .openStatus(loginMember.getOpenStatus().name())
                    .memberStatus(loginMember.getMemberStatus().name())
                    .build();
        }
        catch (BaseException e){
            throw e;
        }
    }
}
