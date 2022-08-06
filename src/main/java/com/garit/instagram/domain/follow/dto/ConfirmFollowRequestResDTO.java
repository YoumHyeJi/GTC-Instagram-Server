package com.garit.instagram.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfirmFollowRequestResDTO {
    private Long followId;
    private String followStatus;
    private String message;
}
