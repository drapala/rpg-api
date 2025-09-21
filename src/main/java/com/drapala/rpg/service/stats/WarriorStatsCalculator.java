package com.drapala.rpg.service.stats;

import com.drapala.rpg.model.Stats;
import org.springframework.stereotype.Component;

@Component
public class WarriorStatsCalculator implements StatsCalculator {
    @Override
    public int attack(Stats s) {
        return (int) Math.floor(0.8 * s.getStrength() + 0.2 * s.getDexterity());
    }

    @Override
    public int speed(Stats s) {
        return (int) Math.floor(0.6 * s.getDexterity() + 0.2 * s.getIntelligence());
    }
}

