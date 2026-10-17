package com.daramassalik.backend.auth;

import jakarta.persistence.*;

@Entity
@Table(name = "role_permissions", uniqueConstraints = @UniqueConstraint(name = "uq_role_permissions", columnNames = {"role", "permission"}))
public class RolePermission {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Permission permission;

    public RolePermission() {}
    public RolePermission(Role role, Permission permission) { this.role = role; this.permission = permission; }
    public Long getId() { return id; }
    public Role getRole() { return role; }
    public Permission getPermission() { return permission; }
}
