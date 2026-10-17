package com.daramassalik.backend.payment;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record PaymentRequest(@NotNull Long studentId, @Min(1) @Max(12) int month, @Min(2000) int year, @NotNull @DecimalMin("0.00") BigDecimal amount, @NotNull LocalDate paymentDate, String comment) {}
