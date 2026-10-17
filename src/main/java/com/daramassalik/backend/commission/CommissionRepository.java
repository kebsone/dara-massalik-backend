package com.daramassalik.backend.commission;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommissionRepository extends JpaRepository<Commission, Long> {
    List<Commission> findByActiveTrueOrderByNameAsc();
}
