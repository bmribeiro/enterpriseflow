package com.enterpriseflow.project.controller;

import com.enterpriseflow.project.dto.CreateProjectRequest;
import com.enterpriseflow.project.dto.ProjectResponse;
import com.enterpriseflow.project.dto.UpdateProjectRequest;
import com.enterpriseflow.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenants/{tenantId}/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(
            @PathVariable UUID tenantId,
            @Valid @RequestBody CreateProjectRequest request) {

        ProjectResponse response =
                projectService.create(tenantId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public List<ProjectResponse> findAll(
            @PathVariable UUID tenantId) {

        return projectService.findAllByTenant(tenantId);
    }

    @GetMapping("/{projectId}")
    public ProjectResponse findById(
            @PathVariable UUID tenantId,
            @PathVariable UUID projectId) {

        return projectService.findById(tenantId, projectId);
    }

    @PutMapping("/{projectId}")
    public ProjectResponse update(
            @PathVariable UUID tenantId,
            @PathVariable UUID projectId,
            @Valid @RequestBody UpdateProjectRequest request) {

        return projectService.update(
                tenantId,
                projectId,
                request
        );
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable UUID tenantId,
            @PathVariable UUID projectId) {

        projectService.delete(
                tenantId,
                projectId
        );
    }
}