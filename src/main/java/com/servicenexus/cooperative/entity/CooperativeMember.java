
package com.servicenexus.cooperative.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "cooperative_members",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cooperative_member",
        columnNames = {"cooperative_id", "worker_id"}
    )
)
public class CooperativeMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cooperative_id", nullable = false)
    private Cooperative cooperative;

    // References the worker ID from the worker module.
    // We will connect the actual Worker entity when it is available.
    @Column(name = "worker_id", nullable = false)
    private Long workerId;

    @Column(nullable = false, length = 30)
    private String role = "MEMBER";

    @Column(nullable = false)
    private LocalDateTime joinedAt;

    protected CooperativeMember() {
    }

    public CooperativeMember(
            Cooperative cooperative,
            Long workerId,
            String role) {
        this.cooperative = cooperative;
        this.workerId = workerId;
        this.role = role;
    }

    @PrePersist
    void onCreate() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public Cooperative getCooperative() {
        return cooperative;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
}
