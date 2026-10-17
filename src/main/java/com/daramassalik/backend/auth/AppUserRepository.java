package com.daramassalik.backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmailIgnoreCase(String email);
    Optional<AppUser> findByPhone(String phone);

    @Query("select count(u) from AppUser u where :role member of u.roles")
    long countUsersWithRole(@Param("role") Role role);
}
