package com.daramassalik.backend.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        Long shippingAddressId,
        @NotEmpty List<@Valid OrderItemRequest> items
) {}
