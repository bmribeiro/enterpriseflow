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

    private static final UUID TENANT_ID = UUID.randomUUID();
    private static final UUID PROJECT_ID = UUID.randomUUID();
    private static final UUID TASK_ID = UUID.randomUUID();

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTaskWithTodoStatus() {

        Project project = mock(Project.class);

        when(project.getId()).thenReturn(PROJECT_ID);

        when(projectRepository.findByIdAndTenant_Id(
                PROJECT_ID, TENANT_ID))
                .thenReturn(Optional.of(project));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponse response = taskService.create(
                TENANT_ID,
                PROJECT_ID,
                new CreateTaskRequest("Implementar API")
        );

        assertEquals("Implementar API", response.title());
        assertEquals(TaskStatus.TODO, response.status());
        assertEquals(PROJECT_ID, response.projectId());

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldListTasksOfRequestedProjectAndTenant() {

        Project project = mock(Project.class);

        when(project.getId()).thenReturn(PROJECT_ID);

        when(projectRepository.findByIdAndTenant_Id(
                PROJECT_ID, TENANT_ID))
                .thenReturn(Optional.of(project));

        Task task = new Task(project, "Testar API");

        when(taskRepository.findByProject_IdAndProject_Tenant_Id(
                PROJECT_ID, TENANT_ID))
                .thenReturn(List.of(task));

        List<TaskResponse> result =
                taskService.findAllByProject(TENANT_ID, PROJECT_ID);

        assertEquals(1, result.size());
        assertEquals("Testar API", result.get(0).title());
    }

    @Test
    void shouldUpdateTask() {

        Project project = mock(Project.class);
        when(project.getId()).thenReturn(PROJECT_ID);

        Task task = new Task(project, "Título inicial");

        when(taskRepository.findByIdAndProject_IdAndProject_Tenant_Id(
                TASK_ID, PROJECT_ID, TENANT_ID))
                .thenReturn(Optional.of(task));

        TaskResponse response = taskService.update(
                TENANT_ID,
                PROJECT_ID,
                TASK_ID,
                new UpdateTaskRequest(
                        "Título atualizado",
                        TaskStatus.IN_PROGRESS
                )
        );

        assertEquals("Título atualizado", response.title());
        assertEquals(TaskStatus.IN_PROGRESS, response.status());

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldReturnNotFoundForTaskOutsideTenant() {

        UUID otherTenantId = UUID.randomUUID();

        when(taskRepository.findByIdAndProject_IdAndProject_Tenant_Id(
                TASK_ID, PROJECT_ID, otherTenantId))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
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

    @Test
    void shouldDeleteTask() {

        Project project = mock(Project.class);
        Task task = new Task(project, "Tarefa temporária");

        when(taskRepository.findByIdAndProject_IdAndProject_Tenant_Id(
                TASK_ID, PROJECT_ID, TENANT_ID))
                .thenReturn(Optional.of(task));

        taskService.delete(TENANT_ID, PROJECT_ID, TASK_ID);

        verify(taskRepository).delete(task);
    }
}