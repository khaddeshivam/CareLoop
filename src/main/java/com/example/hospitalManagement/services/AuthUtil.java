package com.example.hospitalManagement.services;

import com.example.hospitalManagement.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class AuthUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    public SecretKey getJwtSecretKey() {

        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    // To Generate Access Token
    public String generateAccessToken(User user) {

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getJwtSecretKey())
                .compact();

    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getJwtSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}
