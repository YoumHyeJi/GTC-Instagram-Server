package com.garit.instagram.service;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.domain.member.MemberRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.util.Date;

import static com.garit.instagram.config.base.BaseResponseStatus.*;

@Log4j2
@Service
@RequiredArgsConstructor
public class JwtService {

    // Base64로 인코딩된 JWT 시크릿키
    @Value("${jwt.secret-key}")
    private String JWT_SECRET_KEY;

    // access token 유효 시간 1일 = 1000 * 60 * 60 * 24 = 86400000
    @Value("${jwt.access-token-expire-time}")
    private long ACCESS_TOKEN_EXPIRE_TIME;

    // refresh token 유효 시간 10일 = 1000 * 60 * 60 * 24 * 10 = 864000000
    @Value("${jwt.refresh-token-expire-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;

    // refresh token 재발급 기준 시간 2일 = 1000 * 60 * 60 * 24 * 2 = 172800000
    @Value("${jwt.refresh-token-reissue-time}")
    private long REFRESH_TOKEN_REISSUE_TIME;

    @Value("${jwt.access-token-header-name}")
    private String ACCESS_TOKEN_HEADER_NAME;

    @Value("${jwt.refresh-token-header-name}")
    private String REFRESH_TOKEN_HEADER_NAME;

    private final RedisService redisService;

    /**
     * jwt access Token을 생성해서 리턴한다.
     * 1. payload에 memberId와 memberRole 저장
     * 2. 만료 시간은 1일
     */
    public String createJwtAccessToken(String memberId, MemberRole memberRole) throws BaseException {
        try {
            Date now = new Date();

            Claims claims = Jwts.claims().setSubject(memberId);     // JWT payload에 저장되는 정보단위
            claims.put("memberRole", memberRole.name());

            return Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setClaims(claims)      // payload 저장
                    .setIssuedAt(now)       //토큰 발생 시간 정보
                    .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))      // Expire Time
                    .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)     // 사용할 암호화 알고리즘과 signature에 들어갈 secret 값 세팅
                    .compact();
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("createJwtAccessToken() : jwtAccessToken 생성 중 에러 발생");
            throw new BaseException(JWT_ERROR);
        }
    }

    /**
     * jwt refresh Token을 생성해서 리턴한다.
     * 1. 생성한 jwt refresh token은 redis에 저장한다.
     * 2. 만료 시간은 10일
     */
    public String createJwtRefreshToken(String deviceTokenId) throws BaseException{
        try {
            Date now = new Date();

            String jwtRefreshToken = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME))
                    .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                    .compact();

            // redis에 jwt refresh token 저장
            redisService.setRefreshTokenInRedis(deviceTokenId, jwtRefreshToken, REFRESH_TOKEN_EXPIRE_TIME);

            return jwtRefreshToken;
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("createJwtRefreshToken() : jwtRefreshToken 생성 중 에러 발생");
            throw new BaseException(JWT_ERROR);
        }

    }

    /**
     * Request Header에서 JWT access token 값을 가져와서 리턴한다. (Bearer 문자열은 제외하고 토큰 값만 리턴)
     * 1. Request Header에 access token이 없으면 BaseException throw
     * 2. access token이 Bearer로 시작하지 않으면 BaseException throw
     */
    public String getJwtAccessToken(HttpServletRequest request) throws BaseException {
        try {
            String jwtAccessToken = request.getHeader(ACCESS_TOKEN_HEADER_NAME);

            if (jwtAccessToken == null || !jwtAccessToken.startsWith("Bearer ")) {
                throw new BaseException(NOT_EXIST_ACCESS_TOKEN_IN_HEADER);
            }

            return jwtAccessToken.replace("Bearer ", "");
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("getJwtAccessToken() : 헤더에서 jwtAccessToken 파싱 중 에러 발생");
            throw new BaseException(JWT_ERROR);
        }
    }

    /**
     * Request Header에서 JWT refresh token 값을 가져와서 리턴한다. (Bearer 문자열은 제외하고 토큰 값만 리턴)
     * 1. Request Header에 refresh token이 없으면 BaseException throw
     * 2. refresh token이 Bearer로 시작하지 않으면 BaseException throw
     */
    public String getJwtRefreshToken(HttpServletRequest request) throws BaseException {
        try {
            String jwtRefreshToken = request.getHeader(REFRESH_TOKEN_HEADER_NAME);

            if (jwtRefreshToken == null || !jwtRefreshToken.startsWith("Bearer ")) {
                throw new BaseException(NOT_EXIST_REFRESH_TOKEN_IN_HEADER);
            }

            return jwtRefreshToken.replace("Bearer ", "");
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("getJwtRefreshToken() : 헤더에서 jwtRefreshToken 파싱 중 에러 발생");
            throw new BaseException(JWT_ERROR);
        }
    }

    /**
     * jwt access token에서 MemberId 추출하기
     */
    public String getMemberIdInJwtAccessToken(String jwtAccessToken) throws BaseException {
        try {
            return Jwts.parser()
                    .setSigningKey(JWT_SECRET_KEY)
                    .parseClaimsJws(jwtAccessToken)
                    .getBody()
                    .getSubject();
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("getMemberIdInJwtAccessToken() : jwt 토큰 파싱 중 오류 발생");
            throw new BaseException(JWT_ERROR);
        }
    }

    /**
     * 토큰의 유효성 + 만료일자 확인하기 (access token, refresh token)
     */
    public boolean validateToken(String jwtToken){
        try{
            Date expiration = Jwts.parser()
                    .setSigningKey(JWT_SECRET_KEY)
                    .parseClaimsJws(jwtToken)
                    .getBody()
                    .getExpiration();

            return !expiration.before(new Date());
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("validateToken() : jwt 검증 과정에서 오류 발생");
            return false;
        }
    }

    /**
     * refresh token의 남은 유효기간 확인
     * 1. refresh token의 유효기간이 2일 이하로 남았으면 true 리턴
     * 2. refresh token의 유효기간이 3일 이상으로 남았으면 false 리턴
     */
    public boolean isJwtRefreshTokenReissue(String jwtRefreshToken){
        try{
            Date now = new Date();
            Date expiration = Jwts.parser()
                    .setSigningKey(JWT_SECRET_KEY)
                    .parseClaimsJws(jwtRefreshToken)
                    .getBody()
                    .getExpiration();
            return expiration.before(new Date(now.getTime() + REFRESH_TOKEN_REISSUE_TIME));
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("isJwtRefreshTokenReissue() : jwt 검증 과정에서 오류 발생");
            return true;
        }
    }


}
