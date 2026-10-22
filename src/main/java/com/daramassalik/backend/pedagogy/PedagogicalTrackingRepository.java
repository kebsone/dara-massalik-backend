package com.daramassalik.backend.pedagogy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedagogicalTrackingRepository extends JpaRepository<PedagogicalTracking, Long> {
    List<PedagogicalTracking> findByStudentIdOrderByDateDesc(Long studentId);

    Optional<PedagogicalTracking> findByStudentIdAndDate(Long studentId, LocalDate date);

    // Charge tous les suivis de la date pour les élèves de la page en une requête.
    List<PedagogicalTracking> findByDateAndStudentIdIn(LocalDate date, List<Long> studentIds);
}
