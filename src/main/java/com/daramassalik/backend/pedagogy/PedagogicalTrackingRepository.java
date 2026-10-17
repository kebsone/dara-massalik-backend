package com.daramassalik.backend.pedagogy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PedagogicalTrackingRepository extends JpaRepository<PedagogicalTracking, Long> {
    List<PedagogicalTracking> findByStudentIdOrderByDateDesc(Long studentId);
}
