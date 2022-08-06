package com.garit.instagram.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FollowMemberDTO {

    private Long followId;          // 팔로우 식별자
    private String followStatus;    // 팔로우 상태

    private Long memberId;          // 멤버 식별자
    private String username;        // 사용자 이름
    private String name;            // 이름
    private String profilePhoto;    // 프로필 이미지 url
}
