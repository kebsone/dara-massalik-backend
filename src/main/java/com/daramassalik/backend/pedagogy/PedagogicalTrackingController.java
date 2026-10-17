package com.daramassalik.backend.pedagogy;
import com.daramassalik.backend.student.tracking.StudentTrackingHistoryResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/pedagogical-tracking")
@Tag(name = "Pedagogical Tracking")
@SecurityRequirement(name = "bearerAuth")
public class PedagogicalTrackingController {
    private final PedagogicalTrackingService service;
    public PedagogicalTrackingController(PedagogicalTrackingService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public PedagogicalTrackingResponse create(@Valid @RequestBody PedagogicalTrackingRequest request) { return service.create(request); }
    @GetMapping("/student/{studentId}")
    public List<PedagogicalTrackingResponse> findStudentHistory(@PathVariable Long studentId) { return service.findStudentHistory(studentId); }


}
