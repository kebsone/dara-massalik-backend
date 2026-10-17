package com.daramassalik.backend.member;

import com.daramassalik.backend.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class MemberService {
    private final MemberRepository repository;

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll(String search) {
        List<Member> members = search == null || search.isBlank()
                ? repository.findByActiveTrueOrderByLastNameAscFirstNameAsc()
                : repository.searchActive(search);
        return members.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public MemberResponse findById(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id)));
    }

    @Transactional
    public MemberResponse create(MemberRequest request) {
        Member member = new Member();
        apply(member, request);
        return toResponse(repository.save(member));
    }

    @Transactional
    public MemberResponse update(Long id, MemberRequest request) {
        Member member = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
        apply(member, request);
        return toResponse(repository.save(member));
    }

    @Transactional
    public void deactivate(Long id) {
        Member member = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + id));
        member.setActive(false);
    }

    private void apply(Member member, MemberRequest request) {
        member.setFirstName(request.firstName());
        member.setLastName(request.lastName());
        member.setEmail(request.email());
        member.setPhone(request.phone());
        member.setAddress(request.address());
        member.setJoinedDate(request.joinedDate() == null ? LocalDate.now() : request.joinedDate());
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(member.getId(), member.getFirstName(), member.getLastName(),
                member.getEmail(), member.getPhone(), member.getAddress(), member.getJoinedDate(), member.isActive());
    }
}
