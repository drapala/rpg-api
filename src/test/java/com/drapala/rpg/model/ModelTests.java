package com.drapala.rpg.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

class ModelTests {

    @Test
    void statsRejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class, () ->
            Stats.builder().lifePoints(-1).strength(0).dexterity(0).intelligence(0).build().validate());
    }

    @Test
    void characterInitializesLifePointsAndAlive() {
        Stats stats = Stats.builder().lifePoints(20).strength(10).dexterity(5).intelligence(5).build().validate();
        Character c = Character.builder()
                .id(UUID.randomUUID())
                .name("Arthur_Hero")
                .job(Job.WARRIOR)
                .stats(stats)
                .build();

        assertEquals(20, c.getCurrentLifePoints());
        assertTrue(c.isAlive());
    }
}

