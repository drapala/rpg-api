package com.drapala.rpg.service;

import com.drapala.rpg.dto.CharacterResponse;
import com.drapala.rpg.dto.CreateCharacterRequest;
import com.drapala.rpg.model.Job;
import com.drapala.rpg.repository.InMemoryCharacterRepository;
import com.drapala.rpg.service.stats.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CharacterServiceTests {

    private CharacterService newService() {
        InMemoryCharacterRepository repo = new InMemoryCharacterRepository();
        StatsFactory statsFactory = new StatsFactory();
        StatsCalculatorResolver resolver = new StatsCalculatorResolver(new WarriorStatsCalculator(), new ThiefStatsCalculator(), new MageStatsCalculator());
        return new CharacterService(repo, statsFactory, resolver);
    }

    @Test
    void createAndGetCharacter() {
        CharacterService svc = newService();
        CreateCharacterRequest req = new CreateCharacterRequest();
        req.setName("Arthur_Hero");
        req.setJob(Job.WARRIOR);
        CharacterResponse created = svc.create(req);
        assertEquals("Arthur_Hero", created.getName());
        assertEquals(Job.WARRIOR, created.getJob());
        assertTrue(created.isAlive());
        assertEquals(20, created.getCurrentLifePoints());

        CharacterResponse fetched = svc.get(UUID.fromString(created.getId()));
        assertEquals(created.getId(), fetched.getId());
    }

    @Test
    void listCharacters() {
        CharacterService svc = newService();
        CreateCharacterRequest req = new CreateCharacterRequest();
        req.setName("Magey");
        req.setJob(Job.MAGE);
        svc.create(req);
        List<CharacterResponse> all = svc.list();
        assertFalse(all.isEmpty());
    }
}

