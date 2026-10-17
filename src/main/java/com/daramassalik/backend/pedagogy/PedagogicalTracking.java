package com.daramassalik.backend.pedagogy;

import com.daramassalik.backend.student.Student;
import com.daramassalik.backend.teacher.Teacher;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "pedagogical_tracking",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pedagogical_tracking_student_date",
                columnNames = {"student_id", "date"}
        )
)
public class PedagogicalTracking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "student_id") private Student student;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "teacher_id") private Teacher teacher;
    @Column(nullable = false) private LocalDate date;
    @Column(length = 120) private String surah;
    @Column(name = "completed_today", length = 1000) private String completedToday;
    @Column(name = "to_prepare", length = 1000) private String toPrepare;
    @Column(length = 1500) private String observations;
    public Long getId() { return id; }
    public Student getStudent() { return student; } public void setStudent(Student student) { this.student = student; }
    public Teacher getTeacher() { return teacher; } public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    public LocalDate getDate() { return date; } public void setDate(LocalDate date) { this.date = date; }
    public String getSurah() { return surah; } public void setSurah(String surah) { this.surah = surah; }
    public String getCompletedToday() { return completedToday; } public void setCompletedToday(String v) { this.completedToday = v; }
    public String getToPrepare() { return toPrepare; } public void setToPrepare(String v) { this.toPrepare = v; }
    public String getObservations() { return observations; } public void setObservations(String v) { this.observations = v; }
}
