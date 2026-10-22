package com.daramassalik.backend.sessiontracking;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/session-tracking")
@Tag(name = "Session Tracking")
@SecurityRequirement(name = "bearerAuth")
public class SessionTrackingController {
    private final SessionTrackingService service;

    public SessionTrackingController(SessionTrackingService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(operationId = "getSessionTrackingPage",
            summary = "Get a paginated student list with attendance and pedagogical tracking for a date")
    public Page<SessionTrackingRowResponse> getSessionTrackingPage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.findSession(date, search, page, size);
    }
}
