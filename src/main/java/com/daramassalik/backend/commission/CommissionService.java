package com.daramassalik.backend.commission;

import com.daramassalik.backend.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CommissionService {
    private final CommissionRepository repository;

    public CommissionService(CommissionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<CommissionResponse> findAll() {
        return repository.findByActiveTrueOrderByNameAsc().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CommissionResponse findById(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found: " + id)));
    }

    @Transactional
    public CommissionResponse create(CommissionRequest request) {
        Commission commission = new Commission();
        apply(commission, request);
        return toResponse(repository.save(commission));
    }

    @Transactional
    public CommissionResponse update(Long id, CommissionRequest request) {
        Commission commission = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found: " + id));
        apply(commission, request);
        return toResponse(repository.save(commission));
    }

    @Transactional
    public void deactivate(Long id) {
        Commission commission = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found: " + id));
        commission.setActive(false);
    }

    private void apply(Commission commission, CommissionRequest request) {
        commission.setName(request.name());
        commission.setDescription(request.description());
    }

    private CommissionResponse toResponse(Commission commission) {
        return new CommissionResponse(commission.getId(), commission.getName(), commission.getDescription(), commission.isActive());
    }
}
