package com.garit.instagram.controller;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.config.base.BaseResponse;
import com.garit.instagram.config.base.BaseResponseStatus;
import com.garit.instagram.domain.member.LoginType;
import com.garit.instagram.domain.member.dto.CheckAuthNumReqDTO;
import com.garit.instagram.domain.member.dto.SendAuthNumReqDTO;
import com.garit.instagram.domain.member.dto.JoinReqDTO;
import com.garit.instagram.domain.member.dto.LoginResDTO;
import com.garit.instagram.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import static com.garit.instagram.config.base.BaseResponseStatus.NOT_EXIST_KAKAO_ACCESS_TOKEN_IN_KAKAO_SIGNUP;
import static com.garit.instagram.config.base.BaseResponseStatus.NOT_EXIST_PASSWORD_IN_BASIC_SIGNUP;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 API
     */
    @PostMapping("/role-guest/api/join")
    public BaseResponse<LoginResDTO> join(HttpServletResponse response,
                                          @Valid @RequestBody JoinReqDTO reqDTO,
                                          BindingResult br){
        if (br.hasErrors()){
            String errorName = br.getAllErrors().get(0).getDefaultMessage();
            return new BaseResponse<>(BaseResponseStatus.of(errorName));
        }

        // kakao 소셜 회원가입인 경우, kakaoAccessToken 필수로 입력
        if (reqDTO.getLoginType().equals(LoginType.KAKAO.name())){
            if(reqDTO.getKakaoAccessToken() == null){
                return new BaseResponse<>(NOT_EXIST_KAKAO_ACCESS_TOKEN_IN_KAKAO_SIGNUP);
            }
        }
        // 일반 회원가입인 경우, password 필수로 입력
        else{
            if(reqDTO.getPassword() == null){
                return new BaseResponse<>(NOT_EXIST_PASSWORD_IN_BASIC_SIGNUP);
            }
        }

        try{
            return new BaseResponse<>(memberService.join(response, reqDTO));
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 휴대폰 인증 번호 발급 API
     */
    @PostMapping("/role-guest/api/join/auth/new-num")
    public BaseResponse<String> sendAuthNum(@Valid @RequestBody SendAuthNumReqDTO reqDTO,
                                       BindingResult br){
        if(br.hasErrors()){
            String errorName = br.getAllErrors().get(0).getDefaultMessage();
            return new BaseResponse<>(BaseResponseStatus.of(errorName));
        }

        try{
            memberService.sendAuthNum(reqDTO.getPhoneNumber());
            return new BaseResponse<>("해당 휴대폰 번호로 인증번호를 발송했습니다.");
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }

    /**
     * 휴대폰 인증 번호 검증 API
     */
    @PostMapping("/role-guest/api/join/auth/check-num")
    public BaseResponse<String> checkAuthNum(@Valid @RequestBody CheckAuthNumReqDTO reqDTO,
                                            BindingResult br){
        if(br.hasErrors()){
            String errorName = br.getAllErrors().get(0).getDefaultMessage();
            return new BaseResponse<>(BaseResponseStatus.of(errorName));
        }

        try{
            memberService.checkAuthNum(reqDTO.getPhoneNumber(), reqDTO.getAuthNum());
            return new BaseResponse<>("인증이 완료되었습니다.");
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }


}
