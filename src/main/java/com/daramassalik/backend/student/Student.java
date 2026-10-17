package com.daramassalik.backend.student;

import com.daramassalik.backend.auth.AppUser;
import com.daramassalik.backend.group.SchoolGroup;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80) private String firstName;
    @Column(nullable = false, length = 80) private String lastName;
    private LocalDate birthDate;
    @Column(length = 30) private String phone;
    @Column(length = 160) private String parentName;
    @Column(length = 30) private String parentPhone;
    @Column(nullable = false) private boolean active = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private AppUser user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private SchoolGroup group;

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }
    public String getParentPhone() { return parentPhone; }
    public void setParentPhone(String parentPhone) { this.parentPhone = parentPhone; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public SchoolGroup getGroup() { return group; }
    public void setGroup(SchoolGroup group) { this.group = group; }
}
