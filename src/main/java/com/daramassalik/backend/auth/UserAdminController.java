package com.daramassalik.backend.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/admin/access")
@Tag(name = "Access management", description = "Roles and permissions administration")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('PERM_ROLE_MANAGE') or hasAuthority('PERM_PERMISSION_MANAGE')")
public class UserAdminController {
    private final AppUserRepository userRepository;
    private final UserRoleService userRoleService;
    private final PermissionService permissionService;

    public UserAdminController(AppUserRepository userRepository, UserRoleService userRoleService, PermissionService permissionService) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
        this.permissionService = permissionService;
    }

    @GetMapping("/users")
    @Operation(summary = "List users for access administration")
    public List<UserSummaryResponse> users() {
        return userRepository.findAll().stream()
                .sorted(Comparator.comparing((AppUser u) -> u.getLastName() == null ? "" : u.getLastName()).thenComparing(u -> u.getFirstName() == null ? "" : u.getFirstName()))
                .map(u -> new UserSummaryResponse(u.getId(), u.getFirstName(), u.getLastName(), u.getPhone(), u.getEmail(), u.isActive(), u.getRoles().stream().sorted(Comparator.comparing(Enum::name)).toList()))
                .toList();
    }

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('PERM_ROLE_MANAGE') or hasAuthority('PERM_PERMISSION_MANAGE')")
    public List<Role> roles() { return List.of(Role.values()); }

    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('PERM_ROLE_MANAGE') or hasAuthority('PERM_PERMISSION_MANAGE')")
    public List<Permission> permissions() {
        return Arrays.stream(Permission.values())
                .sorted(Comparator.comparing(Enum::name))
                .toList();
    }

    @GetMapping("/roles/{role}/permissions")
    @PreAuthorize("hasAuthority('PERM_ROLE_MANAGE')")
    public List<Permission> getRolePermissions(@PathVariable Role role) { return permissionService.getRolePermissions(role); }

    @PutMapping("/roles/{role}/permissions")
    @PreAuthorize("hasAuthority('PERM_ROLE_MANAGE')")
    public List<Permission> updateRolePermissions(@PathVariable Role role, @RequestBody RolePermissionUpdateRequest request) {
        return permissionService.replaceRolePermissions(role, request.permissions());
    }

    @GetMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('PERM_ROLE_MANAGE')")
    public UserRolesResponse getUserRoles(@PathVariable Long id) { return userRoleService.getRoles(id); }

    @PutMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('PERM_ROLE_MANAGE')")
    public UserRolesResponse updateUserRoles(@PathVariable Long id, @RequestBody UserRoleUpdateRequest request) {
        return userRoleService.updateRoles(id, request);
    }

    @GetMapping("/users/{id}/permissions")
    @PreAuthorize("hasAuthority('PERM_ROLE_MANAGE') or hasAuthority('PERM_PERMISSION_MANAGE')")
    public PermissionService.UserPermissionSettings getUserPermissions(@PathVariable Long id) {
        return permissionService.getUserPermissionSettings(id);
    }

    @PutMapping("/users/{id}/permissions")
    @PreAuthorize("hasAuthority('PERM_PERMISSION_MANAGE')")
    public PermissionService.UserPermissionSettings updateUserPermissions(@PathVariable Long id, @RequestBody UserPermissionUpdateRequest request) {
        return permissionService.replaceUserPermissionOverrides(id, request.overrides());
    }
}
