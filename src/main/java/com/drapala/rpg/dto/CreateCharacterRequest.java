package com.drapala.rpg.dto;

import com.drapala.rpg.model.Job;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCharacterRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 4, max = 15, message = "Name must be 4-15 characters long")
    @Pattern(regexp = "^[a-zA-Z_]+$", message = "Name may contain letters and underscore only")
    private String name;

    @NotNull(message = "Job is required")
    private Job job;
}
