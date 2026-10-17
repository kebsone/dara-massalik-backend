package com.daramassalik.backend.auth;

import com.daramassalik.backend.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PermissionService {
    private final AppUserRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionOverrideRepository userPermissionOverrideRepository;

    public PermissionService(AppUserRepository userRepository,
                             RolePermissionRepository rolePermissionRepository,
                             UserPermissionOverrideRepository userPermissionOverrideRepository) {
        this.userRepository = userRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userPermissionOverrideRepository = userPermissionOverrideRepository;
    }

    @Transactional(readOnly = true)
    public Set<Permission> getEffectivePermissions(AppUser user) {
        EnumSet<Permission> effective = EnumSet.noneOf(Permission.class);
        for (Role role : user.getRoles()) {
            rolePermissionRepository.findByRole(role).forEach(rp -> effective.add(rp.getPermission()));
        }
        for (UserPermissionOverride override : userPermissionOverrideRepository.findByUser_Id(user.getId())) {
            if (override.isGranted()) effective.add(override.getPermission());
            else effective.remove(override.getPermission());
        }
        if (user.hasRole(Role.ADMIN)) effective.addAll(EnumSet.allOf(Permission.class));
        return new LinkedHashSet<>(effective);
    }

    public boolean hasPermission(AppUser user, Permission permission) {
        return getEffectivePermissions(user).contains(permission);
    }

    @Transactional(readOnly = true)
    public List<Permission> getRolePermissions(Role role) {
        return rolePermissionRepository.findByRole(role).stream().map(RolePermission::getPermission).sorted(Comparator.comparing(Enum::name)).toList();
    }


    @Transactional
    public List<Permission> replaceRolePermissions(
            Role role,
            Set<Permission> permissions) {

        // Supprimer les anciennes permissions du rôle.
        rolePermissionRepository.deleteByRole(role);

        // Forcer l'exécution des suppressions avant les insertions.
        rolePermissionRepository.flush();

        // Ajouter les nouvelles permissions uniquement si la liste n'est pas vide.
        if (permissions != null && !permissions.isEmpty()) {
            List<RolePermission> entities = permissions.stream()
                    .map(permission -> new RolePermission(role, permission))
                    .toList();

            rolePermissionRepository.saveAllAndFlush(entities);
        }

        return getRolePermissions(role);
    }

    @Transactional(readOnly = true)
    public UserPermissionSettings getUserPermissionSettings(Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Set<Permission> inherited = EnumSet.noneOf(Permission.class);
        for (Role role : user.getRoles()) {
            inherited.addAll(getRolePermissions(role));
        }
        Set<Permission> granted = EnumSet.noneOf(Permission.class);
        Set<Permission> denied = EnumSet.noneOf(Permission.class);
        for (UserPermissionOverride override : userPermissionOverrideRepository.findByUser_Id(userId)) {
            (override.isGranted() ? granted : denied).add(override.getPermission());
        }
        Set<Permission> effective = getEffectivePermissions(user);
        return new UserPermissionSettings(user.getId(), user.getRoles(), inherited, granted, denied, effective);
    }


    @Transactional
    public UserPermissionSettings replaceUserPermissionOverrides(
            Long userId,
            Map<Permission, Boolean> overrides) {

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Charger les autorisations personnalisées existantes.
        List<UserPermissionOverride> existingOverrides =
                userPermissionOverrideRepository.findByUser_Id(userId);

        // Copie pour identifier les nouvelles autorisations à créer.
        Map<Permission, Boolean> remainingOverrides =
                new EnumMap<>(Permission.class);

        remainingOverrides.putAll(overrides);

        for (UserPermissionOverride existing : existingOverrides) {
            Permission permission = existing.getPermission();

            // Permission absente ou valeur null : retour au comportement par rôle.
            Boolean requestedValue = remainingOverrides.remove(permission);

            if (requestedValue == null) {
                userPermissionOverrideRepository.delete(existing);
            } else {
                // Modifier la ligne existante au lieu de la recréer.
                existing.setGranted(requestedValue);
            }
        }

        // Insérer uniquement les autorisations qui n'existaient pas.
        List<UserPermissionOverride> newOverrides =
                remainingOverrides.entrySet().stream()
                        .filter(entry -> entry.getValue() != null)
                        .map(entry -> new UserPermissionOverride(
                                user,
                                entry.getKey(),
                                entry.getValue()
                        ))
                        .toList();

        if (!newOverrides.isEmpty()) {
            userPermissionOverrideRepository.saveAll(newOverrides);
        }

        return getUserPermissionSettings(userId);
    }


    public record UserPermissionSettings(Long userId, Set<Role> roles, Set<Permission> inherited,
                                         Set<Permission> granted, Set<Permission> denied,
                                         Set<Permission> effective) {}
}
