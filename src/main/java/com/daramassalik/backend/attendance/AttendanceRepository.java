package com.daramassalik.backend.attendance;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByDateBetween(LocalDate from, LocalDate to);

    List<Attendance> findByStudentIdOrderByDateDesc(Long studentId);

    // Charge toutes les présences d'une date pour les élèves de la page en une requête.
    List<Attendance> findByDateAndStudentIdIn(LocalDate date, List<Long> studentIds);
}
