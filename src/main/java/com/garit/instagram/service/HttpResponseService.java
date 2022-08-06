package com.garit.instagram.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.config.base.BaseResponse;
import com.garit.instagram.config.base.BaseResponseStatus;
import com.garit.instagram.domain.member.dto.LoginResDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.garit.instagram.config.base.BaseResponseStatus.IO_EXCEPTION;

@Service
@RequiredArgsConstructor
@Log4j2
public class HttpResponseService {

    private final ObjectMapper objectMapper;

    private final String CONTENT_TYPE = "application/json";
    private final String ENCODING = "UTF-8";

    /**
     * 에러가 발생한 경우
     */
    public void errorRespond(HttpServletResponse response, BaseResponseStatus status) throws BaseException {
       try {
           response.setContentType(CONTENT_TYPE);
           response.setCharacterEncoding(ENCODING);
           PrintWriter out = response.getWriter();
           objectMapper.writeValue(out, new BaseResponse<>(status));
       }
       catch (IOException e){
           e.printStackTrace();
           log.error("errorRespond() : objectMapper.writeValue() 실행중 IOException 발생");
           throw new BaseException(IO_EXCEPTION);
       }
    }

    /**
     * 정상적인 경우
     */
    public void successRespond(HttpServletResponse response, LoginResDTO loginResDTO) throws BaseException {
        try{
            response.setContentType(CONTENT_TYPE);
            response.setCharacterEncoding(ENCODING);
            PrintWriter out = response.getWriter();
            objectMapper.writeValue(out, new BaseResponse<>(loginResDTO));
        }
        catch (IOException e){
            e.printStackTrace();
            log.error("successRespond() : objectMapper.writeValue() 실행중 IOException 발생");
            throw new BaseException(IO_EXCEPTION);
        }
    }
}
