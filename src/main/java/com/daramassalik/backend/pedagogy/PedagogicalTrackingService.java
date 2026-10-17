package com.daramassalik.backend.pedagogy;

import com.daramassalik.backend.common.ResourceNotFoundException;
import com.daramassalik.backend.student.StudentRepository;
import com.daramassalik.backend.teacher.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedagogicalTrackingService {
    private final PedagogicalTrackingRepository repository; private final StudentRepository studentRepository; private final TeacherRepository teacherRepository;
    public PedagogicalTrackingService(PedagogicalTrackingRepository repository, StudentRepository studentRepository, TeacherRepository teacherRepository) { this.repository = repository; this.studentRepository = studentRepository; this.teacherRepository = teacherRepository; }
    @Transactional
    public PedagogicalTrackingResponse create(PedagogicalTrackingRequest request) {
        var student = studentRepository.findById(request.studentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found: " + request.studentId()));
        var entity = new PedagogicalTracking(); entity.setStudent(student); entity.setDate(request.date()); entity.setSurah(request.surah()); entity.setCompletedToday(request.completedToday()); entity.setToPrepare(request.toPrepare()); entity.setObservations(request.observations());
        if (request.teacherId() != null) entity.setTeacher(teacherRepository.findById(request.teacherId()).orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + request.teacherId())));
        return toResponse(repository.save(entity));
    }
    @Transactional(readOnly = true)
    public List<PedagogicalTrackingResponse> findStudentHistory(Long studentId) { return repository.findByStudentIdOrderByDateDesc(studentId).stream().map(this::toResponse).toList(); }
    private PedagogicalTrackingResponse toResponse(PedagogicalTracking t) { return new PedagogicalTrackingResponse(t.getId(), t.getStudent().getId(), t.getStudent().getFirstName()+" "+t.getStudent().getLastName(), t.getTeacher()==null?null:t.getTeacher().getId(), t.getDate(), t.getSurah(), t.getCompletedToday(), t.getToPrepare(), t.getObservations()); }
}
