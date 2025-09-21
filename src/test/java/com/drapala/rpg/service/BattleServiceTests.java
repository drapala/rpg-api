package com.drapala.rpg.service;

import com.drapala.rpg.dto.BattleRequest;
import com.drapala.rpg.dto.BattleResponse;
import com.drapala.rpg.dto.CreateCharacterRequest;
import com.drapala.rpg.model.Character;
import com.drapala.rpg.model.Job;
import com.drapala.rpg.repository.CharacterRepository;
import com.drapala.rpg.repository.InMemoryCharacterRepository;
import com.drapala.rpg.service.stats.*;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    void testBattleWithMinimumDamage() {
        // Teste quando o dano é sempre mínimo (random sempre retorna 0)
        InMemoryCharacterRepository repo = new InMemoryCharacterRepository();
        CharacterService characters = newCharacterService(repo);

        // Criar um BattleService mockado para controlar o random
        StatsCalculatorResolver resolver = new StatsCalculatorResolver(
            new WarriorStatsCalculator(),
            new ThiefStatsCalculator(),
            new MageStatsCalculator()
        );

        // Criar dois guerreiros para ter stats previsíveis
        CreateCharacterRequest w1 = new CreateCharacterRequest();
        w1.setName("Warrior_One");
        w1.setJob(Job.WARRIOR);
        var warrior1 = characters.create(w1);

        CreateCharacterRequest w2 = new CreateCharacterRequest();
        w2.setName("Warrior_Two");
        w2.setJob(Job.WARRIOR);
        var warrior2 = characters.create(w2);

        // Com dano mínimo (attack/2), a batalha será mais longa
        BattleService battleService = new BattleService(repo, resolver, new SimpleMeterRegistry()) {
            @Override
            protected int calculateDamage(int attack) {
                return attack / 2; // Sempre retorna o dano mínimo
            }
        };

        BattleRequest req = new BattleRequest();
        req.setAttackerId(UUID.fromString(warrior1.getId()));
        req.setDefenderId(UUID.fromString(warrior2.getId()));

        BattleResponse res = battleService.battle(req);

        // A batalha deve ter ocorrido e ter mais turnos devido ao dano mínimo
        assertNotNull(res.getWinnerId());
        assertNotNull(res.getLoserId());
        assertTrue(res.getBattleLog().size() > 4); // Mais turnos devido ao dano baixo

        // Verificar que o log menciona o dano reduzido
        boolean hasDamageLog = res.getBattleLog().stream()
            .anyMatch(log -> log.contains("damage") && log.contains("HP remaining"));
        assertTrue(hasDamageLog);
    }

    @Test
    void testBattleWithMaximumDamage() {
        // Teste quando o dano é sempre máximo (random sempre retorna max)
        InMemoryCharacterRepository repo = new InMemoryCharacterRepository();
        CharacterService characters = newCharacterService(repo);

        StatsCalculatorResolver resolver = new StatsCalculatorResolver(
            new WarriorStatsCalculator(),
            new ThiefStatsCalculator(),
            new MageStatsCalculator()
        );

        // Criar um mago (alto ataque) vs thief (vida menor)
        CreateCharacterRequest m = new CreateCharacterRequest();
        m.setName("Powerful_Mage");
        m.setJob(Job.MAGE);
        var mage = characters.create(m);

        CreateCharacterRequest t = new CreateCharacterRequest();
        t.setName("Fragile_Thief");
        t.setJob(Job.THIEF);
        var thief = characters.create(t);

        // Com dano máximo (attack), a batalha será mais curta
        BattleService battleService = new BattleService(repo, resolver, new SimpleMeterRegistry()) {
            @Override
            protected int calculateDamage(int attack) {
                return attack; // Sempre retorna o dano máximo
            }
        };

        BattleRequest req = new BattleRequest();
        req.setAttackerId(UUID.fromString(mage.getId()));
        req.setDefenderId(UUID.fromString(thief.getId()));

        BattleResponse res = battleService.battle(req);

        // A batalha deve ter ocorrido e terminar rapidamente
        assertNotNull(res.getWinnerId());
        assertNotNull(res.getLoserId());

        // Com dano máximo, a batalha deve terminar em poucos turnos
        // Thief tem 15 HP, Mage tem attack ~10, então ~2 hits para derrotar
        assertTrue(res.getBattleLog().size() >= 3); // Início + pelo menos 1 turno + fim

        // Verificar que há log de vitória
        boolean hasWinLog = res.getBattleLog().stream()
            .anyMatch(log -> log.contains("wins the battle"));
        assertTrue(hasWinLog);
    }

    @Test
    void testBattleBetweenSameCharacterThrowsException() {
        InMemoryCharacterRepository repo = new InMemoryCharacterRepository();
        CharacterService characters = newCharacterService(repo);
        BattleService battle = newBattleService(repo);

        CreateCharacterRequest req = new CreateCharacterRequest();
        req.setName("Lonely_One");
        req.setJob(Job.WARRIOR);
        var character = characters.create(req);

        BattleRequest battleReq = new BattleRequest();
        UUID id = UUID.fromString(character.getId());
        battleReq.setAttackerId(id);
        battleReq.setDefenderId(id);

        assertThrows(IllegalStateException.class, () -> battle.battle(battleReq),
            "Attacker and defender must be different");
    }

    @Test
    void testBattleWithDeadCharacterThrowsException() {
        InMemoryCharacterRepository repo = new InMemoryCharacterRepository();
        CharacterService characters = newCharacterService(repo);

        CreateCharacterRequest alive = new CreateCharacterRequest();
        alive.setName("Alive_Hero");
        alive.setJob(Job.WARRIOR);
        var aliveChar = characters.create(alive);

        CreateCharacterRequest dead = new CreateCharacterRequest();
        dead.setName("Dead_Hero");
        dead.setJob(Job.MAGE);
        var deadChar = characters.create(dead);

        // Simular personagem morto
        Character deadCharacter = repo.findById(UUID.fromString(deadChar.getId())).get();
        deadCharacter.setCurrentLifePoints(0);
        deadCharacter.setAlive(false);
        repo.save(deadCharacter);

        BattleService battle = newBattleService(repo);
        BattleRequest req = new BattleRequest();
        req.setAttackerId(UUID.fromString(aliveChar.getId()));
        req.setDefenderId(UUID.fromString(deadChar.getId()));

        assertThrows(IllegalStateException.class, () -> battle.battle(req),
            "Both characters must be alive to battle");
    }
}
