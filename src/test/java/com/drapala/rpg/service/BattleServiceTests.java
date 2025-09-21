package com.drapala.rpg.service;

import com.drapala.rpg.dto.BattleRequest;
import com.drapala.rpg.dto.BattleResponse;
import com.drapala.rpg.dto.CreateCharacterRequest;
import com.drapala.rpg.model.Job;
import com.drapala.rpg.repository.InMemoryCharacterRepository;
import com.drapala.rpg.service.stats.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BattleServiceTests {

    private CharacterService newCharacterService(InMemoryCharacterRepository repo) {
        StatsFactory statsFactory = new StatsFactory();
        StatsCalculatorResolver resolver = new StatsCalculatorResolver(new WarriorStatsCalculator(), new ThiefStatsCalculator(), new MageStatsCalculator());
        return new CharacterService(repo, statsFactory, resolver);
    }

    private BattleService newBattleService(InMemoryCharacterRepository repo) {
        StatsCalculatorResolver resolver = new StatsCalculatorResolver(new WarriorStatsCalculator(), new ThiefStatsCalculator(), new MageStatsCalculator());
        return new BattleService(repo, resolver, new SimpleMeterRegistry());
    }

    @Test
    void warriorBeatsThiefExample() {
        InMemoryCharacterRepository repo = new InMemoryCharacterRepository();
        CharacterService characters = newCharacterService(repo);
        BattleService battle = newBattleService(repo);

        CreateCharacterRequest a = new CreateCharacterRequest();
        a.setName("Arthur_Hero");
        a.setJob(Job.WARRIOR);
        var attacker = characters.create(a);

        CreateCharacterRequest d = new CreateCharacterRequest();
        d.setName("Shadow_Thief");
        d.setJob(Job.THIEF);
        var defender = characters.create(d);

        BattleRequest req = new BattleRequest();
        req.setAttackerId(UUID.fromString(attacker.getId()));
        req.setDefenderId(UUID.fromString(defender.getId()));

        BattleResponse res = battle.battle(req);
        // With base stats, Thief is faster and deals higher damage; Thief should win.
        assertEquals(defender.getId(), res.getWinnerId());
        assertTrue(res.getBattleLog().size() >= 2);
    }
}
