package com.garit.instagram.controller;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.config.base.BaseResponse;
import com.garit.instagram.config.base.BaseResponseStatus;
import com.garit.instagram.domain.member.dto.KakaoLoginReqDTO;
import com.garit.instagram.domain.member.dto.LoginResDTO;
import com.garit.instagram.service.OAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/role-guest/api/oauth")
@Log4j2
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    /**
     * 카카오 callback
     * [GET] /oauth/kakao/callback
     */
    @GetMapping("/kakao")
    public BaseResponse<String> kakaoCallBack(@RequestParam String code) {
        log.info("kakao's code : " + code);
        String kakaoAccessToken = oAuthService.getKakaoAccessToken(code);
        log.info("kakao's accessToken : " + kakaoAccessToken);
        return new BaseResponse<>(kakaoAccessToken);
    }

    /**
     * KAKAO 소셜 로그인
     */
    @PostMapping("/kakao")
    public BaseResponse<LoginResDTO> kakaoLogin(HttpServletResponse response,
                                                @Valid @RequestBody KakaoLoginReqDTO reqDTO,
                                                BindingResult br) {
        if (br.hasErrors()) {
            String errorName = br.getAllErrors().get(0).getDefaultMessage();
            return new BaseResponse<>(BaseResponseStatus.of(errorName));
        }

        try {
            return new BaseResponse<>(oAuthService.kakaoLogin(response, reqDTO.getKakaoAccessToken(), reqDTO.getDeviceTokenValue()));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }
}
