package com.zinchenko.jobboard.job;

import jakarta.validation.constraints.*;

public record JobRequest(
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 100) String company,
        @NotBlank @Size(max = 100) String location,
        @NotBlank @Size(max = 4000) String description,
        @NotNull @Min(0) @Max(50) Integer experienceYears) {
}
