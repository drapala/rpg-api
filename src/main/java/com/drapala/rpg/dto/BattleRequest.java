package com.drapala.rpg.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BattleRequest {
    @NotNull
    private UUID attackerId;
    @NotNull
    private UUID defenderId;
}

