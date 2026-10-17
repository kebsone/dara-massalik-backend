package com.daramassalik.backend.auth;

import jakarta.persistence.*;

@Entity
@Table(name = "user_permission_overrides", uniqueConstraints = @UniqueConstraint(name = "uq_user_permission_override", columnNames = {"user_id", "permission"}))
public class UserPermissionOverride {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Permission permission;

    @Column(nullable = false)
    private boolean granted;

    public UserPermissionOverride() {}
    public UserPermissionOverride(AppUser user, Permission permission, boolean granted) { this.user = user; this.permission = permission; this.granted = granted; }
    public Long getId() { return id; }
    public AppUser getUser() { return user; }
    public Permission getPermission() { return permission; }
    public boolean isGranted() { return granted; }
    public void setGranted(boolean granted) { this.granted = granted; }
}
