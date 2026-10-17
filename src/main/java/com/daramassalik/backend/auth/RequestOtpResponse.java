package com.daramassalik.backend.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record RequestOtpResponse(
        @Schema(description = "OTP challenge identifier")
        UUID challengeId,
        @Schema(description = "OTP validity in seconds", example = "300")
        long expiresInSeconds,
        @Schema(description = "Whether a user already exists for this phone number")
        boolean existingUser,
        @Schema(description = "Development-only OTP. Never expose this in production.", nullable = true)
        String debugCode
) {
}
