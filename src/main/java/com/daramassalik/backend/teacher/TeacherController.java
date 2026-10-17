package com.daramassalik.backend.teacher;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@Tag(name="Teachers")
@SecurityRequirement(name="bearerAuth")
public class TeacherController {
    private final TeacherService service;
    public TeacherController(TeacherService service){this.service=service;}
    @GetMapping public List<TeacherResponse> findAll(){return service.findAll();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public TeacherResponse create(@Valid @RequestBody TeacherRequest request){return service.create(request);}
    @PutMapping("/{id}") public TeacherResponse update(@PathVariable Long id,@Valid @RequestBody TeacherRequest request){return service.update(id,request);}
}
