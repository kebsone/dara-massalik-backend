package com.daramassalik.backend.attendance;

import com.daramassalik.backend.student.Student;
import com.daramassalik.backend.teacher.Teacher;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "attendance_date"}))
public class Attendance {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "student_id") private Student student;
    @Column(name = "attendance_date", nullable = false) private LocalDate date;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30) private AttendanceStatus status;
    @Column(length = 500) private String comment;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "teacher_id") private Teacher teacher;
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
}
