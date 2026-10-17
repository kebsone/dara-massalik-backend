package com.daramassalik.backend.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AuthUserResponse(
        Long id,
        String phone,
        String email,
        String firstName,
        String lastName,
        @Schema(description = "Primary role kept for backwards compatibility", example = "MEMBER") Role role,
        @Schema(description = "All roles assigned to the user", example = "[MEMBER, CUSTOMER]") List<Role> roles,
        @Schema(description = "Effective permissions for the current user") List<Permission> permissions
) {
}
