package com.enterpriseflow.task.repository;

import com.enterpriseflow.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository
        extends JpaRepository<Task, UUID> {

    List<Task> findByProject_Id(UUID projectId);

    List<Task> findByProject_Tenant_Id(UUID tenantId);

    Optional<Task> findByIdAndProject_Tenant_Id(
            UUID taskId,
            UUID tenantId
    );
}