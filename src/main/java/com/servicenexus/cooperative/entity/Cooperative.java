
package com.servicenexus.cooperative.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "cooperatives",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_cooperative_name",
        columnNames = "name"
    )
)
public class Cooperative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(name = "services_offered", length = 500)
    private String servicesOffered;

    @Column(name = "contact_email", length = 254)
    private String contactEmail;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Cooperative() {
    }

    public Cooperative(
            String name,
            String description,
            String location,
            String servicesOffered,
            String contactEmail) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.servicesOffered = servicesOffered;
        this.contactEmail = contactEmail;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getServicesOffered() { return servicesOffered; }
    public String getContactEmail() { return contactEmail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
