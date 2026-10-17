package com.enterpriseflow.task.service;

import com.enterpriseflow.project.entity.Project;
import com.enterpriseflow.project.repository.ProjectRepository;

import com.enterpriseflow.task.dto.CreateTaskRequest;
import com.enterpriseflow.task.dto.TaskResponse;
import com.enterpriseflow.task.dto.UpdateTaskRequest;
import com.enterpriseflow.task.entity.Task;
import com.enterpriseflow.task.enums.TaskStatus;
import com.enterpriseflow.task.repository.TaskRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

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

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    // ========================================
    // CREATE
    // ========================================

    @Test
    void shouldCreateTaskWithTodoStatus() {

        Project project = mock(Project.class);

        when(project.getId())
                .thenReturn(PROJECT_ID);

        when(projectRepository.findByIdAndTenant_Id(
                PROJECT_ID,
                TENANT_ID
        )).thenReturn(Optional.of(project));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        CreateTaskRequest request =
                new CreateTaskRequest("Implementar API");

        TaskResponse response = taskService.create(
                TENANT_ID,
                PROJECT_ID,
                request
        );

        assertNotNull(response);
        assertEquals(PROJECT_ID, response.projectId());
        assertEquals("Implementar API", response.title());
        assertEquals(TaskStatus.TODO, response.status());

        verify(projectRepository).findByIdAndTenant_Id(
                PROJECT_ID,
                TENANT_ID
        );

        verify(taskRepository).save(any(Task.class));
    }

    // ========================================
    // CREATE - PROJECT NOT FOUND
    // ========================================

    @Test
    void shouldRejectCreationWhenProjectDoesNotExist() {

        when(projectRepository.findByIdAndTenant_Id(
                PROJECT_ID,
                TENANT_ID
        )).thenReturn(Optional.empty());

        CreateTaskRequest request =
                new CreateTaskRequest("Nova tarefa");

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> taskService.create(
                                TENANT_ID,
                                PROJECT_ID,
                                request
                        )
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verifyNoInteractions(taskRepository);
    }

    // ========================================
    // READ - LIST
    // ========================================

    @Test
    void shouldListTasksByProjectAndTenant() {

        Project project = mock(Project.class);

        when(project.getId())
                .thenReturn(PROJECT_ID);

        when(projectRepository.findByIdAndTenant_Id(
                PROJECT_ID,
                TENANT_ID
        )).thenReturn(Optional.of(project));

        Task task = new Task(
                project,
                "Testar persistencia"
        );

        when(taskRepository
                .findByProject_IdAndProject_Tenant_Id(
                        PROJECT_ID,
                        TENANT_ID
                ))
                .thenReturn(List.of(task));

        List<TaskResponse> result =
                taskService.findAllByProject(
                        TENANT_ID,
                        PROJECT_ID
                );

        assertNotNull(result);
        assertEquals(1, result.size());

        TaskResponse response = result.get(0);

        assertEquals(
                "Testar persistencia",
                response.title()
        );

        assertEquals(
                TaskStatus.TODO,
                response.status()
        );

        assertEquals(
                PROJECT_ID,
                response.projectId()
        );

        verify(taskRepository)
                .findByProject_IdAndProject_Tenant_Id(
                        PROJECT_ID,
                        TENANT_ID
                );
    }

    // ========================================
    // READ - SINGLE
    // ========================================

    @Test
    void shouldFindTaskById() {

        Project project = mock(Project.class);

        when(project.getId())
                .thenReturn(PROJECT_ID);

        Task task = new Task(
                project,
                "Tarefa existente"
        );

        when(taskRepository
                .findByIdAndProject_IdAndProject_Tenant_Id(
                        TASK_ID,
                        PROJECT_ID,
                        TENANT_ID
                ))
                .thenReturn(Optional.of(task));

        TaskResponse response = taskService.findById(
                TENANT_ID,
                PROJECT_ID,
                TASK_ID
        );

        assertNotNull(response);

        assertEquals(
                "Tarefa existente",
                response.title()
        );

        assertEquals(
                PROJECT_ID,
                response.projectId()
        );

        assertEquals(
                TaskStatus.TODO,
                response.status()
        );
    }

    // ========================================
    // UPDATE
    // ========================================

    @Test
    void shouldUpdateTaskWithoutCallingSave() {

        Project project = mock(Project.class);

        when(project.getId())
                .thenReturn(PROJECT_ID);

        Task task = new Task(
                project,
                "Titulo inicial"
        );

        when(taskRepository
                .findByIdAndProject_IdAndProject_Tenant_Id(
                        TASK_ID,
                        PROJECT_ID,
                        TENANT_ID
                ))
                .thenReturn(Optional.of(task));

        UpdateTaskRequest request =
                new UpdateTaskRequest(
                        "Titulo atualizado",
                        TaskStatus.IN_PROGRESS
                );

        TaskResponse response = taskService.update(
                TENANT_ID,
                PROJECT_ID,
                TASK_ID,
                request
        );

        assertNotNull(response);

        assertEquals(
                "Titulo atualizado",
                response.title()
        );

        assertEquals(
                TaskStatus.IN_PROGRESS,
                response.status()
        );

        assertEquals(
                "Titulo atualizado",
                task.getTitle()
        );

        assertEquals(
                TaskStatus.IN_PROGRESS,
                task.getStatus()
        );

        verify(taskRepository, never())
                .save(any(Task.class));
    }

    // ========================================
    // READ - WRONG TENANT
    // ========================================

    @Test
    void shouldReturnNotFoundForAnotherTenant() {

        UUID otherTenantId =
                UUID.fromString(
                        "22222222-2222-4222-8222-222222222222"
                );

        when(taskRepository
                .findByIdAndProject_IdAndProject_Tenant_Id(
                        TASK_ID,
                        PROJECT_ID,
                        otherTenantId
                ))
                .thenReturn(Optional.empty());

        ResponseStatusException exception =
                assertThrows(
                        ResponseStatusException.class,
                        () -> taskService.findById(
                                otherTenantId,
                                PROJECT_ID,
                                TASK_ID
                        )
                );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );
    }

    // ========================================
    // DELETE
    // ========================================

    @Test
    void shouldDeleteTask() {

        Project project = mock(Project.class);

        Task task = new Task(
                project,
                "Tarefa temporaria"
        );

        when(taskRepository
                .findByIdAndProject_IdAndProject_Tenant_Id(
                        TASK_ID,
                        PROJECT_ID,
                        TENANT_ID
                ))
                .thenReturn(Optional.of(task));

        taskService.delete(
                TENANT_ID,
                PROJECT_ID,
                TASK_ID
        );

        verify(taskRepository).delete(task);
        verify(taskRepository, never())
                .save(any(Task.class));
    }
}