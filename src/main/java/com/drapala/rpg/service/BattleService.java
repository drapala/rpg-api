package com.drapala.rpg.service;

import com.drapala.rpg.dto.BattleRequest;
import com.drapala.rpg.dto.BattleResponse;
import com.drapala.rpg.model.Character;
import com.drapala.rpg.repository.CharacterRepository;
import com.drapala.rpg.service.stats.StatsCalculator;
import com.drapala.rpg.service.stats.StatsCalculatorResolver;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class BattleService {
    private final CharacterRepository repository;
    private final StatsCalculatorResolver resolver;

    public BattleService(CharacterRepository repository, StatsCalculatorResolver resolver) {
        this.repository = repository;
        this.resolver = resolver;
    }

    public BattleResponse battle(BattleRequest request) {
        UUID aId = request.getAttackerId();
        UUID dId = request.getDefenderId();

        Character attacker = repository.findById(aId).orElseThrow();
        Character defender = repository.findById(dId).orElseThrow();

        if (!attacker.isAlive() || !defender.isAlive()) {
            throw new IllegalStateException("Both characters must be alive to battle");
        }

        String battleId = UUID.randomUUID().toString();
        MDC.put("battleId", battleId);
        try {
            List<String> logLines = new ArrayList<>();
            log.info("Battle started: {} vs {}", attacker.getId(), defender.getId());
            logLines.add(String.format("Battle between %s (%s) - %d HP and %s (%s) - %d HP begins!",
                    attacker.getName(), attacker.getJob(), attacker.getCurrentLifePoints(),
                    defender.getName(), defender.getJob(), defender.getCurrentLifePoints()));

            // Determine first attacker by speed
            Character first = firstAttacker(attacker, defender);
            Character second = first == attacker ? defender : attacker;

            while (attacker.isAlive() && defender.isAlive()) {
                executeHit(first, second, logLines);
                if (!second.isAlive()) break;
                executeHit(second, first, logLines);
            }

            Character winner = attacker.isAlive() ? attacker : defender;
            Character loser = attacker.isAlive() ? defender : attacker;
            logLines.add(String.format("%s wins the battle! %s still has %d HP remaining!",
                    winner.getName(), winner.getName(), winner.getCurrentLifePoints()));
            log.info("Battle finished: winner={} loser={}", winner.getId(), loser.getId());

            // Persist state
            repository.save(attacker);
            repository.save(defender);

            return BattleResponse.builder()
                    .winnerId(winner.getId().toString())
                    .loserId(loser.getId().toString())
                    .battleLog(logLines)
                    .build();
        } finally {
            MDC.remove("battleId");
        }
    }

    private Character firstAttacker(Character a, Character b) {
        int sa = resolver.forJob(a.getJob()).speed(a.getStats());
        int sb = resolver.forJob(b.getJob()).speed(b.getStats());
        if (sa == sb) return a; // tie -> request attacker
        return sa > sb ? a : b;
    }

    private void executeHit(Character atk, Character def, List<String> logLines) {
        StatsCalculator calc = resolver.forJob(atk.getJob());
        int damage = Math.max(0, calc.attack(atk.getStats()));
        int newHp = Math.max(0, def.getCurrentLifePoints() - damage);
        def.setCurrentLifePoints(newHp);
        if (newHp == 0) {
            def.setAlive(false);
        }
        logLines.add(String.format("%s attacks %s for %d damage, %s has %d HP remaining.",
                atk.getName(), def.getName(), damage, def.getName(), newHp));
    }
}

