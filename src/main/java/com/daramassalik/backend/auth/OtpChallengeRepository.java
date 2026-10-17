package com.daramassalik.backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OtpChallengeRepository extends JpaRepository<OtpChallenge, UUID> {
    Optional<OtpChallenge> findTopByPhoneOrderByCreatedAtDesc(String phone);
}
