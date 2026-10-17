package com.enterpriseflow.project.dto;


import com.enterpriseflow.project.entity.Project;

import java.util.UUID;

public record ProjectResponse(
        UUID id,
        UUID tenantId,
        String name,
        String description
) {

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getTenant().getId(),
                project.getName(),
                project.getDescription()
        );
    }
}
