package com.drapala.rpg.repository;

import com.drapala.rpg.model.Character;
import com.drapala.rpg.model.Job;
import com.drapala.rpg.model.Stats;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCharacterRepositoryTest {

    @Test
    void saveAndRetrieve() {
        InMemoryCharacterRepository repo = new InMemoryCharacterRepository();
        Character c = Character.builder()
                .id(UUID.randomUUID())
                .name("Arthur_Hero")
                .job(Job.WARRIOR)
                .stats(Stats.builder().lifePoints(20).strength(10).dexterity(5).intelligence(5).build())
                .build();
        repo.save(c);
        assertTrue(repo.findById(c.getId()).isPresent());
        assertFalse(repo.findAll().isEmpty());
    }
}

