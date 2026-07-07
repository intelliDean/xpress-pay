package com.api.xpress.auth_config.security.auth_utils;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Configuration
public class XpressAppConfig {

    @Value("${token.secret}")
    private String jwtSecret;

    @Value("${sendinblue.mail.api_key}")
    private String apiKey;

    @Value("${sendinblue.mail.url}")
    private String mailUrl;

    @Bean
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new Argon2PasswordEncoder(
                16,
                32,
                1,
                65536,
                4
        );
    }

    @Bean
    public WebClient.Builder getWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }
}
