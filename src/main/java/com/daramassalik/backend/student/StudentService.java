package com.daramassalik.backend.student;

import com.daramassalik.backend.common.ResourceNotFoundException;
import com.daramassalik.backend.group.GroupRepository;
import com.daramassalik.backend.group.SchoolGroup;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;
    private final StudentPhotoService studentPhotoService;

    public StudentService(
            StudentRepository studentRepository,
            GroupRepository groupRepository,
            StudentPhotoService studentPhotoService
    ) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.studentPhotoService = studentPhotoService;
    }

    @Transactional
    public StudentResponse uploadPhoto(Long id, MultipartFile file) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found: " + id));

        String oldPhotoPath = student.getPhotoPath();

        // Enregistrer la nouvelle photo sur le disque.
        String newPhotoPath = studentPhotoService.store(file);

        try {
            student.setPhotoPath(newPhotoPath);
            Student savedStudent = studentRepository.saveAndFlush(student);

            // Supprimer l'ancienne photo seulement après la sauvegarde.
            if (oldPhotoPath != null && !oldPhotoPath.equals(newPhotoPath)) {
                studentPhotoService.delete(oldPhotoPath);
            }

            return toResponse(savedStudent);

        } catch (RuntimeException e) {
            // Nettoyer le nouveau fichier si la sauvegarde échoue.
            studentPhotoService.delete(newPhotoPath);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> getPhoto(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found: " + id));

        if (student.getPhotoPath() == null) {
            throw new ResourceNotFoundException("Photo not found for student: " + id);
        }

        StudentPhotoService.StoredPhoto photo =
                studentPhotoService.load(student.getPhotoPath());

        return ResponseEntity.ok()
                .contentType(photo.mediaType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header(HttpHeaders.CACHE_CONTROL, "private, no-store")
                .header("X-Content-Type-Options", "nosniff")
                .body(photo.resource());
    }

    @Transactional
    public void deletePhoto(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student not found: " + id));

        String photoPath = student.getPhotoPath();

        if (photoPath == null) {
            return;
        }

        // Vider la référence en base avant de supprimer le fichier.
        student.setPhotoPath(null);
        studentRepository.saveAndFlush(student);

        studentPhotoService.delete(photoPath);
    }



    @Transactional(readOnly = true)
    public Page<StudentResponse> findAll(String search, Long groupId, Pageable pageable) {
        String normalizedSearch = (search == null || search.isBlank())
                ? null
                : search.trim().toLowerCase(Locale.ROOT);

        Specification<Student> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isTrue(root.get("active")));

            if (normalizedSearch != null) {
                String pattern = "%" + normalizedSearch + "%";
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("firstName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.<String>get("lastName")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(root.<String>get("parentName"), "")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(root.<String>get("phone"), "")), pattern),
                        criteriaBuilder.like(criteriaBuilder.lower(criteriaBuilder.coalesce(root.<String>get("parentPhone"), "")), pattern)
                ));
            }

            if (groupId != null) {
                predicates.add(criteriaBuilder.equal(root.get("group").get("id"), groupId));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };

        return studentRepository.findAll(specification, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public StudentResponse findById(Long id) {
        return toResponse(studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id)));
    }

    @Transactional
    public StudentResponse create(StudentRequest request) {
        Student student = new Student();
        apply(student, request);
        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
        apply(student, request);
        return toResponse(studentRepository.save(student));
    }

    @Transactional
    public void deactivate(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
        student.setActive(false);
    }

    private void apply(Student student, StudentRequest request) {
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setBirthDate(request.birthDate());
        student.setPhone(request.phone());
        student.setParentName(request.parentName());
        student.setParentPhone(request.parentPhone());
        if (request.groupId() == null) {
            student.setGroup(null);
        } else {
            SchoolGroup group = groupRepository.findById(request.groupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found: " + request.groupId()));
            student.setGroup(group);
        }
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getFirstName(),
                student.getLastName(),
                student.getBirthDate(),
                student.getPhone(),
                student.getParentName(),
                student.getParentPhone(),
                student.isActive(),
                student.getGroup() != null ? student.getGroup().getId() : null,
                student.getGroup() != null ? student.getGroup().getName() : null,
                student.getGroup() != null ? student.getGroup().getColor() : null,
                student.getPhotoPath() != null
                        ? "/api/students/" + student.getId() + "/photo"
                        : null
        );
    }
}
