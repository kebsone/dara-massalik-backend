package com.daramassalik.backend.commission;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CommissionMemberRepository extends JpaRepository<CommissionMember, Long> {
    List<CommissionMember> findByCommissionIdOrderByMemberLastNameAscMemberFirstNameAsc(Long commissionId);
    Optional<CommissionMember> findByCommissionIdAndMemberId(Long commissionId, Long memberId);
}
