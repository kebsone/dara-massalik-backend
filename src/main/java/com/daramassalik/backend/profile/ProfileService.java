package com.daramassalik.backend.profile;

import com.daramassalik.backend.auth.AppUser;
import com.daramassalik.backend.auth.AppUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final AppUserRepository userRepository;
    private final AddressRepository addressRepository;

    public ProfileService(AppUserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public UserProfileResponse update(AppUser user, UserProfileRequest request) {
        if (request.firstName() != null) user.setFirstName(request.firstName().trim());
        if (request.lastName() != null) user.setLastName(request.lastName().trim());
        if (request.email() != null && !request.email().isBlank()) {
            userRepository.findByEmailIgnoreCase(request.email().trim())
                    .filter(existing -> !existing.getId().equals(user.getId()))
                    .ifPresent(existing -> { throw new IllegalArgumentException("Cette adresse e-mail est déjà utilisée."); });
            user.setEmail(request.email().trim().toLowerCase());
        }
        return UserProfileResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public java.util.List<AddressResponse> addresses(AppUser user) {
        return addressRepository.findAllByUserOrderByDefaultAddressDescCreatedAtAsc(user)
                .stream().map(AddressResponse::from).toList();
    }

    @Transactional
    public AddressResponse createAddress(AppUser user, AddressRequest request) {
        Address address = new Address();
        address.setUser(user);
        apply(address, request);
        clearDefaultIfNeeded(user, request.defaultAddress(), null);
        return AddressResponse.from(addressRepository.save(address));
    }

    @Transactional
    public AddressResponse updateAddress(AppUser user, Long id, AddressRequest request) {
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new com.daramassalik.backend.common.ResourceNotFoundException("Address not found"));
        apply(address, request);
        clearDefaultIfNeeded(user, request.defaultAddress(), id);
        return AddressResponse.from(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(AppUser user, Long id) {
        Address address = addressRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new com.daramassalik.backend.common.ResourceNotFoundException("Address not found"));
        addressRepository.delete(address);
    }

    private void apply(Address address, AddressRequest request) {
        address.setLabel(blankToNull(request.label()));
        address.setFirstName(request.firstName().trim());
        address.setLastName(request.lastName().trim());
        address.setAddressLine1(request.addressLine1().trim());
        address.setAddressLine2(blankToNull(request.addressLine2()));
        address.setPostalCode(request.postalCode().trim());
        address.setCity(request.city().trim());
        address.setCountry(request.country().trim());
        address.setDefaultAddress(request.defaultAddress());
    }

    private void clearDefaultIfNeeded(AppUser user, boolean makeDefault, Long exceptId) {
        if (!makeDefault) return;
        addressRepository.findAllByUserOrderByDefaultAddressDescCreatedAtAsc(user).stream()
                .filter(a -> exceptId == null || !a.getId().equals(exceptId))
                .filter(Address::isDefaultAddress)
                .forEach(a -> a.setDefaultAddress(false));
    }

    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
