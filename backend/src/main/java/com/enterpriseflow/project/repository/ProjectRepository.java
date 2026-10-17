package com.enterpriseflow.project.repository;

import com.enterpriseflow.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository
        extends JpaRepository<Project, UUID> {

    List<Project> findByTenant_Id(UUID tenantId);

    Optional<Project> findByIdAndTenant_Id(
            UUID projectId,
            UUID tenantId
    );
}
