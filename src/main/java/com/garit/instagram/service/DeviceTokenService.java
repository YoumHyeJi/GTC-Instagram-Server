package com.garit.instagram.service;

import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.config.base.BaseResponseStatus;
import com.garit.instagram.domain.deviceToken.DeviceToken;
import com.garit.instagram.domain.deviceToken.DeviceTokenRepository;
import com.garit.instagram.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import static com.garit.instagram.config.base.BaseResponseStatus.DATABASE_ERROR;
import static com.garit.instagram.config.base.BaseResponseStatus.NOT_EXIST_DEVICE_TOKEN;

@Service
@Log4j2
@RequiredArgsConstructor
public class DeviceTokenService {

    private final DeviceTokenRepository deviceTokenRepository;

    public void createDeviceToken(Member member, String deviceTokenValue) throws BaseException {
        try{
            // member+deviceTokenValue 조합이 DB에 존재하지 않는 경우 => DeviceToken 새로 생성
            if (existsDeviceToken(member, deviceTokenValue) == false){
                DeviceToken deviceToken = DeviceToken.createDeviceToken(member, deviceTokenValue);
                deviceTokenRepository.save(deviceToken);
            }
        }
        catch (BaseException e){
            throw e;
        }
        catch(Exception e){
            log.error("createDeviceToken() : 디바이스 토큰 생성 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public boolean existsDeviceToken(Member member, String deviceTokenValue) throws BaseException {
        try{
            return deviceTokenRepository.existsDeviceTokenByMemberAndDeviceTokenValue(member, deviceTokenValue);
        }
        catch (Exception e){
            log.error("existsDeviceToken() : deviceTokenRepository.existsDeviceTokenByMemberAndDeviceTokenValue() 함수 실행 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public DeviceToken findDeviceToken(Member member, String deviceTokenValue) throws BaseException{
        try{
            return deviceTokenRepository.findDeviceTokenByMemberAndDeviceTokenValue(member, deviceTokenValue)
                    .orElseThrow(()-> new BaseException(NOT_EXIST_DEVICE_TOKEN));
        }
        catch (BaseException e){
            throw e;
        }
        catch (Exception e){
            log.error("findDeviceToken() : deviceTokenRepository.findDeviceTokenByMemberAndDeviceTokenValue() 실행 중 데이터베이스 에러 발생");
            e.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
