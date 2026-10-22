package com.daramassalik.backend.student;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record StudentResponse(
        @NotNull Long id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String phone,
        String parentName,
        String parentPhone,
        boolean active,
        Long groupId,
        String groupName,
        String groupColor,
        String photoUrl
) {}