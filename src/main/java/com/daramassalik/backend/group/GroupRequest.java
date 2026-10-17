
package com.daramassalik.backend.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GroupRequest(
        @NotBlank @Size(max = 100) String name,
        Long teacherId,
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "La couleur doit être au format hexadécimal, par exemple #2563EB")
        String color
) {}
