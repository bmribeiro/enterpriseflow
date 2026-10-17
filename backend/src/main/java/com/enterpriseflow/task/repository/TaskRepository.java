package com.enterpriseflow.task.repository;

import com.enterpriseflow.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository
        extends JpaRepository<Task, UUID> {

    List<Task> findByProject_IdAndProject_Tenant_Id(
            UUID projectId,
            UUID tenantId
    );

    Optional<Task> findByIdAndProject_IdAndProject_Tenant_Id(
            UUID taskId,
            UUID projectId,
            UUID tenantId
    );

    boolean existsByProject_Id(UUID projectId);
}