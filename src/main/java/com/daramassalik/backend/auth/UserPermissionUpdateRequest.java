package com.daramassalik.backend.auth;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record UserPermissionUpdateRequest(@NotNull Map<Permission, Boolean> overrides) {}
