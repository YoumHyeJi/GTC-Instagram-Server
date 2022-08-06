package com.garit.instagram.domain.post.dto;

import com.garit.instagram.domain.postAttachment.dto.PostAttachmentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePostReqDTO {

    @Size(max=1000, message = "OVER_POST_CONTENT_MAX_SIZE")
    private String postContent;

    @NotNull(message = "NOT_EXIST_POST_ATTACHMENT_LIST")
    @Size(min = 1, max = 5, message = "OVER_POST_ATTACHMENT_LIST_SIZE")
    @Valid
    private List<PostAttachmentDTO> postAttachmentList = new ArrayList<>();

}
