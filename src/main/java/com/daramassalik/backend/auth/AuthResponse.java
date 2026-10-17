package com.daramassalik.backend.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthResponse(
        @Schema(description = "Short-lived JWT access token") String accessToken,
        @Schema(example = "Bearer") String tokenType,
        @Schema(description = "Access token lifetime in seconds", example = "900") long expiresIn,
        @Schema(description = "Long-lived opaque refresh token") String refreshToken,
        AuthUserResponse user
) {
}
