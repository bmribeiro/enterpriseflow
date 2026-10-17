package com.enterpriseflow.task.dto;

import com.enterpriseflow.task.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateTaskRequest(

        @NotBlank
        @Size(max = 200)
        String title,

        @NotNull
        TaskStatus status

) {
}
