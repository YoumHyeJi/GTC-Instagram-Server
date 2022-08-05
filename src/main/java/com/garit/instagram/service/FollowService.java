package com.garit.instagram.service;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.domain.follow.Follow;
import com.garit.instagram.domain.follow.FollowCategory;
import com.garit.instagram.domain.follow.FollowRepository;
import com.garit.instagram.domain.follow.FollowStatus;
import com.garit.instagram.domain.follow.dto.ChangeFollowResDTO;
import com.garit.instagram.domain.follow.dto.FollowMemberDTO;
import com.garit.instagram.domain.follow.dto.GetFollowMemberResDTO;
import com.garit.instagram.domain.member.Member;
import com.garit.instagram.domain.member.OpenStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

import static com.garit.instagram.config.base.BaseResponseStatus.*;
import static com.garit.instagram.domain.follow.FollowStatus.*;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional(rollbackFor = {Exception.class, BaseException.class})
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberService memberService;

    private final int PAGE_SIZE = 3;

    /**
     * 팔로우 상태 변경시키기
     */
    public ChangeFollowResDTO changeFollowStatus(Long myMemberId, Long targetMemberId) throws BaseException {
        try {
            Member myMember = memberService.findMemberById(myMemberId);
            Member targetMember = memberService.findMemberById(targetMemberId);
            ChangeFollowResDTO result;

            // 1. 이미 존재하는 Follow 관계인 경우
            if (isExistFollowerAndFollowed(myMember, targetMember)) {
                Follow follow = findFollowByFollowerAndFollowed(myMember, targetMember);

                // 1-1. 팔로우 상태가 WAIT이거나 ACTIVE인 경우, 팔로우 상태를 INACTIVE로 변경
                if (follow.getFollowStatus() == WAIT || follow.getFollowStatus() == ACTIVE) {
                    follow.changeFollowStatus(INACTIVE);

                    result = ChangeFollowResDTO.builder()
                            .followStatus(INACTIVE)
                            .message("[팔로우 (요청) 취소]를 성공적으로 수행했습니다.")
                            .build();
                }
                // 1-2. 팔로우 상태가 INACTIVE인 경우
                else {
                    // 1-2-1. 상대방이 공개 계정인 경우, 팔로우 상태를 ACTIVE 로 변경
                    if (targetMember.getOpenStatus() == OpenStatus.PUBLIC) {
                        follow.changeFollowStatus(ACTIVE);
                        result = ChangeFollowResDTO.builder()
                                .followStatus(ACTIVE)
                                .message("[팔로우]를 성공적으로 수행했습니다.")
                                .build();
                    }
                    // 1-2-2. 상대방이 비공개 계정인 경우, 팔로우 상태를 WAIT 로 변경
                    else {
                        follow.changeFollowStatus(WAIT);
                        result = ChangeFollowResDTO.builder()
                                .followStatus(WAIT)
                                .message("[팔로우 요청]을 성공적으로 수행했습니다.")
                                .build();
                    }
                }

                save(follow);


            }
            // 2. 아직 존재하지 않는 Follow 관계인 경우
            else {
                Follow follow = Follow.createFollow(myMember, targetMember);

                // 2-1. 상대방이 공개 계정인 경우, 팔로우 상태를 ACTIVE로 변경
                if (targetMember.getOpenStatus() == OpenStatus.PUBLIC) {
                    follow.changeFollowStatus(ACTIVE);
                }
                save(follow);

                result = ChangeFollowResDTO.builder()
                        .followStatus(ACTIVE)
                        .message("[팔로우]를 성공적으로 수행했습니다.")
                        .build();
            }

            return result;
        } catch (BaseException e) {
            throw e;
        }
    }

    /**
     * Follower, Followed 조합으로 Follow 엔티티가 존재하는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isExistFollowerAndFollowed(Member follower, Member followed) throws BaseException {
        try {
            return followRepository.existsByFollowerAndFollowed(follower, followed);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("isExistFollowerAndFollowed() : followRepository.existsByFollowerAndFollowed() 실행 중 데이터베이스 에러 발생");
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * Follower, Followed, FollowStatus 조합으로 Follow 엔티티가 존재하는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isExistFollowerAndFollowedAndFollowStatus(Member follower, Member followed, FollowStatus followStatus) throws BaseException {
        try {
            return followRepository.existsByFollowerAndFollowedAndFollowStatus(follower, followed, followStatus);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("isExistFollowerAndFollowedAndFollowStatus() : followRepository.existsByFollowerAndFollowedAndFollowStatus() 실행 중 데이터베이스 에러 발생");
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * Follow 엔티티 저장
     */
    public void save(Follow follow) throws BaseException {
        try {
            followRepository.save(follow);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("save() : followRepository.save() 실행 중 데이터베이스 에러 발생");
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * Follower, Followed 조합으로 Follow 엔티티 조회
     */
    @Transactional(readOnly = true)
    public Follow findFollowByFollowerAndFollowed(Member follower, Member followed) throws BaseException {
        try {
            return followRepository.findFollowByFollowerAndFollowed(follower, followed)
                    .orElseThrow(() -> new BaseException(NOT_EXIST_FOLLOW));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("findFollowByFollowerAndFollowed() : followRepository.findFollowByFollowerAndFollowed() 실행 중 데이터베이스 에러 발생");
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 1. 팔로워 조회 = targetMember를 팔로우 하는 사람들 / category = follower
     * 2. 팔로잉 조회 = targetMember가 팔로우 하는 사람들 / category = following
     */
    @Transactional(readOnly = true)
    public GetFollowMemberResDTO getFollowMember(FollowCategory category, Long myMemberId, Long targetMemberId, Long lastFollowId) throws BaseException {

        try {
            Member me = memberService.findMemberById(myMemberId);
            Member targetMember = memberService.findMemberById(targetMemberId);

            // 다른 사람의 팔로워 조회 & 상대방이 비공개 계정일 경우
            if ((myMemberId != targetMemberId) && (targetMember.getOpenStatus() == OpenStatus.PRIVATE)) {
                // 서로 팔로우한 관계 아닌 경우
                if (isExistFollowerAndFollowedAndFollowStatus(me, targetMember, ACTIVE) == false) {
                    throw new BaseException(NOT_HAVE_PERMISSION_TO_READ_FOLLOW);
                }
            }

            // 1. 팔로워 조회인 경우 => targetMember를 팔로우 하는 사람들
            if (category == FollowCategory.follower) {
                return getFollowerMemberResultByPaging(targetMember, ACTIVE, lastFollowId);
            }
            // 2. 팔로잉 조회인 경우 => targetMember가 팔로우 하는 사람들
            else{
                return getFollowedMemberResultByPaging(targetMember, ACTIVE, lastFollowId);
            }

        } catch (BaseException e) {
            throw e;
        }
    }

    /**
     * 1. 팔로워 조회 = targetMember를 팔로우 하는 사람들 (페이징 처리)
     */
    @Transactional(readOnly = true)
    public GetFollowMemberResDTO getFollowerMemberResultByPaging(Member followed, FollowStatus followStatus, Long lastFollowId) throws BaseException {
        try {
            PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

            Slice<FollowMemberDTO> result = followRepository.getFollowerMember(followed, followStatus, lastFollowId, pageRequest)
                    .map((Function<Follow, FollowMemberDTO>)
                            f -> FollowMemberDTO.builder()
                                    .followId(f.getId())
                                    .followStatus(f.getFollowStatus().name())
                                    .memberId(f.getFollower().getId())
                                    .name(f.getFollower().getName())
                                    .username(f.getFollower().getUsername())
                                    .profilePhoto(f.getFollower().getProfilePhoto())
                                    .build());

            return GetFollowMemberResDTO.builder()
                    .hasNext(result.hasNext())
                    .followMemberList(result.getContent())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("");
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 2. 팔로잉 조회 = targetMember가 팔로우 하는 사람들 (페이징 처리)
     */
    @Transactional(readOnly = true)
    public GetFollowMemberResDTO getFollowedMemberResultByPaging(Member follower, FollowStatus followStatus, Long lastFollowId) throws BaseException {
        try {
            PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE);

            Slice<FollowMemberDTO> result = followRepository.getFollowedMember(follower, followStatus, lastFollowId, pageRequest)
                    .map((Function<Follow, FollowMemberDTO>)
                            f -> FollowMemberDTO.builder()
                                    .followId(f.getId())
                                    .followStatus(f.getFollowStatus().name())
                                    .memberId(f.getFollowed().getId())
                                    .name(f.getFollowed().getName())
                                    .username(f.getFollowed().getUsername())
                                    .profilePhoto(f.getFollowed().getProfilePhoto())
                                    .build());

            return GetFollowMemberResDTO.builder()
                    .hasNext(result.hasNext())
                    .followMemberList(result.getContent())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("");
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
