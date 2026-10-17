package com.daramassalik.backend.order;

import com.daramassalik.backend.profile.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Authenticated shop orders")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    private final CurrentUserService currentUserService;
    private final OrderService orderService;

    public OrderController(CurrentUserService currentUserService, OrderService orderService) {
        this.currentUserService = currentUserService;
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create an order from the current user's cart")
    public OrderResponse create(Authentication authentication, @Valid @RequestBody CreateOrderRequest request) {
        return orderService.create(currentUserService.requireUser(authentication), request);
    }

    @GetMapping
    @Operation(summary = "List the current user's orders")
    public List<OrderResponse> findMine(Authentication authentication) {
        return orderService.findMine(currentUserService.requireUser(authentication));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one of the current user's orders")
    public OrderResponse findMineById(Authentication authentication, @PathVariable Long id) {
        return orderService.findMineById(currentUserService.requireUser(authentication), id);
    }
}
