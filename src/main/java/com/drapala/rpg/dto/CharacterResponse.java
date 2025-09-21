package com.drapala.rpg.dto;

import com.drapala.rpg.model.Job;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CharacterResponse {
    String id;
    String name;
    Job job;
    boolean alive;
    int currentLifePoints;
    int maxLifePoints;
    int strength;
    int dexterity;
    int intelligence;
    Integer attack; // optional derived
    Integer speed;  // optional derived
}

