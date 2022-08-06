package com.garit.instagram.domain.postAttachment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostAttachmentDTO {

    @NotBlank(message = "NOT_EXIST_POST_ATTACHMENT_URL")
    private String postAttachmentUrl;

    @NotBlank(message = "NOT_EXIST_POST_ATTACHMENT_TYPE")
    @Pattern(regexp = "^(PHOTO|VIDEO)$", message = "INVALID_POST_ATTACHMENT_TYPE")
    private String postAttachmentType;

}
