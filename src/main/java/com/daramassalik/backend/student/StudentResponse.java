package com.daramassalik.backend.student;

import java.time.LocalDate;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String phone,
        String parentName,
        String parentPhone,
        boolean active,
        Long groupId,
        String groupName,
        String groupColor
) {}