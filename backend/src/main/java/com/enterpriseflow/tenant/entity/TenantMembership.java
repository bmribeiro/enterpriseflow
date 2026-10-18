package com.enterpriseflow.tenant.entity;

import com.enterpriseflow.tenant.enums.TenantRole;
import jakarta.persistence.*;

@Entity
@Table(name = "tenant_memberships")
public class TenantMembership {

    @EmbeddedId
    private TenantMembershipId id;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "role",
            nullable = false,
            length = 20
    )
    private TenantRole role;

    protected TenantMembership() {
        // Required by JPA
    }

    public TenantMembership(
            TenantMembershipId id,
            TenantRole role
    ) {
        this.id = id;
        this.role = role;
    }

    public TenantMembershipId getId() {
        return id;
    }

    public TenantRole getRole() {
        return role;
    }
}