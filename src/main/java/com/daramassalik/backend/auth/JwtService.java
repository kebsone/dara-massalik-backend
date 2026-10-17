package com.daramassalik.backend.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
    private final SecretKey key;
    private final long expirationMinutes;

    public JwtService(
            @Value("${app.security.jwt.secret}") String secret,
            @Value("${app.security.jwt.expiration-minutes:15}") long expirationMinutes) {
        if (secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must contain at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(AppUser user) {
        Instant now = Instant.now();
        List<String> roles = user.getRoles().stream().map(Enum::name).sorted().toList();
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("roles", roles)
                .claim("role", user.getPrimaryRole() != null ? user.getPrimaryRole().name() : null)
                .claim("email", user.getEmail())
                .claim("phone", user.getPhone())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(getExpirationSeconds())))
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public long getExpirationSeconds() { return expirationMinutes * 60; }
}
