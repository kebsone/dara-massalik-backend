package com.daramassalik.backend.commission;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CommissionMemberRequest(
        @NotNull Long memberId,
        @Size(max = 120) String roleLabel,
        LocalDate joinedDate
) {}
