package com.garit.instagram.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.garit.instagram.domain.member.LoginType;
import com.garit.instagram.domain.member.MemberRole;
import com.garit.instagram.domain.member.MemberStatus;
import com.garit.instagram.domain.member.OpenStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResDTO {

    private Long memberId;
    private String loginType;
    private String role;
    private String username;
    private String name;
    private String phoneNumber;
    private String email;

    // 자바 클래스 -> json 으로 serialize할 때 문제가 생기므로 추가
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    private Integer age;
    private String profilePhoto;
    private String website;
    private String introduce;
    private String openStatus;
    private String memberStatus;

}
