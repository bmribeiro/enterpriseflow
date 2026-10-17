package com.enterpriseflow.task.service;


import com.enterpriseflow.security.config.SecurityConfig;
import com.enterpriseflow.task.controller.TaskController;
import com.enterpriseflow.task.dto.TaskResponse;
import com.enterpriseflow.task.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

    // ========================================
    // TEST DATA
    // ========================================

    private static final UUID TENANT_ID =
            UUID.fromString(
                    "11111111-1111-4111-8111-111111111111"
            );

    private static final UUID PROJECT_ID =
            UUID.fromString(
                    "33333333-3333-4333-8333-333333333333"
            );

    private static final UUID TASK_ID =
            UUID.fromString(
                    "44444444-4444-4444-8444-444444444444"
            );

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    // ========================================
    // HELPER METHODS
    // ========================================

    private String baseUrl() {
        return "/api/v1/tenants/"
                + TENANT_ID
                + "/projects/"
                + PROJECT_ID
                + "/tasks";
    }

    private static JwtRequestPostProcessor readJwt() {
        return jwt().authorities(
                new SimpleGrantedAuthority(
                        "SCOPE_projects.read"
                )
        );
    }

    private static JwtRequestPostProcessor writeJwt() {
        return jwt().authorities(
                new SimpleGrantedAuthority(
                        "SCOPE_projects.write"
                )
        );
    }

    // ========================================
    // SECURITY - UNAUTHENTICATED GET
    // Expected: 401 Unauthorized
    // ========================================

    @Test
    void shouldRejectUnauthenticatedRequest()
            throws Exception {

        mockMvc.perform(
                        get(baseUrl())
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(taskService);
    }

    // ========================================
    // SECURITY - UNAUTHENTICATED DELETE
    // Expected: 401 Unauthorized
    // ========================================

    @Test
    void shouldRejectUnauthenticatedDelete()
            throws Exception {

        mockMvc.perform(
                        delete(baseUrl() + "/" + TASK_ID)
                )
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(taskService);
    }

    // ========================================
    // SECURITY - INSUFFICIENT WRITE SCOPE
    // Expected: 403 Forbidden
    // ========================================

    @Test
    void shouldRejectDeleteWithoutWriteScope()
            throws Exception {

        mockMvc.perform(
                        delete(baseUrl() + "/" + TASK_ID)
                                .with(readJwt())
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(taskService);
    }

    // ========================================
    // SECURITY - INSUFFICIENT READ SCOPE
    // Expected: 403 Forbidden
    // ========================================

    @Test
    void shouldRejectReadWithoutReadScope()
            throws Exception {

        mockMvc.perform(
                        get(baseUrl())
                                .with(writeJwt())
                )
                .andExpect(status().isForbidden());

        verifyNoInteractions(taskService);
    }

    // ========================================
    // CREATE - VALID REQUEST
    // Required scope: projects.write
    // Expected: 201 Created
    // ========================================

    @Test
    void shouldCreateTaskWithJwt()
            throws Exception {

        when(taskService.create(
                eq(TENANT_ID),
                eq(PROJECT_ID),
                any()
        ))
                .thenReturn(
                        new TaskResponse(
                                TASK_ID,
                                PROJECT_ID,
                                "Nova tarefa",
                                TaskStatus.TODO
                        )
                );

        mockMvc.perform(
                        post(baseUrl())
                                .with(writeJwt())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "title": "Nova tarefa"
                        }
                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(TASK_ID.toString())
                )
                .andExpect(
                        jsonPath("$.projectId")
                                .value(PROJECT_ID.toString())
                )
                .andExpect(
                        jsonPath("$.title")
                                .value("Nova tarefa")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("TODO")
                )
                .andExpect(
                        header().string(
                                "Location",
                                baseUrl() + "/" + TASK_ID
                        )
                );

        verify(taskService).create(
                eq(TENANT_ID),
                eq(PROJECT_ID),
                any()
        );
    }

    // ========================================
    // CREATE - INVALID REQUEST
    // Required scope: projects.write
    // Expected: 400 Bad Request
    // ========================================

    @Test
    void shouldRejectBlankTitleWithJwt()
            throws Exception {

        mockMvc.perform(
                        post(baseUrl())
                                .with(writeJwt())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "title": ""
                        }
                        """)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(taskService);
    }

    // ========================================
    // READ - SINGLE TASK
    // Required scope: projects.read
    // Expected: 200 OK
    // ========================================

    @Test
    void shouldReturnTaskWithJwt()
            throws Exception {

        when(taskService.findById(
                TENANT_ID,
                PROJECT_ID,
                TASK_ID
        ))
                .thenReturn(
                        new TaskResponse(
                                TASK_ID,
                                PROJECT_ID,
                                "Tarefa existente",
                                TaskStatus.IN_PROGRESS
                        )
                );

        mockMvc.perform(
                        get(baseUrl() + "/" + TASK_ID)
                                .with(readJwt())
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(TASK_ID.toString())
                )
                .andExpect(
                        jsonPath("$.projectId")
                                .value(PROJECT_ID.toString())
                )
                .andExpect(
                        jsonPath("$.title")
                                .value("Tarefa existente")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("IN_PROGRESS")
                );

        verify(taskService).findById(
                TENANT_ID,
                PROJECT_ID,
                TASK_ID
        );
    }

    // ========================================
    // READ - TASK LIST
    // Required scope: projects.read
    // Expected: 200 OK
    // ========================================

    @Test
    void shouldListTasksWithJwt()
            throws Exception {

        when(taskService.findAllByProject(
                TENANT_ID,
                PROJECT_ID
        ))
                .thenReturn(
                        List.of(
                                new TaskResponse(
                                        TASK_ID,
                                        PROJECT_ID,
                                        "Primeira tarefa",
                                        TaskStatus.TODO
                                )
                        )
                );

        mockMvc.perform(
                        get(baseUrl())
                                .with(readJwt())
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(TASK_ID.toString())
                )
                .andExpect(
                        jsonPath("$[0].title")
                                .value("Primeira tarefa")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("TODO")
                );

        verify(taskService).findAllByProject(
                TENANT_ID,
                PROJECT_ID
        );
    }

    // ========================================
    // UPDATE - VALID REQUEST
    // Required scope: projects.write
    // Expected: 200 OK
    // ========================================

    @Test
    void shouldUpdateTaskWithJwt()
            throws Exception {

        when(taskService.update(
                eq(TENANT_ID),
                eq(PROJECT_ID),
                eq(TASK_ID),
                any()
        ))
                .thenReturn(
                        new TaskResponse(
                                TASK_ID,
                                PROJECT_ID,
                                "Tarefa atualizada",
                                TaskStatus.DONE
                        )
                );

        mockMvc.perform(
                        put(baseUrl() + "/" + TASK_ID)
                                .with(writeJwt())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                        {
                            "title": "Tarefa atualizada",
                            "status": "DONE"
                        }
                        """)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(TASK_ID.toString())
                )
                .andExpect(
                        jsonPath("$.title")
                                .value("Tarefa atualizada")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("DONE")
                );

        verify(taskService).update(
                eq(TENANT_ID),
                eq(PROJECT_ID),
                eq(TASK_ID),
                any()
        );
    }

    // ========================================
    // DELETE
    // Required scope: projects.write
    // Expected: 204 No Content
    // ========================================

    @Test
    void shouldDeleteTaskWithJwt()
            throws Exception {

        mockMvc.perform(
                        delete(baseUrl() + "/" + TASK_ID)
                                .with(writeJwt())
                )
                .andExpect(status().isNoContent());

        verify(taskService).delete(
                TENANT_ID,
                PROJECT_ID,
                TASK_ID
        );
    }
}
