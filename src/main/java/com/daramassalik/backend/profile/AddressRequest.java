package com.daramassalik.backend.profile;

import jakarta.validation.constraints.NotBlank;

public record AddressRequest(
        String label,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String addressLine1,
        String addressLine2,
        @NotBlank String postalCode,
        @NotBlank String city,
        @NotBlank String country,
        boolean defaultAddress
) {}
