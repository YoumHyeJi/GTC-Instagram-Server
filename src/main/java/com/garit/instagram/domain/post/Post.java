package com.garit.instagram.domain.post;

import com.garit.instagram.domain.base.BaseEntity;
import com.garit.instagram.domain.base.Status;
import com.garit.instagram.domain.member.Member;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "post_tb")
public class Post extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_member_id")
    @NotNull
    private Member member;

    @Column(length = 1000)
    private String content;

    @Enumerated(value = STRING)
    @Column(name = "post_status")
    @NotNull
    private PostStatus postStatus;

    /**
     * 생성 메서드
     */
    public static Post createPost(Member member, String content){
        Post post = new Post();

        post.member = member;
        post.content = content;
        post.postStatus = PostStatus.ACTIVE;
        post.status = Status.VALID;
        post.createDate = LocalDateTime.now();
        post.updateDate = LocalDateTime.now();

        return post;
    }
}
