package com.garit.instagram.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Configuration
public class ApplicationConfig {

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    // 암호화에 필요한 PasswordEncoder를 Bean으로 등록
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public Random random(){
        return new Random();
    }

    @Bean
    public RestTemplate getCustomRestTemplate(){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();

        factory.setReadTimeout(5000);       // read timeout
        factory.setConnectTimeout(3000);    // connection timeout

        //Apache HttpComponents HttpClient
        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(50)        //최대 커넥션 수
                .setMaxConnPerRoute(20)     //각 호스트(IP와 Port의 조합)당 커넥션 풀에 생성가능한 커넥션 수
                .build();
        factory.setHttpClient(httpClient);
        return new RestTemplate(factory);
    }
}
