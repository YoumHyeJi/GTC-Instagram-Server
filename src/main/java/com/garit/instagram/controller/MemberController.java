package com.garit.instagram.controller;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.config.base.BaseResponse;
import com.garit.instagram.config.base.BaseResponseStatus;
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

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/role-guest/api/join")
    public BaseResponse<LoginResDTO> join(HttpServletResponse response,
                                          @Valid @RequestBody JoinReqDTO reqDTO,
                                          BindingResult br){
        if (br.hasErrors()){
            String errorName = br.getAllErrors().get(0).getDefaultMessage();
            return new BaseResponse<>(BaseResponseStatus.of(errorName));
        }

        try{
            return new BaseResponse<>(memberService.join(response, reqDTO));
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }

    }

}
