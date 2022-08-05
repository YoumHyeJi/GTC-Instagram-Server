package com.garit.instagram.domain.follow;

import com.garit.instagram.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowed(Member follower, Member followed);
    boolean existsByFollowerAndFollowedAndFollowStatus(Member follower, Member followed, FollowStatus followStatus);

    Optional<Follow> findFollowByFollowerAndFollowed(Member follower, Member followed);


    /**
     * 1. 팔로워 조회 = targetMember를 팔로우 하는 사람들
     */
    @Query("select f from Follow f " +
            "join fetch f.follower " +
            "where f.followed = :followed " +
            "and f.followStatus = :followStatus " +
            "and f.id < :lastFollowId " +
            "order by f.createDate desc")
    Slice<Follow> getFollowerMember(@Param("followed") Member followed, @Param("followStatus") FollowStatus followStatus, @Param("lastFollowId") Long lastFollowId, Pageable pageable);

    /**
     * 2. 팔로잉 조회 = targetMember가 팔로우 하는 사람들
     */
    @Query("select f from Follow f " +
            "join fetch f.followed " +
            "where f.follower = :follower " +
            "and f.followStatus = :followStatus " +
            "and f.id < :lastFollowId " +
            "order by f.createDate desc")
    Slice<Follow> getFollowedMember(@Param("follower") Member follower, @Param("followStatus") FollowStatus followStatus, @Param("lastFollowId") Long lastFollowId, Pageable pageable);


    /**
     * 3. 팔로우 요청 조회 = 내게 팔로우 요청을 보낸 사람들
     */

}
