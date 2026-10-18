
package com.enterpriseflow.task.service;

import com.enterpriseflow.tenant.entity.TenantMembership;
import com.enterpriseflow.tenant.entity.TenantMembershipId;
import com.enterpriseflow.tenant.enums.TenantRole;
import com.enterpriseflow.tenant.repository.TenantMembershipRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

import static org.springframework.security.test.web.servlet.request
        .SecurityMockMvcRequestPostProcessors.jwt;

import static org.springframework.test.web.servlet.request
        .MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result
        .MockMvcResultMatchers.status;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@SpringBootTest
@AutoConfigureMockMvc
class TenantSecurityIntegrationTest {

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

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TenantMembershipRepository membershipRepository;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private String projectsUrl(UUID tenantId) {
        return "/api/v1/tenants/"
                + tenantId
                + "/projects";
    }

    // ========================================
    // JWT with projects.read
    // ========================================

    private org.springframework.security.test.web.servlet.request
            .SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor
    readJwt() {

        return jwt()
                .jwt(token -> token
                        .issuer(ISSUER)
                        .subject(SUBJECT)
                        .audience(
                                List.of("enterpriseflow-api")
                        )
                )
                .authorities(
                        new SimpleGrantedAuthority(
                                "SCOPE_projects.read"
                        )
                );
    }

    // ========================================
    // 1. Authorized tenant
    // Expected: 200
    // ========================================

    @Test
    void shouldAllowAccessToTenantB()
            throws Exception {

        TenantMembershipId membershipId =
                new TenantMembershipId(
                        ISSUER,
                        SUBJECT,
                        TENANT_B
                );

        when(membershipRepository.existsById(membershipId))
                .thenReturn(true);

        mockMvc.perform(
                        get(projectsUrl(TENANT_B))
                                .with(readJwt())
                )
                .andExpect(status().isOk());

        verify(membershipRepository)
                .existsById(membershipId);
    }

    // ========================================
    // 2. Unauthorized tenant
    // Expected: 403
    // ========================================

    @Test
    void shouldDenyAccessToTenantA()
            throws Exception {

        TenantMembershipId membershipId =
                new TenantMembershipId(
                        ISSUER,
                        SUBJECT,
                        TENANT_A
                );

        mockMvc.perform(
                        get(projectsUrl(TENANT_A))
                                .with(readJwt())
                )
                .andExpect(status().isForbidden());

        verify(membershipRepository)
                .existsById(membershipId);
    }

    // ========================================
    // 3. Unauthenticated request
    // Expected: 401
    // ========================================

    @Test
    void shouldRejectRequestWithoutJwt()
            throws Exception {

        mockMvc.perform(
                        get(projectsUrl(TENANT_B))
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(membershipRepository);
    }

    // ========================================
    // 4. Missing read scope
    // Expected: 403
    // ========================================

    @Test
    void shouldRejectRequestWithoutReadScope()
            throws Exception {

        mockMvc.perform(
                        get(projectsUrl(TENANT_B))
                                .with(jwt().authorities(
                                        new SimpleGrantedAuthority(
                                                "SCOPE_projects.write"
                                        )
                                ))
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(membershipRepository);
    }


    @Test
    void shouldDenyProjectDeletionForTenantUser()
            throws Exception {

        TenantMembershipId membershipId =
                new TenantMembershipId(
                        ISSUER,
                        SUBJECT,
                        TENANT_B
                );

        TenantMembership membership =
                new TenantMembership(
                        membershipId,
                        TenantRole.USER
                );

        when(membershipRepository.findById(membershipId))
                .thenReturn(java.util.Optional.of(membership));

        UUID projectId = UUID.fromString(
                "99999999-9999-4999-8999-999999999999"
        );

        mockMvc.perform(
                        delete(
                                projectsUrl(TENANT_B) + "/" + projectId
                        )
                                .with(jwt()
                                        .jwt(token -> token
                                                .issuer(ISSUER)
                                                .subject(SUBJECT)
                                                .audience(
                                                        List.of("enterpriseflow-api")
                                                )
                                        )
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "SCOPE_projects.write"
                                                )
                                        )
                                )
                )
                .andExpect(status().isForbidden());

        verify(membershipRepository)
                .findById(membershipId);
    }

    @Test
    void shouldAllowAdminToReachProjectDeletion()
            throws Exception {

        TenantMembershipId membershipId =
                new TenantMembershipId(
                        ISSUER,
                        SUBJECT,
                        TENANT_B
                );

        TenantMembership membership =
                new TenantMembership(
                        membershipId,
                        TenantRole.ADMIN
                );

        when(membershipRepository.findById(membershipId))
                .thenReturn(Optional.of(membership));

        UUID nonexistentProjectId = UUID.randomUUID();

        mockMvc.perform(
                        delete(
                                projectsUrl(TENANT_B)
                                        + "/" + nonexistentProjectId
                        )
                                .with(jwt()
                                        .jwt(token -> token
                                                .issuer(ISSUER)
                                                .subject(SUBJECT)
                                                .audience(
                                                        List.of("enterpriseflow-api")
                                                )
                                        )
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "SCOPE_projects.write"
                                                )
                                        )
                                )
                )
                .andExpect(status().isNotFound());

        verify(membershipRepository)
                .findById(membershipId);
    }


    @Test
    void shouldDenyAdminAccessToAnotherTenant()
            throws Exception {

        TenantMembershipId membershipB =
                new TenantMembershipId(
                        ISSUER,
                        SUBJECT,
                        TENANT_B
                );

        TenantMembershipId membershipA =
                new TenantMembershipId(
                        ISSUER,
                        SUBJECT,
                        TENANT_A
                );

        when(membershipRepository.findById(membershipB))
                .thenReturn(Optional.of(
                        new TenantMembership(
                                membershipB,
                                TenantRole.ADMIN
                        )
                ));

        UUID projectId = UUID.randomUUID();

        mockMvc.perform(
                        delete(
                                projectsUrl(TENANT_A)
                                        + "/" + projectId
                        )
                                .with(jwt()
                                        .jwt(token -> token
                                                .issuer(ISSUER)
                                                .subject(SUBJECT)
                                                .audience(
                                                        List.of("enterpriseflow-api")
                                                )
                                        )
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "SCOPE_projects.write"
                                                )
                                        )
                                )
                )
                .andExpect(status().isForbidden());

        verify(membershipRepository)
                .findById(membershipA);

        verify(membershipRepository, never())
                .findById(membershipB);
    }

}
