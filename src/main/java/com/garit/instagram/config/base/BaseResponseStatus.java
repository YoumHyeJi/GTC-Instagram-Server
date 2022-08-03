package com.garit.instagram.config.base;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 4000 : REDIS, Database 관련 에러
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    REDIS_ERROR(false, 4001, "Redis 연결에 실패했습니다."),
    IO_EXCEPTION(false, 4002, "IOException이 발생했습니다."),


    /**
     * 4100 :  JWT 토큰 관련 에러
     */
    JWT_ERROR(false, 4100, "JWT 관련 코드 실행 중 에러가 발생했습니다."),
    NOT_EXIST_ACCESS_TOKEN_IN_HEADER(false, 4101, "'Bearer '로 시작하는 JWT Access Token을 헤더에 입력해주세요."),
    NOT_EXIST_REFRESH_TOKEN_IN_HEADER(false, 4102, "'Bearer '로 시작하는 JWT Refresh Token을 헤더에 입력해주세요."),
    INVALID_ACCESS_TOKEN(false, 4103, "만료된 access token입니다."),
    NOT_EXIST_ACCESS_TOKEN_SUBJECT(false, 4104, "access token에 subject가 존재하지 않습니다."),
    NOT_EQUAL_MEMBER_ID(false, 4105, "accessToken의 memberId와 header의 memberId가 일치하지 않습니다."),

    /**
     * 4200 : 로그인, 회원가입 관련 에러
     */
    NOT_EXIST_LOGIN_REQ_DTO(false, 4200, "로그인시 username, password, deviceTokenValue를 모두 입력해주세요."),

    INVALID_USERNAME_FORM(false, 4201, "username 형식이 올바르지 않습니다. (소문자 + 숫자 + '_' + '.'  3~20자)"),
    INVALID_PWD_FORM(false, 4202, "password 형식이 올바르지 않습니다. (영문+숫자+특수문자 6~20자 => 특수문자 한 개 이상 포함)"),
    INVALID_USERNAME_OR_PWD(false, 4203, "username 또는 password가 올바르지 않습니다."),

    NOT_EXIST_MEMBER(false, 4204, "존재하지 않는 멤버입니다."),
    NOT_EXIST_DEVICE_TOKEN(false, 4205, "존재하지 않는 디바이스 토큰입니다."),
    NOT_EXIST_MEMBER_ID_IN_HEADER(false, 4206, "memberId를 헤더에 입력해주세요"),

    INVALID_MEMBER_ID(false, 4207, "유효하지 않은 memberId입니다. 숫자형태로 입력해주세요."),

    NOT_EXIST_JOIN_REQ_DTO(false, 4208, "loginType, phoneNumber, name, username, password, birthDate, deviceTokenValue 를 모두 입력해주세요."),
    INVALID_LOGIN_TYPE_FORM(false, 4209, "loginType 형식이 올바르지 않습니다. (BASIC 또는 KAKAO)"),
    INVALID_PHONE_NUMBER_FORM(false, 4210, "phoneNumber 형식이 올바르지 않습니다. (숫자 3~11자)"),
    INVALID_NAME_FORM(false, 4211, "name 형식이 올바르지 않습니다. (자유형식 1~20자)"),
    ALREADY_EXIST_USERNAME(false, 4212, "이미 존재하는 username입니다."),
    ALREADY_EXIST_PHONE_NUMBER(false, 4213, "이미 존재하는 phoneNumber입니다."),
    ;


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

    public static BaseResponseStatus of(String errorName){
        return BaseResponseStatus.valueOf(errorName);
    }
}
