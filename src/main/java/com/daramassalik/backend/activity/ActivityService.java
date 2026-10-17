package com.daramassalik.backend.activity;

import com.daramassalik.backend.commission.Commission;
import com.daramassalik.backend.commission.CommissionRepository;
import com.daramassalik.backend.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository repository;
    private final CommissionRepository commissionRepository;

    public ActivityService(ActivityRepository repository, CommissionRepository commissionRepository) {
        this.repository = repository;
        this.commissionRepository = commissionRepository;
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> findAll(boolean upcomingOnly) {
        List<Activity> activities = upcomingOnly
                ? repository.findByActivityDateGreaterThanEqualOrderByActivityDateAsc(LocalDate.now())
                : repository.findAllByOrderByActivityDateAsc();
        return activities.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ActivityResponse findById(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + id)));
    }

    @Transactional
    public ActivityResponse create(ActivityRequest request) {
        Activity activity = new Activity();
        apply(activity, request);
        return toResponse(repository.save(activity));
    }

    @Transactional
    public ActivityResponse update(Long id, ActivityRequest request) {
        Activity activity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found: " + id));
        apply(activity, request);
        return toResponse(repository.save(activity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Activity not found: " + id);
        }
        repository.deleteById(id);
    }

    private void apply(Activity activity, ActivityRequest request) {
        activity.setName(request.name());
        activity.setDescription(request.description());
        activity.setActivityDate(request.activityDate());
        activity.setLocation(request.location());
        activity.setStatus(request.status());
        activity.setCommission(request.commissionId() == null ? null : commissionRepository.findById(request.commissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found: " + request.commissionId())));
    }

    private ActivityResponse toResponse(Activity activity) {
        Commission commission = activity.getCommission();
        return new ActivityResponse(activity.getId(), activity.getName(), activity.getDescription(), activity.getActivityDate(),
                activity.getLocation(), activity.getStatus(), commission == null ? null : commission.getId(),
                commission == null ? null : commission.getName());
    }
}
