package com.daramassalik.backend.payment;
import java.math.BigDecimal; import java.time.LocalDate;
public record PaymentResponse(Long id, Long studentId, String studentName, int month, int year, BigDecimal amount, LocalDate paymentDate, String comment) {}
