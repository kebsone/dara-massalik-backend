package com.daramassalik.backend.profile;

import com.daramassalik.backend.auth.AppUser;
import com.daramassalik.backend.auth.Role;

import java.time.Instant;
import java.util.List;

public record UserProfileResponse(
        Long id,
        String phone,
        String email,
        String firstName,
        String lastName,
        String role,
        List<Role> roles,
        Instant createdAt
) {
    public static UserProfileResponse from(AppUser user) {
        List<Role> roles = user.getRoles().stream()
                .sorted(java.util.Comparator.comparing(Enum::name))
                .toList();
        return new UserProfileResponse(
                user.getId(), user.getPhone(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getPrimaryRole() != null ? user.getPrimaryRole().name() : null,
                roles,
                user.getCreatedAt()
        );
    }
}
