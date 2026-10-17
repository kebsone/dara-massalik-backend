package com.daramassalik.backend.student;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record StudentRequest(
        @NotBlank @Size(max = 80) String firstName,
        @NotBlank @Size(max = 80) String lastName,
        LocalDate birthDate,
        @Size(max = 30) String phone,
        @Size(max = 160) String parentName,
        @Size(max = 30) String parentPhone,
        Long groupId
) {}
