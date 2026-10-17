package com.daramassalik.backend.payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface PaymentRepository extends JpaRepository<Payment, Long> { List<Payment> findByYearAndMonth(int year, int month); List<Payment> findByStudentIdOrderByYearDescMonthDesc(Long studentId); }
