package com.drapala.rpg.service.stats;

import com.drapala.rpg.model.Stats;

public interface StatsCalculator {
    int attack(Stats stats);
    int speed(Stats stats);
}

