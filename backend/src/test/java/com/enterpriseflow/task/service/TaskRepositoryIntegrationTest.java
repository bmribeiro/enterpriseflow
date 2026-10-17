package com.enterpriseflow.task.service;

import com.enterpriseflow.project.entity.Project;
import com.enterpriseflow.project.repository.ProjectRepository;

import com.enterpriseflow.task.entity.Task;
import com.enterpriseflow.task.enums.TaskStatus;

import com.enterpriseflow.task.repository.TaskRepository;
import com.enterpriseflow.tenant.entity.Tenant;
import com.enterpriseflow.tenant.repository.TenantRepository;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TaskRepositoryIntegrationTest {

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldIsolateTasksByTenant() {

        String suffix = UUID.randomUUID().toString();

        Tenant tenantA = tenantRepository.saveAndFlush(
                new Tenant("TEST_A_" + suffix, "Empresa A")
        );

        Tenant tenantB = tenantRepository.saveAndFlush(
                new Tenant("TEST_B_" + suffix, "Empresa B")
        );

        Project project = projectRepository.saveAndFlush(
                new Project(tenantA, "Projeto A", "Teste")
        );

        Task task = taskRepository.saveAndFlush(
                new Task(project, "Tarefa A")
        );

        entityManager.clear();

        assertTrue(
                taskRepository
                        .findByIdAndProject_IdAndProject_Tenant_Id(
                                task.getId(),
                                project.getId(),
                                tenantA.getId()
                        )
                        .isPresent()
        );

        assertTrue(
                taskRepository
                        .findByIdAndProject_IdAndProject_Tenant_Id(
                                task.getId(),
                                project.getId(),
                                tenantB.getId()
                        )
                        .isEmpty()
        );
    }

    @Test
    void shouldPersistTaskChangesThroughDirtyChecking() {

        String suffix = UUID.randomUUID().toString();

        Tenant tenant = tenantRepository.saveAndFlush(
                new Tenant("TEST_" + suffix, "Empresa Teste")
        );

        Project project = projectRepository.saveAndFlush(
                new Project(tenant, "Projeto Teste", null)
        );

        Task task = taskRepository.saveAndFlush(
                new Task(project, "Título inicial")
        );

        UUID taskId = task.getId();

        task.updateDetails(
                "Título atualizado",
                TaskStatus.DONE
        );

        entityManager.flush();
        entityManager.clear();

        Task reloaded = taskRepository.findById(taskId)
                .orElseThrow();

        assertEquals("Título atualizado", reloaded.getTitle());
        assertEquals(TaskStatus.DONE, reloaded.getStatus());
    }
}