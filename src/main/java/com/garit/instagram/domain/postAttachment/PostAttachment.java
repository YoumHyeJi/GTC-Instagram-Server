package com.garit.instagram.domain.postAttachment;

import com.garit.instagram.domain.base.BaseEntity;
import com.garit.instagram.domain.base.Status;
import com.garit.instagram.domain.post.Post;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Table(name = "post_attachment_tb")
public class PostAttachment extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "post_attachment_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fk_post_id")
    @NotNull
    private Post post;

    // 사진 or 동영상 여부
    @Enumerated(value = STRING)
    @NotNull
    @Column(name = "post_attachment_type")
    private PostAttachmentType postAttachmentType;

    @NotNull
    private String url;

    @Enumerated(value = STRING)
    @NotNull
    @Column(name = "post_attachment_status")
    private PostAttachmentStatus postAttachmentStatus;

    /**
     * 생성 메서드
     */
    public static PostAttachment createPostAttachment(Post post, PostAttachmentType type, String url){
        PostAttachment postAttachment = new PostAttachment();
        postAttachment.post = post;
        postAttachment.postAttachmentType = type;
        postAttachment.url = url;
        postAttachment.postAttachmentStatus = PostAttachmentStatus.ACTIVE;
        postAttachment.status = Status.VALID;
        postAttachment.createDate = LocalDateTime.now();
        postAttachment.updateDate = LocalDateTime.now();

        return postAttachment;
    }
}
