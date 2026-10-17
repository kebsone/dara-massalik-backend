package com.daramassalik.backend.commission;

import com.daramassalik.backend.common.ResourceNotFoundException;
import com.daramassalik.backend.member.Member;
import com.daramassalik.backend.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class CommissionMemberService {
    private final CommissionMemberRepository repository;
    private final CommissionRepository commissionRepository;
    private final MemberRepository memberRepository;

    public CommissionMemberService(CommissionMemberRepository repository,
                                   CommissionRepository commissionRepository,
                                   MemberRepository memberRepository) {
        this.repository = repository;
        this.commissionRepository = commissionRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<CommissionMemberResponse> findByCommission(Long commissionId) {
        ensureCommission(commissionId);
        return repository.findByCommissionIdOrderByMemberLastNameAscMemberFirstNameAsc(commissionId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public CommissionMemberResponse add(Long commissionId, CommissionMemberRequest request) {
        Commission commission = ensureCommission(commissionId);
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + request.memberId()));

        repository.findByCommissionIdAndMemberId(commissionId, request.memberId()).ifPresent(existing -> {
            throw new IllegalArgumentException("Member is already assigned to this commission");
        });

        CommissionMember assignment = new CommissionMember();
        assignment.setCommission(commission);
        assignment.setMember(member);
        assignment.setRoleLabel(request.roleLabel());
        assignment.setJoinedDate(request.joinedDate() == null ? LocalDate.now() : request.joinedDate());
        return toResponse(repository.save(assignment));
    }

    @Transactional
    public void remove(Long commissionId, Long memberId) {
        CommissionMember assignment = repository.findByCommissionIdAndMemberId(commissionId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission member assignment not found"));
        repository.delete(assignment);
    }

    private Commission ensureCommission(Long commissionId) {
        return commissionRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found: " + commissionId));
    }

    private CommissionMemberResponse toResponse(CommissionMember assignment) {
        Member member = assignment.getMember();
        return new CommissionMemberResponse(assignment.getId(), assignment.getCommission().getId(), member.getId(),
                member.getFirstName() + " " + member.getLastName(), assignment.getRoleLabel(), assignment.getJoinedDate());
    }
}
