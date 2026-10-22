
package com.daramassalik.backend.pedagogy;

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

    public PedagogicalTrackingController(
            PedagogicalTrackingService service
    ) {
        this.service = service;
    }

    /**
     * Crée ou met à jour le suivi de l'élève pour la date donnée.
     */
    @PostMapping
    public PedagogicalTrackingResponse saveOrUpdate(
            @Valid @RequestBody PedagogicalTrackingRequest request
    ) {
        return service.saveOrUpdate(request);
    }

    /**
     * Met à jour un suivi identifié par son ID.
     */
    @PutMapping("/{id}")
    public PedagogicalTrackingResponse update(
            @PathVariable Long id,
            @Valid @RequestBody PedagogicalTrackingRequest request
    ) {
        return service.update(id, request);
    }

    @GetMapping("/student/{studentId}")
    public List<PedagogicalTrackingResponse> findStudentHistory(
            @PathVariable Long studentId
    ) {
        return service.findStudentHistory(studentId);
    }
}
