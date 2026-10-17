package com.daramassalik.backend.activity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/activities")
@Tag(name = "Activities", description = "Dahira activity management")
@SecurityRequirement(name = "bearerAuth")
public class ActivityController {
    private final ActivityService service;

    public ActivityController(ActivityService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List Dahira activities")
    public List<ActivityResponse> findAll(@RequestParam(defaultValue = "false") boolean upcomingOnly) {
        return service.findAll(upcomingOnly);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an activity")
    public ActivityResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Dahira activity")
    public ActivityResponse create(@Valid @RequestBody ActivityRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Dahira activity")
    public ActivityResponse update(@PathVariable Long id, @Valid @RequestBody ActivityRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a Dahira activity")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
