package com.garit.instagram.service;

import com.garit.instagram.config.base.BaseException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.garit.instagram.config.base.BaseResponseStatus.REDIS_ERROR;

@Log4j2
@Service
public class RedisService {

    private final String REFRESH_PREFIX = "REFRESH-TOKEN-";

    private final StringRedisTemplate stringRedisTemplate;
    private final ValueOperations<String, String> valueOperations;

    private final String SIGNUP_AUTH_NUM_PREFIX = "AUTH-NUM-";
    private final long SIGNUP_AUTH_NUM_EXPIRE_TIME = 300000;         // 5분 => 1000 * 60 * 5

    @Autowired
    public RedisService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.valueOperations = this.stringRedisTemplate.opsForValue();
    }

    /**
     * refresh 토큰 10일간 저장하기
     */
    public Boolean setRefreshTokenInRedis(String deviceTokenId, String jwtRefreshToken, long expireTime) throws BaseException {
        try{
            String key = REFRESH_PREFIX + deviceTokenId;
            valueOperations.set(key, jwtRefreshToken);
            return stringRedisTemplate.expire(key, expireTime, TimeUnit.MILLISECONDS);
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("setRefreshTokenInRedis() : valueOperations.set() 혹은 stringRedisTemplate.expire() 실행 중 에러 발생");
            throw new BaseException(REDIS_ERROR);
        }
    }

    /**
     * refresh 토큰 비교하기
     */
    public boolean compareRefreshTokenInRedis(String deviceTokenId, String jwtRefreshTokenInHeader) throws BaseException {
        try{
            String key = REFRESH_PREFIX + deviceTokenId;

            // 해당 deviceTokenId로 발급받은 refresh 토큰이 없다면 null 리턴
            String jwtRefreshTokenInRedis = valueOperations.get(key);
            if(jwtRefreshTokenInRedis == null || !jwtRefreshTokenInRedis.equals(jwtRefreshTokenInHeader)){
                return false;
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("compareRefreshTokenInRedis() : valueOperations.get() 실행 중 에러 발생");
            throw new BaseException(REDIS_ERROR);
        }
    }

    /**
     * refresh 토큰 삭제하기
     */
    public void deleteRefreshTokenInRedis(String deviceTokenId) throws BaseException{
        try {
            String key = REFRESH_PREFIX + deviceTokenId;
            stringRedisTemplate.delete(key);
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("deleteRefreshTokenInRedis() : stringRedisTemplate.delete() 실행 중 에러 발생");
            throw new BaseException(REDIS_ERROR);
        }
    }

    /**
     * 회원가입 인증번호 5분간 저장하기
     */
    public Boolean setSignupAuthNumInRedis(String phoneOrEmail, String authNum) throws BaseException{
        try {
            String key = SIGNUP_AUTH_NUM_PREFIX + phoneOrEmail;
            valueOperations.set(key, authNum);
            return stringRedisTemplate.expire(key, SIGNUP_AUTH_NUM_EXPIRE_TIME, TimeUnit.MILLISECONDS);
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("setSignupAuthNumInRedis() : valueOperations.set() 혹은 stringRedisTemplate.expire() 실행 중 에러 발생");
            throw new BaseException(REDIS_ERROR);
        }
    }

    /**
     * 회원가입 인증번호 검증하기
     */
    public boolean compareSignupAuthNumInRedis(String phoneOrEmail, String authNumInRequest) throws BaseException{
        try{
            String key = SIGNUP_AUTH_NUM_PREFIX + phoneOrEmail;
            String authNumInRedis = valueOperations.get(key);
            if(authNumInRedis == null || !authNumInRedis.equals(authNumInRequest)){
                return false;
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("compareSignupAuthNumInRedis() : valueOperations.get() 실행 중 에러 발생");
            throw new BaseException(REDIS_ERROR);
        }
    }
}
