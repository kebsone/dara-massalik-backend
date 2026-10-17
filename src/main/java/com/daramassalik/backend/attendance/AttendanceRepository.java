package com.daramassalik.backend.attendance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByDateBetween(LocalDate from, LocalDate to);
    List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);
}
