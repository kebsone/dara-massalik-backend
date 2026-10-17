package com.daramassalik.backend.group;

import com.daramassalik.backend.teacher.Teacher;
import jakarta.persistence.*;

@Entity
@Table(name = "school_groups")
public class SchoolGroup {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
    @Column(nullable = false, length = 7)
    private String color = "#2563EB";
    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
