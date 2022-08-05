package com.garit.instagram.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetFollowMemberResDTO {

    private Boolean hasNext;
    private List<FollowMemberDTO> followMemberList = new ArrayList<>();
}
