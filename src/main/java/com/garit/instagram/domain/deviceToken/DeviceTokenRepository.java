package com.garit.instagram.domain.deviceToken;

import com.garit.instagram.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {

    // 기존 디바이스 토큰이 존재하는지 확인
    boolean existsDeviceTokenByMemberAndDeviceTokenValue(Member member, String DeviceTokenValue);

    Optional<DeviceToken> findDeviceTokenByMemberAndDeviceTokenValue(Member member, String DeviceTokenValue);
}
