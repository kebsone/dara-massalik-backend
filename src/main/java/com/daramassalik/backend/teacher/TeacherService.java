package com.daramassalik.backend.teacher;

import com.daramassalik.backend.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TeacherService {
    private final TeacherRepository repository;
    public TeacherService(TeacherRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true) public List<TeacherResponse> findAll() { return repository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional public TeacherResponse create(TeacherRequest request) { Teacher t = new Teacher(); apply(t,request); return toResponse(repository.save(t)); }
    @Transactional public TeacherResponse update(Long id, TeacherRequest request) { Teacher t = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Teacher not found: "+id)); apply(t,request); return toResponse(repository.save(t)); }
    private void apply(Teacher t, TeacherRequest r){t.setFirstName(r.firstName());t.setLastName(r.lastName());t.setEmail(r.email());}
    private TeacherResponse toResponse(Teacher t){return new TeacherResponse(t.getId(),t.getFirstName(),t.getLastName(),t.getEmail());}
}
