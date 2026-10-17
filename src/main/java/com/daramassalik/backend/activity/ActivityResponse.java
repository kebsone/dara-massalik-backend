package com.daramassalik.backend.activity;

import java.time.LocalDate;

public record ActivityResponse(
        Long id,
        String name,
        String description,
        LocalDate activityDate,
        String location,
        ActivityStatus status,
        Long commissionId,
        String commissionName
) {}
