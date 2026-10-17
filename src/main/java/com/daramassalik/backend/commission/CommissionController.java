package com.daramassalik.backend.commission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/commissions")
@Tag(name = "Commissions", description = "Dahira commission management")
@SecurityRequirement(name = "bearerAuth")
public class CommissionController {
    private final CommissionService service;

    public CommissionController(CommissionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List active commissions")
    public List<CommissionResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a commission")
    public CommissionResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a commission")
    public CommissionResponse create(@Valid @RequestBody CommissionRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a commission")
    public CommissionResponse update(@PathVariable Long id, @Valid @RequestBody CommissionRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a commission")
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }
}
