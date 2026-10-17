package com.enterpriseflow.project.service;

import com.enterpriseflow.project.dto.CreateProjectRequest;
import com.enterpriseflow.project.dto.ProjectResponse;
import com.enterpriseflow.project.dto.UpdateProjectRequest;
import com.enterpriseflow.project.entity.Project;
import com.enterpriseflow.project.repository.ProjectRepository;
import com.enterpriseflow.task.repository.TaskRepository;
import com.enterpriseflow.tenant.entity.Tenant;
import com.enterpriseflow.tenant.repository.TenantRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TenantRepository tenantRepository;
    private final TaskRepository taskRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            TenantRepository tenantRepository,
            TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.tenantRepository = tenantRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public ProjectResponse create(
            UUID tenantId,
            CreateProjectRequest request) {

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Tenant not found"
                ));

        Project project = new Project(
                tenant,
                request.name(),
                request.description()
        );

        Project savedProject = projectRepository.save(project);

        return ProjectResponse.from(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> findAllByTenant(UUID tenantId) {

        return projectRepository.findByTenant_Id(tenantId)
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(UUID tenantId, UUID projectId) {

        Project project = projectRepository
                .findByIdAndTenant_Id(projectId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        return ProjectResponse.from(project);
    }

    @Transactional
    public ProjectResponse update(
            UUID tenantId,
            UUID projectId,
            UpdateProjectRequest request) {

        Project project = projectRepository
                .findByIdAndTenant_Id(projectId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        project.updateDetails(
                request.name(),
                request.description()
        );

        return ProjectResponse.from(project);
    }

    @Transactional
    public void delete(
            UUID tenantId,
            UUID projectId) {

        Project project = projectRepository
                .findByIdAndTenant_Id(projectId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        if (taskRepository.existsByProject_Id(projectId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a project containing tasks"
            );
        }

        projectRepository.delete(project);
    }
}