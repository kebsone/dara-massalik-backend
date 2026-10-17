package com.daramassalik.backend.attendance;

import java.time.LocalDate;
public record AttendanceResponse(Long id, Long studentId, String studentName, LocalDate date, AttendanceStatus status, String comment, Long teacherId) {}
