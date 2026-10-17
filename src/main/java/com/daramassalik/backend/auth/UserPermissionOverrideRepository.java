
package com.daramassalik.backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserPermissionOverrideRepository
        extends JpaRepository<UserPermissionOverride, Long> {

    List<UserPermissionOverride> findByUser_Id(Long userId);

    Optional<UserPermissionOverride> findByUser_IdAndPermission(
            Long userId,
            Permission permission
    );
}
