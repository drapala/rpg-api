package com.drapala.rpg.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

import java.util.UUID;

@Getter
@Setter
@ToString
public class Character {
    private final UUID id;
    private final String name;
    private final Job job;
    private final Stats stats;

    private int currentLifePoints;
    private boolean alive;

    @Builder
    public Character(UUID id, String name, Job job, Stats stats) {
        if (id == null || name == null || job == null || stats == null) {
            throw new IllegalArgumentException("Character requires id, name, job, and stats");
        }
        this.id = id;
        this.name = name;
        this.job = job;
        this.stats = stats.validate();
        this.currentLifePoints = stats.getLifePoints();
        this.alive = true;
    }
}

