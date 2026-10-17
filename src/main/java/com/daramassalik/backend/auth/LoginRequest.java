package com.daramassalik.backend.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "User email address", example = "admin@daramassalik.local")
        @NotBlank
        @Email
        String email,

        @Schema(description = "User password", example = "ChangeMe123!")
        @NotBlank
        String password
) {
}
