
package com.enterpriseflow.project.entity;

import com.enterpriseflow.tenant.entity.Tenant;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "tenant_id",
            nullable = false
    )
    private Tenant tenant;

    @Column(
            name = "name",
            nullable = false,
            length = 150
    )
    private String name;

    @Column(
            name = "description",
            length = 1000
    )
    private String description;

    protected Project() {
    }

    public Project(
            Tenant tenant,
            String name,
            String description
    ) {
        this.tenant = tenant;
        this.name = name;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void updateDetails(
            String name,
            String description) {

        this.name = name;
        this.description = description;
    }
}
