package com.garit.instagram.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeFollowReqDTO {

    @NotNull(message = "NOT_EXIST_TARGET_MEMBER_ID")
    private Long targetMemberId;
}
