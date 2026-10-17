package com.daramassalik.backend.student.tracking;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Student Tracking History")
@SecurityRequirement(name = "bearerAuth")
public class StudentTrackingHistoryController {

    private final StudentTrackingHistoryService service;

    public StudentTrackingHistoryController(
            StudentTrackingHistoryService service) {
        this.service = service;
    }

    @GetMapping("/{studentId}/tracking-history")
    public StudentTrackingHistoryResponse getHistory(
            @PathVariable Long studentId) {
        return service.getHistory(studentId);
    }
}