package com.daramassalik.backend.profile;

public record AddressResponse(
        Long id,
        String label,
        String firstName,
        String lastName,
        String addressLine1,
        String addressLine2,
        String postalCode,
        String city,
        String country,
        boolean defaultAddress
) {
    public static AddressResponse from(Address address) {
        return new AddressResponse(address.getId(), address.getLabel(), address.getFirstName(), address.getLastName(),
                address.getAddressLine1(), address.getAddressLine2(), address.getPostalCode(), address.getCity(),
                address.getCountry(), address.isDefaultAddress());
    }
}
