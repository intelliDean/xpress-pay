package com.api.xpress.auth_config.security.auth_utils;

import com.api.xpress.auth_config.user.data.enums.Role;
import com.api.xpress.auth_config.user.data.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtService {
    //This is the access token expiration time. it's an environment variable, and it's in seconds
    @Value("${token.expiration.access}")
    private long accessExpiration;
    //This is the refresh token expiration time. it's an environment variable, and it's in seconds
    @Value("${token.expiration.refresh}")
    private long refreshExpiration;

    @Value("${app.name}")
    private String issuer;


    private final SecretKey secretKey;    //This is the key used in signing the jwt token and it's autowired


    //this method extracts the email(username) from the jwt token, and it's used at the point of authorization
    public String extractUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //this method generates the refresh token for authentication
    //refresh token has a longer expiration than access token
    public String generateRefreshToken(User user) {

        return generateToken(getClaims(user), user.getEmailAddress(), refreshExpiration);
    }

    //this method generates the access token for authentication
    //access token has a shorter expiration than refresh token
    public String generateAccessToken(User user) {

        return generateToken(
                getClaims(user),
                user.getEmailAddress(),
                accessExpiration);
    }

    public XpressAuthToken generateTokens(User user) {
        return XpressAuthToken.builder()
                .accessToken(generateAccessToken(user))
                .refreshToken(generateRefreshToken(user))
                .build();
    }

    private String generateToken(Map<String, Object> claims, String email, Long expiration) {
        return Jwts.builder()
                .issuer(issuer)
                .issuedAt(Date.from(Instant.now()))
                .claims(claims)
                .subject(email)
                .expiration(Date.from(Instant.now().plus(Duration.ofHours(expiration))))
                .signWith(secretKey)
                .compact();
    }

    //this method validates the generated token
    public Boolean isValid(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.getExpiration() != null;
        } catch (JwtException e) {
            return false;
        }
    }

    private Map<String, Object> getClaims(User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::name)
                .toList();
        return Map.of("roles", roles);
    }
}
