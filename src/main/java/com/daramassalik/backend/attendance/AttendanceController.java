package com.daramassalik.backend.attendance;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@Tag(name = "Attendance")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {
    private final AttendanceService service;
    public AttendanceController(AttendanceService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public AttendanceResponse create(@Valid @RequestBody AttendanceRequest request) { return service.create(request); }
    @GetMapping
    public List<AttendanceResponse> findByDateRange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) { return service.findByDateRange(from, to); }






    @GetMapping("/student/{studentId}")
    public List<AttendanceResponse> findStudentHistory(@PathVariable Long studentId) { return service.findStudentHistory(studentId); }
}
