package com.daramassalik.backend.profile;

import com.daramassalik.backend.auth.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUserOrderByDefaultAddressDescCreatedAtAsc(AppUser user);
    Optional<Address> findByIdAndUser(Long id, AppUser user);
}
