package com.daramassalik.backend.teacher;

import com.daramassalik.backend.auth.AppUser;
import jakarta.persistence.*;

@Entity
@Table(name = "teachers")
public class Teacher {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80) private String firstName;
    @Column(nullable = false, length = 80) private String lastName;
    @Column(length = 160) private String email;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private AppUser user;

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
}
