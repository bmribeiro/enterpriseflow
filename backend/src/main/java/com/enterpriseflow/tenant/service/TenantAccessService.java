package com.enterpriseflow.tenant.service;

import com.enterpriseflow.tenant.entity.TenantMembershipId;
import com.enterpriseflow.tenant.enums.TenantRole;
import com.enterpriseflow.tenant.repository.TenantMembershipRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TenantAccessService {

    private final TenantMembershipRepository repository;

    public TenantAccessService(
            TenantMembershipRepository repository
    ) {
        this.repository = repository;
    }

    // Used by @PreAuthorize expressions.
    @Transactional(readOnly = true)
    public boolean isMember(
            UUID tenantId,
            Authentication authentication
    ) {
        if (tenantId == null
                || !(authentication instanceof JwtAuthenticationToken jwtAuth)
                || !authentication.isAuthenticated()) {
            return false;
        }

        return membershipExists(
                tenantId,
                jwtAuth.getToken()
        );
    }

    // Available for explicit authorization checks.
    @Transactional(readOnly = true)
    public void assertMember(
            UUID tenantId,
            Jwt jwt
    ) {
        if (!membershipExists(tenantId, jwt)) {
            throw new AccessDeniedException(
                    "Tenant access denied"
            );
        }
    }

    private boolean membershipExists(
            UUID tenantId,
            Jwt jwt
    ) {
        if (tenantId == null || jwt == null) {
            return false;
        }

        String issuer = jwt.getIssuer() == null
                ? null
                : jwt.getIssuer().toString();

        String subject = jwt.getSubject();

        if (issuer == null || issuer.isBlank()
                || subject == null || subject.isBlank()) {
            return false;
        }

        TenantMembershipId membershipId =
                new TenantMembershipId(
                        issuer,
                        subject,
                        tenantId
                );

        return repository.existsById(membershipId);
    }


    @Transactional(readOnly = true)
    public boolean isAdmin(
            UUID tenantId,
            Authentication authentication
    ) {

        if (tenantId == null
                || !(authentication instanceof JwtAuthenticationToken jwtAuth)
                || !authentication.isAuthenticated()) {
            return false;
        }

        Jwt jwt = jwtAuth.getToken();

        String issuer = jwt.getIssuer() == null
                ? null
                : jwt.getIssuer().toString();

        String subject = jwt.getSubject();

        if (issuer == null || issuer.isBlank()
                || subject == null || subject.isBlank()) {
            return false;
        }

        TenantMembershipId membershipId =
                new TenantMembershipId(
                        issuer,
                        subject,
                        tenantId
                );

        return repository.findById(membershipId)
                .map(membership ->
                        membership.getRole() == TenantRole.ADMIN
                )
                .orElse(false);
    }

}