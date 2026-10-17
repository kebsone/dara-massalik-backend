package com.daramassalik.backend.profile;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserProfileRequest(
        @Schema(example = "Mamadou") String firstName,
        @Schema(example = "Diop") String lastName,
        @Schema(example = "mamadou.diop@example.com") String email
) {}
