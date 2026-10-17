package com.daramassalik.backend.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User roles", description = "Administration of user roles")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class UserRoleController {
    private final UserRoleService service;

    public UserRoleController(UserRoleService service) {
        this.service = service;
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "Get roles assigned to a user")
    public UserRolesResponse getRoles(@PathVariable Long id) {
        return service.getRoles(id);
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "Replace roles assigned to a user")
    public UserRolesResponse updateRoles(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateRequest request) {
        return service.updateRoles(id, request);
    }
}
