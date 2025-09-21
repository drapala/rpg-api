package com.drapala.rpg.service;

import com.drapala.rpg.dto.BattleRequest;
import com.drapala.rpg.dto.BattleResponse;
import com.drapala.rpg.model.Character;
import com.drapala.rpg.repository.CharacterRepository;
import com.drapala.rpg.service.stats.StatsCalculatorResolver;
import io.micrometer.core.instrument.MeterRegistry;
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
    private final MeterRegistry meterRegistry;

    public BattleService(CharacterRepository repository, StatsCalculatorResolver resolver, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.resolver = resolver;
        this.meterRegistry = meterRegistry;
    }

    public BattleResponse battle(BattleRequest request) {
        UUID aId = request.getAttackerId();
        UUID dId = request.getDefenderId();

        if (aId.equals(dId)) {
            throw new IllegalStateException("Attacker and defender must be different");
        }

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
            if (meterRegistry != null) {
                meterRegistry.counter("battle.started").increment();
            }
            logLines.add(String.format("Battle between %s (%s) - %d HP and %s (%s) - %d HP begins!",
                    attacker.getName(), attacker.getJob(), attacker.getCurrentLifePoints(),
                    defender.getName(), defender.getJob(), defender.getCurrentLifePoints()));

            // Determine first attacker by speed (precompute speeds once)
            int attackerSpeed = resolver.forJob(attacker.getJob()).speed(attacker.getStats());
            int defenderSpeed = resolver.forJob(defender.getJob()).speed(defender.getStats());
            Character first = attackerSpeed == defenderSpeed ? attacker : (attackerSpeed > defenderSpeed ? attacker : defender);
            Character second = first == attacker ? defender : attacker;

            // Precompute constant damage per attacker for current battle
            int firstDamage = resolver.forJob(first.getJob()).attack(first.getStats());
            int secondDamage = resolver.forJob(second.getJob()).attack(second.getStats());

            while (attacker.isAlive() && defender.isAlive()) {
                executeHit(first, second, firstDamage, logLines);
                if (!second.isAlive()) break;
                executeHit(second, first, secondDamage, logLines);
            }

            Character winner = attacker.isAlive() ? attacker : defender;
            Character loser = attacker.isAlive() ? defender : attacker;
            logLines.add(String.format("%s wins the battle! %s still has %d HP remaining!",
                    winner.getName(), winner.getName(), winner.getCurrentLifePoints()));
            log.info("Battle finished: winner={} loser={}", winner.getId(), loser.getId());
            if (meterRegistry != null) {
                meterRegistry.counter("battle.completed").increment();
            }

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

    private void executeHit(Character atk, Character def, int baseDamage, List<String> logLines) {
        int damage = Math.max(0, baseDamage);
        int newHp = Math.max(0, def.getCurrentLifePoints() - damage);
        def.setCurrentLifePoints(newHp);
        if (newHp == 0) {
            def.setAlive(false);
        }
        logLines.add(String.format("%s attacks %s for %d damage, %s has %d HP remaining.",
                atk.getName(), def.getName(), damage, def.getName(), newHp));
    }
}
