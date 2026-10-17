package com.daramassalik.backend.commission;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/commissions/{commissionId}/members")
@Tag(name = "Commission members", description = "Member assignments to Dahira commissions")
@SecurityRequirement(name = "bearerAuth")
public class CommissionMemberController {
    private final CommissionMemberService service;

    public CommissionMemberController(CommissionMemberService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List members assigned to a commission")
    public List<CommissionMemberResponse> findByCommission(@PathVariable Long commissionId) {
        return service.findByCommission(commissionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Assign a member to a commission")
    public CommissionMemberResponse add(@PathVariable Long commissionId,
                                         @Valid @RequestBody CommissionMemberRequest request) {
        return service.add(commissionId, request);
    }

    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a member from a commission")
    public void remove(@PathVariable Long commissionId, @PathVariable Long memberId) {
        service.remove(commissionId, memberId);
    }
}
