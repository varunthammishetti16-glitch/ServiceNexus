
package com.servicenexus.entity;

import com.servicenexus.model.WorkerProfile;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "worker_accounts",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_worker_accounts_email",
        columnNames = "email"
    )
)
public class WorkerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "worker_profile_id",
        nullable = false,
        unique = true
    )
    private WorkerProfile workerProfile;

    @Column(name = "verification_status", length = 20)
    private String verificationStatus = "PENDING";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public WorkerAccount() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (verificationStatus == null) {
            verificationStatus = "PENDING";
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public WorkerProfile getWorkerProfile() {
        return workerProfile;
    }

    public void setWorkerProfile(WorkerProfile workerProfile) {
        this.workerProfile = workerProfile;
    }

    public String getVerificationStatus() {
        return verificationStatus == null
                ? "PENDING"
                : verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
