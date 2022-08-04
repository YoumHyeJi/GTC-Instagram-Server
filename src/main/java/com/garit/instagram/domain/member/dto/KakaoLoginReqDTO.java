package com.garit.instagram.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoLoginReqDTO {

    @NotBlank(message = "NOT_EXIST_KAKAO_ACCESS_TOKEN")
    private String kakaoAccessToken;

    @NotBlank(message = "NOT_EXIST_DEVICE_TOKEN_VALUE")
    private String deviceTokenValue;
}
