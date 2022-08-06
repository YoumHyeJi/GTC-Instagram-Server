package com.garit.instagram.controller;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.config.base.BaseResponse;
import com.garit.instagram.config.base.BaseResponseStatus;
import com.garit.instagram.domain.post.dto.CreatePostReqDTO;
import com.garit.instagram.domain.post.dto.CreatePostResDTO;
import com.garit.instagram.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/role-member/api/post")
public class PostControlloer {

    @Value("${jwt.member-id-header-name}")
    private String MEMBER_ID_HEADER_NAME;

    private final PostService postService;

    @PostMapping("")
    public BaseResponse<CreatePostResDTO> createPost(HttpServletRequest request,
                                                     @Valid @RequestBody CreatePostReqDTO reqDTO,
                                                     BindingResult br){
        if(br.hasErrors()){
            String errorName = br.getAllErrors().get(0).getDefaultMessage();
            return new BaseResponse<>(BaseResponseStatus.of(errorName));
        }

        try{
            Long memberId = Long.valueOf(request.getHeader(MEMBER_ID_HEADER_NAME));

            return new BaseResponse<>(postService.createPost(memberId, reqDTO.getPostContent(), reqDTO.getPostAttachmentList()));

        } catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
}
