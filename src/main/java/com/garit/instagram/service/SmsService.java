package com.garit.instagram.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.garit.instagram.config.base.BaseException;
import com.garit.instagram.domain.sms.Message;
import com.garit.instagram.domain.sms.SmsReqDTO;
import com.garit.instagram.domain.sms.SmsResDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import static com.garit.instagram.config.base.BaseResponseStatus.SMS_ERROR;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${sms.service-id}")
    private String SMS_SERVICE_ID;

    @Value("${sms.access-key}")
    private String SMS_ACCESS_KEY;

    @Value("${sms.secret-key}")
    private String SMS_SECRET_KEY;

    @Value("${sms.sender-phone-number}")
    private String SMS_SENDER_PHONE_NUMBER;

    public SmsResDTO sendSms(String recipientPhoneNumber, String content) throws BaseException {
        try {
            Long time = System.currentTimeMillis();
            List<Message> messages = new ArrayList<>();
            messages.add(new Message(recipientPhoneNumber, content));

            SmsReqDTO smsReqDTO = SmsReqDTO.builder()
                    .type("SMS")
                    .contentType("COMM")
                    .countryCode("82")
                    .from(SMS_SENDER_PHONE_NUMBER)
                    .content("인증번호 전송")
                    .messages(messages)
                    .build();
            String jsonBody = objectMapper.writeValueAsString(smsReqDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-ncp-apigw-timestamp", time.toString());
            headers.set("x-ncp-iam-access-key", this.SMS_ACCESS_KEY);
            String sig = makeSignature(time); //암호화
            headers.set("x-ncp-apigw-signature-v2", sig);

            HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);

            return restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+this.SMS_SERVICE_ID+"/messages"), body, SmsResDTO.class);
        }
        catch (JsonProcessingException | URISyntaxException e){
            e.printStackTrace();
            throw new BaseException(SMS_ERROR);
        }
    }

    private String makeSignature(Long time) throws BaseException {

        try {
            String space = " ";
            String newLine = "\n";
            String method = "POST";
            String url = "/sms/v2/services/" + this.SMS_SERVICE_ID + "/messages";
            String timestamp = time.toString();
            String accessKey = this.SMS_ACCESS_KEY;
            String secretKey = this.SMS_SECRET_KEY;

            String message = new StringBuilder()
                    .append(method)
                    .append(space)
                    .append(url)
                    .append(newLine)
                    .append(timestamp)
                    .append(newLine)
                    .append(accessKey)
                    .toString();

            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            String encodeBase64String = Base64.encodeBase64String(rawHmac);

            return encodeBase64String;
        }
        catch (NoSuchAlgorithmException | InvalidKeyException e){
            e.printStackTrace();
            throw new BaseException(SMS_ERROR);
        }
    }
}
