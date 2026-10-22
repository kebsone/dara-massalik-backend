
package com.daramassalik.backend.pedagogy;

import com.daramassalik.backend.common.ResourceNotFoundException;
import com.daramassalik.backend.student.Student;
import com.daramassalik.backend.student.StudentRepository;
import com.daramassalik.backend.teacher.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedagogicalTrackingService {

    private final PedagogicalTrackingRepository repository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    public PedagogicalTrackingService(
            PedagogicalTrackingRepository repository,
            StudentRepository studentRepository,
            TeacherRepository teacherRepository
    ) {
        this.repository = repository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    /**
     * Crée un suivi s'il n'existe pas encore pour cet élève et cette date.
     * Sinon, met à jour le suivi existant.
     */
    @Transactional
    public PedagogicalTrackingResponse saveOrUpdate(
            PedagogicalTrackingRequest request
    ) {
        Student student = findStudent(request.studentId());

        PedagogicalTracking entity = repository
                .findByStudentIdAndDate(
                        request.studentId(),
                        request.date()
                )
                .orElseGet(PedagogicalTracking::new);

        entity.setStudent(student);
        entity.setDate(request.date());
        applyRequest(entity, request);

        return toResponse(repository.save(entity));
    }

    /**
     * Mise à jour explicite d'un suivi existant.
     */
    @Transactional
    public PedagogicalTrackingResponse update(
            Long id,
            PedagogicalTrackingRequest request
    ) {
        PedagogicalTracking entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Pedagogical tracking not found: " + id
                        )
                );

        Student student = findStudent(request.studentId());

        // Empêche de modifier le suivi d'un autre élève à son insu.
        if (!entity.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException(
                    "The student does not match the tracking record"
            );
        }

        entity.setDate(request.date());
        applyRequest(entity, request);

        return toResponse(repository.save(entity));
    }

    private void applyRequest(
            PedagogicalTracking entity,
            PedagogicalTrackingRequest request
    ) {
        entity.setSurah(request.surah());
        entity.setCompletedToday(request.completedToday());
        entity.setToPrepare(request.toPrepare());
        entity.setObservations(request.observations());

        if (request.teacherId() != null) {
            var teacher = teacherRepository.findById(request.teacherId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Teacher not found: " + request.teacherId()
                            )
                    );

            entity.setTeacher(teacher);
        } else {
            entity.setTeacher(null);
        }
    }

    private Student findStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Student not found: " + studentId
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<PedagogicalTrackingResponse> findStudentHistory(
            Long studentId
    ) {
        return repository.findByStudentIdOrderByDateDesc(studentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private PedagogicalTrackingResponse toResponse(
            PedagogicalTracking tracking
    ) {
        return new PedagogicalTrackingResponse(
                tracking.getId(),
                tracking.getStudent().getId(),
                tracking.getStudent().getFirstName()
                        + " "
                        + tracking.getStudent().getLastName(),
                tracking.getTeacher() == null
                        ? null
                        : tracking.getTeacher().getId(),
                tracking.getDate(),
                tracking.getSurah(),
                tracking.getCompletedToday(),
                tracking.getToPrepare(),
                tracking.getObservations()
        );
    }
}
