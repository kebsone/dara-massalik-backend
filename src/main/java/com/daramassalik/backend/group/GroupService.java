package com.daramassalik.backend.group;

import com.daramassalik.backend.common.ResourceNotFoundException;
import com.daramassalik.backend.teacher.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class GroupService {
    private final GroupRepository repository;
    private final TeacherRepository teacherRepository;

    public GroupService(GroupRepository repository, TeacherRepository teacherRepository) {
        this.repository = repository;
        this.teacherRepository = teacherRepository;
    }

    @Transactional(readOnly = true)
    public List<GroupResponse> findAll() { return repository.findAll().stream().map(this::toResponse).toList(); }

    @Transactional
    public GroupResponse create(GroupRequest request) { SchoolGroup group = new SchoolGroup(); apply(group, request); return toResponse(repository.save(group)); }

    @Transactional
    public GroupResponse update(Long id, GroupRequest request) {
        SchoolGroup group = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Group not found: " + id));
        apply(group, request);
        return toResponse(repository.save(group));
    }


    private void apply(SchoolGroup group, GroupRequest request) {
        group.setName(request.name().trim());

        group.setColor(
                request.color() == null || request.color().isBlank()
                        ? (group.getColor() == null ? "#2563EB" : group.getColor())
                        : request.color()
        );

        group.setTeacher(
                request.teacherId() == null
                        ? null
                        : teacherRepository.findById(request.teacherId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Teacher not found: " + request.teacherId()
                                )
                        )
        );
    }

    private GroupResponse toResponse(SchoolGroup group) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getColor(),
                group.getTeacher() == null ? null : group.getTeacher().getId(),
                group.getTeacher() == null
                        ? null
                        : group.getTeacher().getFirstName() + " "
                        + group.getTeacher().getLastName()
        );
    }

}
