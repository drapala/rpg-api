package com.drapala.rpg.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Stats {
    int lifePoints;
    int strength;
    int dexterity;
    int intelligence;

    public Stats validate() {
        if (lifePoints < 0 || strength < 0 || dexterity < 0 || intelligence < 0) {
            throw new IllegalArgumentException("Stats values must be non-negative");
        }
        return this;
    }
}

