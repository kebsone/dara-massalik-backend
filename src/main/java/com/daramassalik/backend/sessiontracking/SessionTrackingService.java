package com.daramassalik.backend.sessiontracking;

import com.daramassalik.backend.attendance.Attendance;
import com.daramassalik.backend.attendance.AttendanceRepository;
import com.daramassalik.backend.pedagogy.PedagogicalTracking;
import com.daramassalik.backend.pedagogy.PedagogicalTrackingRepository;
import com.daramassalik.backend.student.Student;
import com.daramassalik.backend.student.StudentRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionTrackingService {
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final PedagogicalTrackingRepository trackingRepository;

    public SessionTrackingService(StudentRepository studentRepository,
                                  AttendanceRepository attendanceRepository,
                                  PedagogicalTrackingRepository trackingRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.trackingRepository = trackingRepository;
    }

    @Transactional(readOnly = true)
    public Page<SessionTrackingRowResponse> findSession(LocalDate date, String search, int page, int size) {
        if (date == null) throw new IllegalArgumentException("La date de séance est obligatoire.");
        if (page < 0) throw new IllegalArgumentException("Le numéro de page doit être supérieur ou égal à 0.");
        if (size < 1 || size > 100) throw new IllegalArgumentException("La taille doit être comprise entre 1 et 100.");

        String term = search == null || search.isBlank() ? null : search.trim().toLowerCase(Locale.ROOT);
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.asc("firstName").ignoreCase(),
                Sort.Order.asc("lastName").ignoreCase(),
                Sort.Order.asc("id")
        ));

        Specification<Student> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isTrue(root.get("active")));
            if (term != null) {
                String pattern = "%" + term + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.<String>get("firstName")), pattern),
                        cb.like(cb.lower(root.<String>get("lastName")), pattern)
                ));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };

        Page<Student> studentPage = studentRepository.findAll(spec, pageable);
        List<Long> ids = studentPage.getContent().stream().map(Student::getId).toList();
        Map<Long, Attendance> attendanceMap = new HashMap<>();
        Map<Long, PedagogicalTracking> trackingMap = new HashMap<>();

        if (!ids.isEmpty()) {
            attendanceRepository.findByDateAndStudentIdIn(date, ids)
                    .forEach(a -> attendanceMap.put(a.getStudent().getId(), a));
            trackingRepository.findByDateAndStudentIdIn(date, ids)
                    .forEach(t -> trackingMap.put(t.getStudent().getId(), t));
        }

        List<SessionTrackingRowResponse> content = studentPage.getContent().stream()
                .map(student -> toResponse(student, attendanceMap.get(student.getId()), trackingMap.get(student.getId())))
                .toList();
        return new PageImpl<>(content, pageable, studentPage.getTotalElements());
    }

    private SessionTrackingRowResponse toResponse(Student student, Attendance attendance, PedagogicalTracking tracking) {
        var attendanceData = attendance == null ? null
                : new SessionTrackingRowResponse.AttendanceData(attendance.getId(), attendance.getStatus(), attendance.getComment());
        var trackingData = tracking == null ? null
                : new SessionTrackingRowResponse.PedagogicalTrackingData(
                tracking.getId(), tracking.getSurah(), tracking.getCompletedToday(),
                tracking.getToPrepare(), tracking.getObservations());

        return new SessionTrackingRowResponse(
                student.getId(), student.getFirstName(), student.getLastName(), student.isActive(),
                student.getGroup() == null ? null : student.getGroup().getId(),
                student.getGroup() == null ? null : student.getGroup().getName(),
                student.getGroup() == null ? null : student.getGroup().getColor(),
                student.getPhotoPath() == null ? null : "/api/students/" + student.getId() + "/photo",
                attendanceData, trackingData);
    }
}
