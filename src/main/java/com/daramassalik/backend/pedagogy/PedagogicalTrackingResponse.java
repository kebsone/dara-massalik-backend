package com.daramassalik.backend.pedagogy;
import java.time.LocalDate;
public record PedagogicalTrackingResponse(Long id, Long studentId, String studentName, Long teacherId, LocalDate date, String surah, String completedToday, String toPrepare, String observations) {}
