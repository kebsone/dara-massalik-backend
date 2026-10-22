package com.daramassalik.backend.sessiontracking;

import com.daramassalik.backend.attendance.AttendanceStatus;

/** Une ligne de la séance, avec présence et suivi pédagogique de la date demandée. */
public record SessionTrackingRowResponse(
        Long studentId,
        String firstName,
        String lastName,
        boolean active,
        Long groupId,
        String groupName,
        String groupColor,
        String photoUrl,
        AttendanceData attendance,
        PedagogicalTrackingData pedagogicalTracking
) {
    public record AttendanceData(Long id, AttendanceStatus status, String comment) {}
    public record PedagogicalTrackingData(
            Long id, String surah, String completedToday, String toPrepare, String observations
    ) {}
}
