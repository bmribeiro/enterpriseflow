package com.enterpriseflow.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(

        @NotBlank
        @Size(max = 200)
        String title

) {
}