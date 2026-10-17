package com.daramassalik.backend.auth;

import com.daramassalik.backend.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service
public class UserRoleService {
    private final AppUserRepository userRepository;

    public UserRoleService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserRolesResponse getRoles(Long userId) {
        AppUser user = findUser(userId);
        return toResponse(user);
    }

    @Transactional
    public UserRolesResponse updateRoles(Long userId, UserRoleUpdateRequest request) {
        AppUser user = findUser(userId);
        boolean wasAdmin = user.hasRole(Role.ADMIN);
        user.setRoles(request.roles());
        if (user.getRoles().isEmpty()) {
            throw new IllegalArgumentException("A user must have at least one role.");
        }
        if (wasAdmin && !user.getRoles().contains(Role.ADMIN) && userRepository.countUsersWithRole(Role.ADMIN) <= 1) {
            throw new IllegalArgumentException("The last administrator cannot be removed.");
        }
        return toResponse(userRepository.save(user));
    }

    private AppUser findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserRolesResponse toResponse(AppUser user) {
        return new UserRolesResponse(
                user.getId(),
                user.getRoles().stream().sorted(Comparator.comparing(Enum::name)).toList()
        );
    }
}
