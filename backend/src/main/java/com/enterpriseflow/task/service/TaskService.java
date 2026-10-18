package com.enterpriseflow.task.service;

import com.enterpriseflow.project.entity.Project;
import com.enterpriseflow.project.repository.ProjectRepository;
import com.enterpriseflow.task.dto.CreateTaskRequest;
import com.enterpriseflow.task.dto.TaskResponse;
import com.enterpriseflow.task.dto.UpdateTaskRequest;
import com.enterpriseflow.task.entity.Task;
import com.enterpriseflow.task.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@PreAuthorize(
        "@tenantAccessService.isMember(#p0, authentication)"
)
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository) {

        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    // CREATE
    @Transactional
    public TaskResponse create(
            UUID tenantId,
            UUID projectId,
            CreateTaskRequest request) {

        Project project = findProject(tenantId, projectId);

        Task task = new Task(project, request.title());

        Task savedTask = taskRepository.save(task);

        return TaskResponse.from(savedTask);
    }

    // READ - LIST
    @Transactional(readOnly = true)
    public List<TaskResponse> findAllByProject(
            UUID tenantId,
            UUID projectId) {

        findProject(tenantId, projectId);

        return taskRepository
                .findByProject_IdAndProject_Tenant_Id(
                        projectId,
                        tenantId
                )
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    // READ - SINGLE
    @Transactional(readOnly = true)
    public TaskResponse findById(
            UUID tenantId,
            UUID projectId,
            UUID taskId) {

        Task task = findTask(
                tenantId,
                projectId,
                taskId
        );

        return TaskResponse.from(task);
    }

    // UPDATE
    @Transactional
    public TaskResponse update(
            UUID tenantId,
            UUID projectId,
            UUID taskId,
            UpdateTaskRequest request) {

        Task task = findTask(
                tenantId,
                projectId,
                taskId
        );

        task.updateDetails(
                request.title(),
                request.status()
        );

        return TaskResponse.from(task);
    }

    // DELETE
    @Transactional
    public void delete(
            UUID tenantId,
            UUID projectId,
            UUID taskId) {

        Task task = findTask(
                tenantId,
                projectId,
                taskId
        );

        taskRepository.delete(task);
    }

    // INTERNAL - PROJECT LOOKUP
    private Project findProject(
            UUID tenantId,
            UUID projectId) {

        return projectRepository
                .findByIdAndTenant_Id(
                        projectId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Project not found"
                        )
                );
    }

    // INTERNAL - TASK LOOKUP
    private Task findTask(
            UUID tenantId,
            UUID projectId,
            UUID taskId) {

        return taskRepository
                .findByIdAndProject_IdAndProject_Tenant_Id(
                        taskId,
                        projectId,
                        tenantId
                )
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Task not found"
                        )
                );
    }
}
