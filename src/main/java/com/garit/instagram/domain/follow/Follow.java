package com.garit.instagram.domain.follow;

import com.garit.instagram.domain.base.BaseEntity;
import com.garit.instagram.domain.member.Member;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "follow_tb",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "followed_follower_unique_constarint",
                        columnNames = {"fk_member_id_followed", "fk_member_id_follower"}
                )
        }
)
public class Follow extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "follow_id")
    private Long id;

    // 팔로우 받은 사람
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_member_id_followed")
    @NotNull
    private Member followed;

    // 팔로우 요청한 사람
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_member_id_follower")
    @NotNull
    private Member follower;

    @Enumerated(value = STRING)
    @Column(name = "follow_status")
    @NotNull
    private FollowStatus followStatus;
}
