package com.example.hospitalManagement.services;

import com.example.hospitalManagement.entities.RefreshToken;
import com.example.hospitalManagement.entities.User;
import com.example.hospitalManagement.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.auth.refresh-token-expiration-ms}")
    private long refreshTokenDurationMs;

    @Transactional
    public RefreshTokenResult createRefreshToken(User user) {

        // Generate 256 bits of cryptographically secure randomness.
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        // Convert bytes into a URL-safe token.
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        // Store only the SHA-256 hash.
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken
                .builder()
                .tokenHash(tokenHash)
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        // Return the raw token only to the client.
        return new RefreshTokenResult(rawToken, refreshToken);
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashedBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(hashedBytes);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 algorithm unavailable", exception);
        }
    }

    @Transactional
    public RefreshToken verifyRefreshToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash).orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);

            throw new IllegalArgumentException("Refresh token has expired");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            token.setRevoked(true);
            refreshTokenRepository.save(token);
        });
    }

    public record RefreshTokenResult(String rawToken, RefreshToken token) {
    }

    @Transactional
    public RefreshTokenResult rotateRefreshToken(String rawToken) {

        String tokenHash = hashToken(rawToken);

        RefreshToken oldToken = refreshTokenRepository
                .findByTokenHashForUpdate(tokenHash)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid refresh token")
                );

        if (oldToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh token already used or revoked");
        }

        if (!oldToken.getExpiresAt().isAfter(Instant.now())) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        oldToken.setRevoked(true);

        return createRefreshToken(oldToken.getUser());
    }

}
