package com.daramassalik.backend.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final long expirationDays;

    public RefreshTokenService(
            RefreshTokenRepository repository,
            @Value("${app.security.refresh-token.expiration-days:30}") long expirationDays) {
        this.repository = repository;
        this.expirationDays = expirationDays;
    }

    @Transactional
    public String create(AppUser user) {
        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenHash(hash(raw));
        token.setExpiresAt(Instant.now().plus(expirationDays, ChronoUnit.DAYS));
        repository.save(token);
        return raw;
    }

    @Transactional
    public RefreshToken rotate(String rawToken) {
        RefreshToken current = repository.findByTokenHash(hash(rawToken))
                .orElseThrow(() -> new OtpInvalidException("Invalid refresh token"));
        if (!current.isActive() || !current.getUser().isActive()) {
            throw new InvalidRefreshTokenException();
        }
        current.setRevokedAt(Instant.now());
        return current;
    }

    @Transactional
    public void revoke(String rawToken) {
        repository.findByTokenHash(hash(rawToken)).ifPresent(token -> token.setRevokedAt(Instant.now()));
    }

    private String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(64);
            for (byte b : digest) builder.append(String.format("%02x", b));
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }
}
