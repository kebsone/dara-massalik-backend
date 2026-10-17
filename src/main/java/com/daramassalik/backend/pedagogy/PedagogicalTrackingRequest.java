package com.daramassalik.backend.pedagogy;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
public record PedagogicalTrackingRequest(@NotNull Long studentId, Long teacherId, @NotNull LocalDate date, String surah, String completedToday, String toPrepare, String observations) {}
