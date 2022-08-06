package com.garit.instagram.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmFollowRequestReqDTO {

    @NotNull(message = "NOT_EXIST_FOLLOW_ID")
    private Long followId;

    @NotNull(message = "NOT_EXIST_CONFIRM_FLAG")
    private Boolean confirmFlag;
}
