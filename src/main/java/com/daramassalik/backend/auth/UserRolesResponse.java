package com.daramassalik.backend.auth;

import java.util.List;

public record UserRolesResponse(
        Long userId,
        List<Role> roles
) {
}
