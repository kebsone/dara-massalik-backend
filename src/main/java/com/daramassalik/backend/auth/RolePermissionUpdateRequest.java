package com.daramassalik.backend.auth;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record RolePermissionUpdateRequest(@NotNull Set<Permission> permissions) {}
