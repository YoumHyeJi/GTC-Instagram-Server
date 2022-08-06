package com.garit.instagram.utils;

import com.garit.instagram.domain.follow.FollowCategory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.garit.instagram.domain.follow.FollowCategory.follower;
import static com.garit.instagram.domain.follow.FollowCategory.following;

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

    public static boolean isRegexFollowCategory(String target){
        // follower 또는 following
        String regex = "^("+ follower.name() +"|"+following.name()+")$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }
}
