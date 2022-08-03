package com.garit.instagram.service;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.domain.deviceToken.DeviceToken;
import com.garit.instagram.domain.member.LoginType;
import com.garit.instagram.domain.member.Member;
import com.garit.instagram.domain.member.MemberRepository;
import com.garit.instagram.domain.member.dto.JoinReqDTO;
import com.garit.instagram.domain.member.dto.LoginResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;

import static com.garit.instagram.config.base.BaseResponseStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Log4j2
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final DeviceTokenService deviceTokenService;

    @Value("${jwt.access-token-header-name}")
    private String ACCESS_TOKEN_HEADER_NAME;

    @Value("${jwt.refresh-token-header-name}")
    private String REFRESH_TOKEN_HEADER_NAME;

    /**
     * memberId로 Member 엔티티 조회
     */
    public Member findMemberById(Long memberId) throws BaseException {
        try {
            return memberRepository.findMemberById(memberId)
                    .orElseThrow(() -> new BaseException(NOT_EXIST_MEMBER));
        } catch (Exception e) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 회원가입
     */
    @Transactional(rollbackFor = {Exception.class, BaseException.class})
    public LoginResDTO join(HttpServletResponse response, JoinReqDTO reqDTO) throws BaseException {

        try {
            // 사용자이름 중복 검사
            if (isExistUsername(reqDTO.getUsername())) {
                throw new BaseException(ALREADY_EXIST_USERNAME);
            }

            // 전화번호 중복 검사
            if (isExistPhoneNumber(reqDTO.getPhoneNumber())) {
                throw new BaseException(ALREADY_EXIST_PHONE_NUMBER);
            }

            // 멤버 엔티티 생성
            Member member = Member.createMember(
                    LoginType.valueOf(reqDTO.getLoginType()),
                    passwordEncoder.encode(reqDTO.getPassword()),
                    reqDTO.getName(),
                    reqDTO.getUsername(),
                    reqDTO.getPhoneNumber(),
                    reqDTO.getBirthDate());
            save(member);

            // access token, refresh token 생성해서 헤더에 담기
            deviceTokenService.createDeviceToken(member, reqDTO.getDeviceTokenValue());
            DeviceToken deviceToken = deviceTokenService.findDeviceToken(member, reqDTO.getDeviceTokenValue());

            String jwtAccessToken = jwtService.createJwtAccessToken(member.getId().toString(), member.getRole());
            String jwtRefreshToken = jwtService.createJwtRefreshToken(deviceToken.getId().toString());

            response.addHeader(ACCESS_TOKEN_HEADER_NAME, "Bearer " + jwtAccessToken);
            response.addHeader(REFRESH_TOKEN_HEADER_NAME, "Bearer " + jwtRefreshToken);

            return LoginResDTO.builder()
                    .memberId(member.getId())
                    .loginType(member.getLoginType().name())
                    .role(member.getRole().name())
                    .username(member.getUsername())
                    .name(member.getName())
                    .phoneNumber(member.getPhoneNumber())
                    .email(member.getEmail())
                    .birthDate(member.getBirthDate())
                    .age(member.getAge())
                    .profilePhoto(member.getProfilePhoto())
                    .website(member.getWebsite())
                    .introduce(member.getIntroduce())
                    .openStatus(member.getOpenStatus().name())
                    .memberStatus(member.getMemberStatus().name())
                    .build();

        } catch (BaseException e) {
            throw e;
        }
    }

    /**
     * phoneNumber 존재 여부 검사
     */
    public boolean isExistPhoneNumber(String phoneNumber) throws BaseException {
        try {
            return memberRepository.existsByPhoneNumber(phoneNumber);
        } catch (Exception e) {
            log.error("isExistPhoneNumber() : memberRepository.existsByPhoneNumber() 실행 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * username 존재 여부 검사
     */
    public boolean isExistUsername(String username) throws BaseException {
        try {
            return memberRepository.existsByUsername(username);
        } catch (Exception e) {
            log.error("isExistUsername() : memberRepository.existsByUsername() 실행 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * member 엔티티 저장
     */
    public void save(Member member) throws BaseException {
        try {
            memberRepository.save(member);
        } catch (Exception e) {
            log.error("save() : memberRepository.save(member) 함수 실행 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
