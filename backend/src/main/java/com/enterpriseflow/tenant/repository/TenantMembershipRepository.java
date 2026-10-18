package com.enterpriseflow.tenant.repository;

import com.enterpriseflow.tenant.entity.TenantMembership;
import com.enterpriseflow.tenant.entity.TenantMembershipId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantMembershipRepository
        extends JpaRepository<TenantMembership, TenantMembershipId> {
}