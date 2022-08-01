package com.garit.instagram.domain.favorite;

import com.garit.instagram.domain.base.BaseEntity;
import com.garit.instagram.domain.member.Member;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")

@Entity
@Getter
@Table(name = "favorite_tb",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "member_post_fav_unique_constraint",
                        columnNames = {"fk_member_id", "fk_post_id"}
                ),
                @UniqueConstraint(
                        name = "member_comment_fav_unique_constraint",
                        columnNames = {"fk_member_id", "fk_comment_id"}
                )

        }
)
public abstract class Favorite extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "favorite_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_member_id")
    @NotNull
    private Member member;

    @Enumerated(value = STRING)
    @NotNull
    @Column(name = "favorite_status")
    private FavoriteStatus favoriteStatus;
}
