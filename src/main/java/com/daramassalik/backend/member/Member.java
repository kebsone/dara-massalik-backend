package com.daramassalik.backend.member;

import com.daramassalik.backend.auth.AppUser;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "members")
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(length = 160)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String address;

    @Column(name = "joined_date", nullable = false)
    private LocalDate joinedDate;

    @Column(nullable = false)
    private boolean active = true;

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
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDate getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
}
