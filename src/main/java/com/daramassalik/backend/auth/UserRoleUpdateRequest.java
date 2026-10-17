package com.daramassalik.backend.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public record UserRoleUpdateRequest(
        @NotEmpty
        @Schema(description = "Complete set of roles assigned to the user", example = "[MEMBER, CUSTOMER]")
        Set<Role> roles
) {
}
