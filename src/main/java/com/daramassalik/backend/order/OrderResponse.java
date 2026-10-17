package com.daramassalik.backend.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        String status,
        BigDecimal totalAmount,
        String currency,
        String shippingFirstName,
        String shippingLastName,
        String shippingAddressLine1,
        String shippingAddressLine2,
        String shippingPostalCode,
        String shippingCity,
        String shippingCountry,
        Instant createdAt,
        List<OrderItemResponse> items
) {}
