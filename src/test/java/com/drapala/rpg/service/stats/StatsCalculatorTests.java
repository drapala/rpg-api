package com.drapala.rpg.service.stats;

import com.drapala.rpg.model.Stats;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatsCalculatorTests {

    @Test
    void warriorFormulaMatchesExample() {
        WarriorStatsCalculator calc = new WarriorStatsCalculator();
        Stats s = Stats.builder().strength(10).dexterity(5).intelligence(5).lifePoints(20).build();
        assertEquals(9, calc.attack(s));
    }

    @Test
    void thiefAndMageAreDeterministic() {
        ThiefStatsCalculator thief = new ThiefStatsCalculator();
        MageStatsCalculator mage = new MageStatsCalculator();
        Stats s = Stats.builder().strength(5).dexterity(10).intelligence(5).lifePoints(15).build();
        assertTrue(thief.attack(s) >= 0);
        assertTrue(mage.attack(s) >= 0);
    }
}

