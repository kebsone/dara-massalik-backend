package com.daramassalik.backend.student.tracking;

import com.daramassalik.backend.attendance.AttendanceResponse;
import com.daramassalik.backend.pedagogy.PedagogicalTrackingResponse;

import java.time.LocalDate;

public record StudentTrackingHistoryEntry(
        LocalDate date,
        AttendanceResponse attendance,
        PedagogicalTrackingResponse pedagogicalTracking
) {}