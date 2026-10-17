package com.daramassalik.backend.attendance;

import com.daramassalik.backend.common.ResourceNotFoundException;
import com.daramassalik.backend.student.Student;
import com.daramassalik.backend.student.StudentRepository;
import com.daramassalik.backend.teacher.Teacher;
import com.daramassalik.backend.teacher.TeacherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class AttendanceService {
    private final AttendanceRepository repository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    public AttendanceService(AttendanceRepository repository, StudentRepository studentRepository, TeacherRepository teacherRepository) {
        this.repository = repository; this.studentRepository = studentRepository; this.teacherRepository = teacherRepository;
    }
    @Transactional
    public AttendanceResponse create(AttendanceRequest request) {
        Student student = studentRepository.findById(request.studentId()).orElseThrow(() -> new ResourceNotFoundException("Student not found: " + request.studentId()));
        Attendance attendance = new Attendance(); attendance.setStudent(student); attendance.setDate(request.date()); attendance.setStatus(request.status()); attendance.setComment(request.comment());
        if (request.teacherId() != null) attendance.setTeacher(teacherRepository.findById(request.teacherId()).orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + request.teacherId())));
        return toResponse(repository.save(attendance));
    }
    @Transactional(readOnly = true)
    public List<AttendanceResponse> findByDateRange(LocalDate from, LocalDate to) { return repository.findByDateBetween(from, to).stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true)
    public List<AttendanceResponse> findStudentHistory(Long studentId) { return repository.findByStudentIdOrderByDateDesc(studentId).stream().map(this::toResponse).toList(); }
    private AttendanceResponse toResponse(Attendance a) { return new AttendanceResponse(a.getId(), a.getStudent().getId(), a.getStudent().getFirstName() + " " + a.getStudent().getLastName(), a.getDate(), a.getStatus(), a.getComment(), a.getTeacher() == null ? null : a.getTeacher().getId()); }
}
