package com.daramassalik.backend.profile;

import com.daramassalik.backend.auth.AppUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me")
@Tag(name = "My account", description = "Authenticated user profile and delivery addresses")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {
    private final CurrentUserService currentUserService;
    private final ProfileService profileService;

    public ProfileController(CurrentUserService currentUserService, ProfileService profileService) {
        this.currentUserService = currentUserService;
        this.profileService = profileService;
    }

    @GetMapping
    @Operation(summary = "Get current user profile")
    public UserProfileResponse me(Authentication authentication) {
        return UserProfileResponse.from(currentUserService.requireUser(authentication));
    }

    @PatchMapping
    @Operation(summary = "Update current user profile")
    public UserProfileResponse update(Authentication authentication, @Valid @RequestBody UserProfileRequest request) {
        return profileService.update(currentUserService.requireUser(authentication), request);
    }

    @GetMapping("/addresses")
    @Operation(summary = "List delivery addresses")
    public List<AddressResponse> addresses(Authentication authentication) {
        return profileService.addresses(currentUserService.requireUser(authentication));
    }

    @PostMapping("/addresses")
    @Operation(summary = "Create a delivery address")
    public ResponseEntity<AddressResponse> createAddress(Authentication authentication, @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(profileService.createAddress(currentUserService.requireUser(authentication), request));
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "Update a delivery address")
    public AddressResponse updateAddress(Authentication authentication, @PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        return profileService.updateAddress(currentUserService.requireUser(authentication), id, request);
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "Delete a delivery address")
    public ResponseEntity<Void> deleteAddress(Authentication authentication, @PathVariable Long id) {
        profileService.deleteAddress(currentUserService.requireUser(authentication), id);
        return ResponseEntity.noContent().build();
    }
}
