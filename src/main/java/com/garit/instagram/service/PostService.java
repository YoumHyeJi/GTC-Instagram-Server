package com.garit.instagram.service;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.config.base.BaseResponseStatus;
import com.garit.instagram.domain.member.Member;
import com.garit.instagram.domain.post.Post;
import com.garit.instagram.domain.post.PostRepository;
import com.garit.instagram.domain.post.dto.CreatePostReqDTO;
import com.garit.instagram.domain.post.dto.CreatePostResDTO;
import com.garit.instagram.domain.postAttachment.PostAttachment;
import com.garit.instagram.domain.postAttachment.PostAttachmentType;
import com.garit.instagram.domain.postAttachment.dto.PostAttachmentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.garit.instagram.config.base.BaseResponseStatus.DATABASE_ERROR;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {Exception.class, BaseException.class})
@Log4j2
public class PostService {

    private final PostRepository postRepository;
    private final MemberService memberService;
    private final PostAttachmentService postAttachmentService;

    /**
     * 게시물 생성
     */
    public CreatePostResDTO createPost(Long memberId, String postContent, List<PostAttachmentDTO> postAttachmentList) throws BaseException {
        try {
            Member member = memberService.findMemberById(memberId);

            // 게시물 생성
            Post post = Post.createPost(member, postContent);
            save(post);

            // 게시물 첨부파일 생성 (사진)
            for (PostAttachmentDTO postAttachmentDTO : postAttachmentList) {
                PostAttachment postAttachment = PostAttachment.createPostAttachment(
                        post,
                        PostAttachmentType.valueOf(postAttachmentDTO.getPostAttachmentType()),
                        postAttachmentDTO.getPostAttachmentUrl());
                postAttachmentService.save(postAttachment);
            }

            return CreatePostResDTO.builder()
                    .postId(post.getId())
                    .message("성공적으로 게시물을 생성했습니다.")
                    .build();

        } catch (BaseException e) {
            throw e;
        }
    }

    /**
     * Post 엔티티 저장
     */
    public void save(Post post) throws BaseException {
        try {
            postRepository.save(post);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("save() : postRepository.save() 실행 중 데이터베이스 에러 발생");
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
