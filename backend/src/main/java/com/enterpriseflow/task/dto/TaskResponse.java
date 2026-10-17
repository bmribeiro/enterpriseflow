package com.enterpriseflow.task.dto;

import com.enterpriseflow.task.entity.Task;
import com.enterpriseflow.task.enums.TaskStatus;

import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID projectId,
        String title,
        TaskStatus status
) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.getStatus()
        );
    }
}