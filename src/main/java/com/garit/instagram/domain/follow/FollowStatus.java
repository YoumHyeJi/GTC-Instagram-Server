package com.garit.instagram.domain.follow;

public enum FollowStatus {
    WAIT("팔로우 요청/대기 상태"),
    ACTIVE("팔로우 상태"),
    INACTIVE("팔로우 취소/거절 상태");

    private final String name;

    private FollowStatus(String name) {
        this.name = name;
    }
}
