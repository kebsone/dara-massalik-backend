
package com.daramassalik.backend.group;

public record GroupResponse(
        Long id,
        String name,
        String color,
        Long teacherId,
        String teacherName
) {}
