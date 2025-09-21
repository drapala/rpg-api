package com.drapala.rpg.service.stats;

import com.drapala.rpg.model.Stats;
import org.springframework.stereotype.Component;

@Component
public class ThiefStatsCalculator implements StatsCalculator {
    @Override
    public int attack(Stats s) {
        return (int) Math.floor(0.25 * s.getStrength() + 1.0 * s.getDexterity() + 0.25 * s.getIntelligence());
    }

    @Override
    public int speed(Stats s) {
        return (int) Math.floor(0.8 * s.getDexterity());
    }
}

