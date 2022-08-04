package com.garit.instagram.domain.member.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JoinReqDTO {

    @NotBlank(message = "NOT_EXIST_JOIN_REQ_DTO")
    @Pattern(regexp = "^(BASIC|KAKAO)$", message = "INVALID_LOGIN_TYPE_FORM")
    private String loginType;

    private String kakaoAccessToken;

    @NotBlank(message = "NOT_EXIST_JOIN_REQ_DTO")
    @Pattern(regexp = "^[0-9]{3,11}$", message = "INVALID_PHONE_NUMBER_FORM")
    private String phoneNumber;

    @NotBlank(message = "NOT_EXIST_JOIN_REQ_DTO")
    @Size(min = 1, max = 20, message = "INVALID_NAME_FORM")
    private String name;

    @NotBlank(message = "NOT_EXIST_JOIN_REQ_DTO")
    @Pattern(regexp = "^[a-z0-9_.]{3,20}$", message = "INVALID_USERNAME_FORM")
    private String username;

    @Pattern(regexp = "^(?=.*[@$!%*#?&])[A-Za-z0-9@$!%*#?&]{6,20}$", message = "INVALID_PWD_FORM")
    private String password;

    @NotNull(message = "NOT_EXIST_JOIN_REQ_DTO")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @NotBlank(message = "NOT_EXIST_JOIN_REQ_DTO")
    private String deviceTokenValue;
}
