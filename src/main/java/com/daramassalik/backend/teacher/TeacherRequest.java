package com.daramassalik.backend.teacher;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TeacherRequest(@NotBlank @Size(max = 80) String firstName, @NotBlank @Size(max = 80) String lastName, @Email @Size(max = 160) String email) {}
