package com.drapala.rpg.service.stats;

import com.drapala.rpg.model.Stats;
import org.springframework.stereotype.Component;

@Component
public class MageStatsCalculator implements StatsCalculator {
    @Override
    public int attack(Stats s) {
        return (int) Math.floor(0.2 * s.getStrength() + 0.2 * s.getDexterity() + 1.2 * s.getIntelligence());
    }

    @Override
    public int speed(Stats s) {
        return (int) Math.floor(0.4 * s.getDexterity() + 0.1 * s.getStrength());
    }
}

