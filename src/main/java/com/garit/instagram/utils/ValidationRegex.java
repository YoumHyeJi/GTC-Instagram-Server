package com.garit.instagram.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    /**
     * 아이디 형식 체크
     */
    public static boolean isRegexUsername(String target) {
        // 영어 소문자 + 숫자 + '_' + '.'  3~20자
        String regex = "^[a-z0-9_.]{3,20}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    /**
     * 비밀번호 형식 체크
     */
    public static boolean isRegexPassword(String target){
        // 영어 + 숫자 + 특수문자 6~20자 (특수문자 1자 이상 포함)
        String regex = "^(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,20}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

}
