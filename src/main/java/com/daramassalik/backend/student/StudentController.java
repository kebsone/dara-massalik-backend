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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
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


    @PostMapping(
            value = "/{id}/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(operationId = "uploadStudentPhoto", summary = "Upload or replace a student photo")
    public StudentResponse uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return studentService.uploadPhoto(id, file);
    }

    @GetMapping(
            value = "/{id}/photo",
            produces = MediaType.ALL_VALUE
    )
    @Operation(operationId = "getStudentPhoto", summary = "Get a student photo")
    public ResponseEntity<org.springframework.core.io.Resource> getPhoto(
            @PathVariable Long id) {
        return studentService.getPhoto(id);
    }

    @DeleteMapping("/{id}/photo")
    @Operation(operationId = "deleteStudentPhoto", summary = "Delete a student photo")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePhoto(@PathVariable Long id) {
        studentService.deletePhoto(id);
    }
}
