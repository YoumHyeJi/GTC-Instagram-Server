package com.garit.instagram.controller;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.config.base.BaseResponse;
import com.garit.instagram.config.base.BaseResponseStatus;
import com.garit.instagram.domain.follow.FollowCategory;
import com.garit.instagram.domain.follow.dto.*;
import com.garit.instagram.service.FollowService;
import com.garit.instagram.utils.ValidationRegex;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.garit.instagram.config.base.BaseResponseStatus.INVALID_FOLLOW_CATEGORY;

@RestController
@RequestMapping("/role-member/api/follow")
@RequiredArgsConstructor
public class FollowController {

    @Value("${jwt.member-id-header-name}")
    private String MEMBER_ID_HEADER_NAME;

    private final FollowService followService;

    @PatchMapping("")
    public BaseResponse<ChangeFollowResDTO> changeFollowStatus(HttpServletRequest request,
                                                               @Valid @RequestBody ChangeFollowReqDTO reqDTO,
                                                               BindingResult br) {
        if (br.hasErrors()) {
            String errorName = br.getAllErrors().get(0).getDefaultMessage();
            return new BaseResponse(BaseResponseStatus.of(errorName));
        }

        String memberId = request.getHeader(MEMBER_ID_HEADER_NAME);

        try {
            return new BaseResponse(followService.changeFollowStatus(Long.valueOf(memberId), reqDTO.getTargetMemberId()));
        } catch (BaseException e) {
            return new BaseResponse(e.getStatus());
        }

    }


    /**
     * 팔로워 & 팔로잉 조회 API
     * 1. 팔로워 조회 = targetMember를 팔로우 하는 사람들 / category = follower
     * 2. 팔로잉 조회 = targetMember가 팔로우 하는 사람들 / category = following
     */
    @GetMapping("")
    public BaseResponse<GetFollowMemberResDTO> getFollowMember(HttpServletRequest request,
                                                               @RequestParam(value = "category") String category,
                                                               @RequestParam(value = "targetMemberId") Long targetMemberId,
                                                               @RequestParam(value = "lastFollowId", required = false) Long lastFollowId) {
        try {
            if (!ValidationRegex.isRegexFollowCategory(category)) {
                return new BaseResponse<>(INVALID_FOLLOW_CATEGORY);
            }

            Long myMemberId = Long.valueOf(request.getHeader(MEMBER_ID_HEADER_NAME));

            if (lastFollowId == null) {
                lastFollowId = Long.MAX_VALUE;
            }

            return new BaseResponse<>(followService.getFollowMember(FollowCategory.valueOf(category), myMemberId, targetMemberId, lastFollowId));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }


    /**
     * 팔로우 요청 조회 API
     * 3. 팔로우 요청 조회 = 내게 팔로우 요청을 보낸 사람들
     */
    @GetMapping("/request")
    public BaseResponse<GetFollowMemberResDTO> getFollowRequestMember(HttpServletRequest request,
                                                                      @RequestParam(value = "lastFollowId", required = false) Long lastFollowId) {
        try{
            Long myMemberId = Long.valueOf(request.getHeader(MEMBER_ID_HEADER_NAME));

            if (lastFollowId == null){
                lastFollowId = Long.MAX_VALUE;
            }

            return new BaseResponse<>(followService.getFollowRequestMember(myMemberId, lastFollowId));
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    /**
     * 팔로우 요청 승인 & 팔로우 요청 거절 API
     */
    @PatchMapping("/request")
    public BaseResponse<ConfirmFollowRequestResDTO> confirmFollowRequest(HttpServletRequest request,
                                                                         @Valid @RequestBody ConfirmFollowRequestReqDTO reqDTO,
                                                                         BindingResult br){
        if (br.hasErrors()){
            String errorName = br.getAllErrors().get(0).getDefaultMessage();
            return new BaseResponse(BaseResponseStatus.of(errorName));
        }

        try{
            Long myMemberId = Long.valueOf(request.getHeader(MEMBER_ID_HEADER_NAME));

            return new BaseResponse<>(followService.confirmFollowRequest(myMemberId, reqDTO.getFollowId(), reqDTO.getConfirmFlag()));
        }
        catch (BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }


}
