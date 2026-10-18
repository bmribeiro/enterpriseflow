package com.enterpriseflow.task.service;

import com.enterpriseflow.tenant.entity.TenantMembershipId;
import com.enterpriseflow.tenant.repository.TenantMembershipRepository;

import com.enterpriseflow.tenant.service.TenantAccessService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantAccessServiceTest {

    private static final String ISSUER =
            "http://localhost:9000";

    private static final String SUBJECT = "demo";

    private static final UUID TENANT_A =
            UUID.fromString(
                    "11111111-1111-4111-8111-111111111111"
            );

    private static final UUID TENANT_B =
            UUID.fromString(
                    "22222222-2222-4222-8222-222222222222"
            );

    @Mock
    private TenantMembershipRepository repository;

    @InjectMocks
    private TenantAccessService tenantAccessService;

    // ========================================
    // HELPER - JWT SIMULADO
    // ========================================

    private Jwt jwt() {
        return Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .issuer(ISSUER)
                .subject(SUBJECT)
                .build();
    }

    private JwtAuthenticationToken authentication() {
        return new JwtAuthenticationToken(
                jwt(),
                List.of()
        );
    }

    private TenantMembershipId membershipId(UUID tenantId) {
        return new TenantMembershipId(
                ISSUER,
                SUBJECT,
                tenantId
        );
    }

    // ========================================
    // 1. TENANT AUTORIZADO
    // ========================================

    @Test
    void shouldAllowAccessToAuthorizedTenant() {

        when(repository.existsById(membershipId(TENANT_B)))
                .thenReturn(true);

        boolean allowed = tenantAccessService.isMember(
                TENANT_B,
                authentication()
        );

        assertTrue(allowed);

        verify(repository).existsById(
                membershipId(TENANT_B)
        );
    }

    // ========================================
    // 2. TENANT NAO AUTORIZADO
    // ========================================

    @Test
    void shouldDenyAccessToUnauthorizedTenant() {

        boolean allowed = tenantAccessService.isMember(
                TENANT_A,
                authentication()
        );

        assertFalse(allowed);

        verify(repository).existsById(
                membershipId(TENANT_A)
        );
    }

    // ========================================
    // 3. ISOLAMENTO ENTRE TENANTS
    // ========================================

    @Test
    void shouldIsolateTenantsForSameUser() {

        when(repository.existsById(membershipId(TENANT_B)))
                .thenReturn(true);

        boolean companyB = tenantAccessService.isMember(
                TENANT_B,
                authentication()
        );

        boolean companyA = tenantAccessService.isMember(
                TENANT_A,
                authentication()
        );

        assertTrue(companyB);
        assertFalse(companyA);

        verify(repository).existsById(
                membershipId(TENANT_B)
        );

        verify(repository).existsById(
                membershipId(TENANT_A)
        );
    }

    // ========================================
    // 4. AUTENTICACAO NAO JWT
    // ========================================

    @Test
    void shouldRejectNonJwtAuthentication() {

        var authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        SUBJECT,
                        "ignored",
                        List.of()
                );

        boolean allowed = tenantAccessService.isMember(
                TENANT_B,
                authentication
        );

        assertFalse(allowed);

        verifyNoInteractions(repository);
    }

    // ========================================
    // 5. IDENTIDADE SEM ISSUER
    // ========================================

    @Test
    void shouldRejectJwtWithoutIssuer() {

        Jwt token = Jwt.withTokenValue("test-token")
                .header("alg", "RS256")
                .subject(SUBJECT)
                .build();

        boolean allowed = tenantAccessService.isMember(
                TENANT_B,
                new JwtAuthenticationToken(token, List.of())
        );

        assertFalse(allowed);

        verifyNoInteractions(repository);
    }

    // ========================================
    // 6. ACESSO NEGADO COM EXCECAO
    // ========================================

    @Test
    void shouldThrowAccessDeniedForUnauthorizedTenant() {

        AccessDeniedException exception =
                assertThrows(
                        AccessDeniedException.class,
                        () -> tenantAccessService.assertMember(
                                TENANT_A,
                                jwt()
                        )
                );

        assertEquals(
                "Tenant access denied",
                exception.getMessage()
        );

        verify(repository).existsById(
                membershipId(TENANT_A)
        );
    }
}
