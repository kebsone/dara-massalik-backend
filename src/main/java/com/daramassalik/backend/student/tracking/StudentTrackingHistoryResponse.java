package com.daramassalik.backend.student.tracking;

import java.util.List;

public record StudentTrackingHistoryResponse(
        Long studentId,
        String studentName,
        List<StudentTrackingHistoryEntry> history
) {}