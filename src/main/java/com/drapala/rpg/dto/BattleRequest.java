package com.drapala.rpg.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BattleRequest {
    @NotNull
    @Schema(example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID attackerId;
    @NotNull
    @Schema(example = "6ba7b810-9dad-11d1-80b4-00c04fd430c8")
    private UUID defenderId;
}
