package com.daramassalik.backend.shop;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 160) String name,
        @NotBlank @Size(max = 80) String reference,
        @NotBlank @Size(max = 80) String category,
        @Size(max = 1500) String description,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        @PositiveOrZero int stock,
        @Size(max = 500) String imageUrl
) {}
