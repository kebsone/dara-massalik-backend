package com.daramassalik.backend.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MemberRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        @Email @Size(max = 160) String email,
        @Size(max = 30) String phone,
        @Size(max = 255) String address,
        LocalDate joinedDate
) {}
