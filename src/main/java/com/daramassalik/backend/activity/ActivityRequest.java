package com.daramassalik.backend.activity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ActivityRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 1500) String description,
        @NotNull LocalDate activityDate,
        @Size(max = 200) String location,
        @NotNull ActivityStatus status,
        Long commissionId
) {}
