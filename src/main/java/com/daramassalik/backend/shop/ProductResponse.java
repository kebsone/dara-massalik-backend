package com.daramassalik.backend.shop;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String reference,
        String category,
        String description,
        BigDecimal price,
        int stock,
        String imageUrl,
        boolean active
) {}
