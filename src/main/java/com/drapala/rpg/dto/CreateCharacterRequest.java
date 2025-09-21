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
    @NotBlank
    @Size(min = 4, max = 15)
    @Pattern(regexp = "^[a-zA-Z_]+$")
    private String name;

    @NotNull
    private Job job;
}

