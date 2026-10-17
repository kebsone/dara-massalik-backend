package com.daramassalik.backend.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RequestOtpRequest(
        @Schema(description = "Phone number in international format", example = "+33612345678")
        @NotBlank
        String phoneNumber
) {
}
