package com.daramassalik.backend.order;

import com.daramassalik.backend.auth.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserOrderByCreatedAtDesc(AppUser user);
    Optional<Order> findByIdAndUser(Long id, AppUser user);
}
