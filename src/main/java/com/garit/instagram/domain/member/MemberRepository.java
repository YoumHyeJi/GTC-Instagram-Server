package com.garit.instagram.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {


    Optional<Member> findMemberByUsername(String username);
    Optional<Member> findMemberById(Long memberId);
    Optional<Member> findMemberByKakakoMemberId(Long kakaoMemberId);

    boolean existsByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);
}
