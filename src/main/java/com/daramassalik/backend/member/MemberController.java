package com.daramassalik.backend.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Members", description = "Dahira member management")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {
    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List active Dahira members")
    public List<MemberResponse> findAll(@RequestParam(required = false) String search) {
        return service.findAll(search);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a Dahira member")
    public MemberResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Dahira member")
    public MemberResponse create(@Valid @RequestBody MemberRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Dahira member")
    public MemberResponse update(@PathVariable Long id, @Valid @RequestBody MemberRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deactivate a Dahira member")
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }
}
