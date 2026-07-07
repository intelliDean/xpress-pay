package com.api.xpress.xpress_utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;
import com.api.xpress.xceptions.XpressException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class XpressUtils {

    private static final String HMAC_SHA512 = "HmacSHA512";

    @Value("${app.base-url}")
    private static String baseUrl;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static String generateToken(int length) {
        byte[] bytes = new byte[length];
        SECURE_RANDOM.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    public static String calculateHMAC512(String data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    HMAC_SHA512
            );
            Mac mac = Mac.getInstance(HMAC_SHA512);
            mac.init(secretKeySpec);
            return new String(
                    Hex.encode(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)))
            );
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("HMAC512 calculation failed: {}", e.getMessage());
            throw new XpressException("Failed to calculate payment hash");
        }
    }

    public static String getUrl(String token, String email) {
        return "%s/api/v1/auth/verify?token=%s&email=%s".formatted(baseUrl, token, email);
    }
}
