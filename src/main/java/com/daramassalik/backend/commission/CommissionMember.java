package com.daramassalik.backend.commission;

import com.daramassalik.backend.member.Member;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "commission_members", uniqueConstraints = @UniqueConstraint(columnNames = {"commission_id", "member_id"}))
public class CommissionMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "commission_id", nullable = false)
    private Commission commission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "role_label", length = 120)
    private String roleLabel;

    @Column(name = "joined_date", nullable = false)
    private LocalDate joinedDate;

    public Long getId() { return id; }
    public Commission getCommission() { return commission; }
    public void setCommission(Commission commission) { this.commission = commission; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    public String getRoleLabel() { return roleLabel; }
    public void setRoleLabel(String roleLabel) { this.roleLabel = roleLabel; }
    public LocalDate getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }
}
