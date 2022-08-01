package com.garit.instagram.domain.comment;

import com.garit.instagram.domain.base.BaseEntity;
import com.garit.instagram.domain.member.Member;
import com.garit.instagram.domain.post.Post;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "comment_tb")
public class Comment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_member_id")
    @NotNull
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_post_id")
    @NotNull
    private Post post;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_comment_id_parent")
    @NotNull
    private Comment parentComment;

    // 댓글 or 답글 여부
    @Enumerated(value = STRING)
    @NotNull
    @Column(name = "comment_type")
    private CommentType commentType;

    @NotNull
    private String content;

    @Enumerated(value = STRING)
    @NotNull
    @Column(name = "comment_status")
    private CommentStatus commentStatus;
}
