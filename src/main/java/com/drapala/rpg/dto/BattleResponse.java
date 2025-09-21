package com.drapala.rpg.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class BattleResponse {
    String winnerId;
    String loserId;
    List<String> battleLog;
}

