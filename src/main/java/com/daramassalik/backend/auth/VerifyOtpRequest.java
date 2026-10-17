package com.daramassalik.backend.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record VerifyOtpRequest(
        @NotNull UUID challengeId,
        @NotBlank String phoneNumber,
        @NotBlank @Pattern(regexp = "\\d{6}")
        @Schema(description = "Six digit OTP code", example = "482173")
        String code,
        @Schema(description = "First name required when a new account is created", example = "Mamadou")
        String firstName,
        @Schema(description = "Last name required when a new account is created", example = "Diop")
        String lastName,
        @Schema(description = "Optional email address", example = "mamadou.diop@example.com")
        String email
) {
}
