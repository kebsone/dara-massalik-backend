package com.daramassalik.backend.auth;

import java.util.Arrays;
import java.util.List;

public record PermissionCatalogResponse(List<Permission> permissions) {
    public static PermissionCatalogResponse all() { return new PermissionCatalogResponse(Arrays.stream(Permission.values()).sorted().toList()); }
}
