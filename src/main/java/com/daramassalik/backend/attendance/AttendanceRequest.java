package com.daramassalik.backend.attendance;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AttendanceRequest(@NotNull Long studentId, @NotNull LocalDate date, @NotNull AttendanceStatus status, String comment, Long teacherId) {}
