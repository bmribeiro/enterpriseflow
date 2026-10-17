package com.enterpriseflow.task.controller;

import com.enterpriseflow.task.dto.CreateTaskRequest;
import com.enterpriseflow.task.dto.TaskResponse;
import com.enterpriseflow.task.dto.UpdateTaskRequest;
import com.enterpriseflow.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/tenants/{tenantId}/projects/{projectId}/tasks"
)
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<TaskResponse> create(
            @PathVariable UUID tenantId,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request) {

        TaskResponse response = taskService.create(
                tenantId,
                projectId,
                request
        );

        URI location = URI.create(
                "/api/v1/tenants/" + tenantId
                        + "/projects/" + projectId
                        + "/tasks/" + response.id()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    // READ - LIST
    @GetMapping
    public List<TaskResponse> findAll(
            @PathVariable UUID tenantId,
            @PathVariable UUID projectId) {

        return taskService.findAllByProject(
                tenantId,
                projectId
        );
    }

    // READ - SINGLE
    @GetMapping("/{taskId}")
    public TaskResponse findById(
            @PathVariable UUID tenantId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {

        return taskService.findById(
                tenantId,
                projectId,
                taskId
        );
    }

    // UPDATE
    @PutMapping("/{taskId}")
    public TaskResponse update(
            @PathVariable UUID tenantId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request) {

        return taskService.update(
                tenantId,
                projectId,
                taskId,
                request
        );
    }

    // DELETE
    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID tenantId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId) {

        taskService.delete(
                tenantId,
                projectId,
                taskId
        );
    }
}