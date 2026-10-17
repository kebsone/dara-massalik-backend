package com.daramassalik.backend.commission;

import java.time.LocalDate;

public record CommissionMemberResponse(
        Long id,
        Long commissionId,
        Long memberId,
        String memberName,
        String roleLabel,
        LocalDate joinedDate
) {}
