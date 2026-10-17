package com.daramassalik.backend.group;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@Tag(name = "Groups")
@SecurityRequirement(name = "bearerAuth")
public class GroupController {
    private final GroupService service;
    public GroupController(GroupService service) { this.service = service; }
    @GetMapping public List<GroupResponse> findAll() { return service.findAll(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public GroupResponse create(@Valid @RequestBody GroupRequest request) { return service.create(request); }
    @PutMapping("/{id}") public GroupResponse update(@PathVariable Long id, @Valid @RequestBody GroupRequest request) { return service.update(id, request); }
}
