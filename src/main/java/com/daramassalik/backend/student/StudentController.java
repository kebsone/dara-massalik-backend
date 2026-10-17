package com.daramassalik.backend.student;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {
    private final StudentService studentService;
    public StudentController(StudentService studentService) { this.studentService = studentService; }

    @GetMapping
    @Operation(operationId = "listStudents", summary = "List active students")
    public Page<StudentResponse> findAll(@RequestParam(required = false) String search,
                                         @RequestParam(required = false) Long groupId,
                                         @ParameterObject @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {
        return studentService.findAll(search, groupId, pageable);
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getStudentById", summary = "Get a student by ID")
    public StudentResponse findById(@PathVariable Long id) { return studentService.findById(id); }

    @PostMapping
    @Operation(operationId = "createStudent", summary = "Create a student")
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@Valid @RequestBody StudentRequest request) { return studentService.create(request); }

    @PutMapping("/{id}")
    @Operation(operationId = "updateStudent", summary = "Update a student")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) { return studentService.update(id, request); }

    @DeleteMapping("/{id}")
    @Operation(operationId = "deactivateStudent", summary = "Deactivate a student")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) { studentService.deactivate(id); }
}
