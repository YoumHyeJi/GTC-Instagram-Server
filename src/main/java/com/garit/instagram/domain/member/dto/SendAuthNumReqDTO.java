package com.garit.instagram.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendAuthNumReqDTO {

    @NotBlank(message = "NOT_EXIST_PHONE_NUMBER")
    @Pattern(regexp = "^[0-9]{3,11}$", message = "INVALID_PHONE_NUMBER_FORM")
    private String phoneNumber;
}
