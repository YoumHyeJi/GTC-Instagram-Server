package com.garit.instagram.domain.member;

import com.garit.instagram.domain.base.BaseEntity;
import com.garit.instagram.domain.base.Status;
import com.garit.instagram.domain.member.LoginType;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "member_tb")
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "login_type")
    @NotNull
    private LoginType loginType;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private MemberRole role;

    private String password;

    private String name;

    @NotNull
    @Column(unique = true, length = 50)
    private String username;

    @Column(name = "phone_number", unique = true, length = 50)
    private String phoneNumber;

    @Column(unique = true, length = 50)
    private String email;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    private Integer age;

    @Column(name = "profile_photo")
    private String profilePhoto;

    private String website;

    private String introduce;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "open_status")
    @NotNull
    private OpenStatus openStatus;

    @Column(name = "name_update_date")
    private LocalDateTime nameUpdateDate;

    @Column(name = "name_update_count")
    private Integer nameUpdateCount;

    @Column(name = "username_update_date")
    private LocalDateTime usernameUpdateDate;

    @Column(name = "username_update_count")
    private Integer usernameUpdateCount;

    @Column(name = "login_date")
    @NotNull
    private LocalDateTime loginDate;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "member_status")
    @NotNull
    private MemberStatus memberStatus;

    @Transient
    private String deviceTokenValue;

    public void setDeviceTokenValue(String deviceTokenValue) {
        this.deviceTokenValue = deviceTokenValue;
    }

    /**
     * 생성 메서드
     */
    public static Member createMember(LoginType loginType, String password, String name, String username, String phoneNumber, LocalDate birthDate){
        Member member = new Member();
        member.loginType = loginType;
        member.role = MemberRole.ROLE_MEMBER;
        member.password = password;
        member.name = name;
        member.username = username;
        member.phoneNumber = phoneNumber;
        member.birthDate = birthDate;
        member.age = calculateAge(birthDate);
        member.openStatus = OpenStatus.PUBLIC;
        member.nameUpdateCount = 0;
        member.usernameUpdateCount = 0;
        member.loginDate = LocalDateTime.now();
        member.memberStatus = MemberStatus.ACTIVE;
        member.status = Status.VALID;
        member.createDate = LocalDateTime.now();
        member.updateDate = LocalDateTime.now();

        return member;
    }

    public static Integer calculateAge(LocalDate birthDate){
        return LocalDate.now().getYear() - birthDate.getYear() + 1;
    }
}
