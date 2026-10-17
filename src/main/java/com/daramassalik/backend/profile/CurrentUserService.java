package com.daramassalik.backend.profile;

import com.daramassalik.backend.auth.AppUser;
import com.daramassalik.backend.auth.AppUserRepository;
import com.daramassalik.backend.common.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    private final AppUserRepository repository;

    public CurrentUserService(AppUserRepository repository) {
        this.repository = repository;
    }

    public AppUser requireUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }
        Long userId;
        try {
            userId = Long.valueOf(authentication.getName());
        } catch (NumberFormatException ex) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }
        return repository.findById(userId)
                .filter(AppUser::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
    }
}
