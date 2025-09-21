package com.drapala.rpg.dto;

import com.drapala.rpg.model.Job;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CharacterResponse {
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
    String id;
    @Schema(example = "Arthur_Hero")
    String name;
    @Schema(example = "WARRIOR")
    Job job;
    @Schema(example = "true")
    boolean alive;
    @Schema(example = "20")
    int currentLifePoints;
    @Schema(example = "20")
    int maxLifePoints;
    @Schema(example = "10")
    int strength;
    @Schema(example = "5")
    int dexterity;
    @Schema(example = "5")
    int intelligence;
    @Schema(example = "9")
    Integer attack; // optional derived
    @Schema(example = "3")
    Integer speed;  // optional derived
}
