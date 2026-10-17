package com.daramassalik.backend.commission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommissionRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 1000) String description
) {}
