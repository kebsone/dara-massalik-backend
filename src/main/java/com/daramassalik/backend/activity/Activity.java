package com.daramassalik.backend.activity;

import com.daramassalik.backend.commission.Commission;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "activities")
public class Activity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 1500)
    private String description;

    @Column(name = "activity_date", nullable = false)
    private LocalDate activityDate;

    @Column(length = 200)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ActivityStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commission_id")
    private Commission commission;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getActivityDate() { return activityDate; }
    public void setActivityDate(LocalDate activityDate) { this.activityDate = activityDate; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public ActivityStatus getStatus() { return status; }
    public void setStatus(ActivityStatus status) { this.status = status; }
    public Commission getCommission() { return commission; }
    public void setCommission(Commission commission) { this.commission = commission; }
}
