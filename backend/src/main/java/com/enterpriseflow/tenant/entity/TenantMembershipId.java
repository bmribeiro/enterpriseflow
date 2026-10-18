package com.enterpriseflow.tenant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class TenantMembershipId implements Serializable {

    @Column(name = "issuer", nullable = false, length = 255)
    private String issuer;

    @Column(name = "subject", nullable = false, length = 255)
    private String subject;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    protected TenantMembershipId() {
        // Required by JPA
    }

    public TenantMembershipId(
            String issuer,
            String subject,
            UUID tenantId
    ) {
        this.issuer = issuer;
        this.subject = subject;
        this.tenantId = tenantId;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getSubject() {
        return subject;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof TenantMembershipId other)) {
            return false;
        }

        return Objects.equals(issuer, other.issuer)
                && Objects.equals(subject, other.subject)
                && Objects.equals(tenantId, other.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(issuer, subject, tenantId);
    }
}