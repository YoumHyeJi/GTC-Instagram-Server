package com.garit.instagram.service;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.config.base.BaseResponseStatus;
import com.garit.instagram.domain.postAttachment.PostAttachment;
import com.garit.instagram.domain.postAttachment.PostAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.garit.instagram.config.base.BaseResponseStatus.DATABASE_ERROR;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = {Exception.class, BaseException.class})
@Log4j2
public class PostAttachmentService {

    private final PostAttachmentRepository postAttachmentRepository;

    /**
     * PostAttachment 엔티티 저장
     */
    public void save(PostAttachment postAttachment) throws BaseException{
        try{
            postAttachmentRepository.save(postAttachment);
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("save() : postAttachmentRepository.save() 실행 중 데이터베이스 에러 발생");
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
