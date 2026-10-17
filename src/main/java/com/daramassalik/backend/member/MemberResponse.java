package com.daramassalik.backend.member;

import java.time.LocalDate;

public record MemberResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        LocalDate joinedDate,
        boolean active
) {}
