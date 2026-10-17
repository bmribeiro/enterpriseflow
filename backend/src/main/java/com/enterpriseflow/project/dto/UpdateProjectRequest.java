package com.enterpriseflow.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProjectRequest(

        @NotBlank
        String name,

        @Size(max = 1000)
        String description

) {
}
