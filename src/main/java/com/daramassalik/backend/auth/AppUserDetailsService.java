package com.daramassalik.backend.auth;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {
    private final AppUserRepository repository;
    private final PermissionService permissionService;

    public AppUserDetailsService(AppUserRepository repository, PermissionService permissionService) {
        this.repository = repository;
        this.permissionService = permissionService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = repository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getPasswordHash() == null) {
            throw new UsernameNotFoundException("Password authentication is not enabled for this user");
        }

        String principal = user.getEmail() != null ? user.getEmail() : user.getPhone();
        java.util.List<String> authorities = new java.util.ArrayList<>(user.getRoles().stream().map(role -> "ROLE_" + role.name()).toList());
        permissionService.getEffectivePermissions(user).forEach(permission -> authorities.add("PERM_" + permission.name()));
        return User.withUsername(principal)
                .password(user.getPasswordHash())
                .authorities(authorities.toArray(String[]::new))
                .disabled(!user.isActive())
                .build();
    }
}
