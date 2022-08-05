package com.garit.instagram.domain.follow.dto;

import com.garit.instagram.domain.follow.FollowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeFollowResDTO {

    private FollowStatus followStatus;
    private String message;
}
