package com.drapala.rpg.service;

import com.drapala.rpg.dto.CharacterResponse;
import com.drapala.rpg.dto.CreateCharacterRequest;
import com.drapala.rpg.model.Character;
import com.drapala.rpg.model.Job;
import com.drapala.rpg.model.Stats;
import com.drapala.rpg.repository.CharacterRepository;
import com.drapala.rpg.service.stats.StatsCalculator;
import com.drapala.rpg.service.stats.StatsCalculatorResolver;
import com.drapala.rpg.service.stats.StatsFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Service
public class CharacterService {
    private final CharacterRepository repository;
    private final StatsFactory statsFactory;
    private final StatsCalculatorResolver calculatorResolver;

    public CharacterService(CharacterRepository repository, StatsFactory statsFactory, StatsCalculatorResolver calculatorResolver) {
        this.repository = repository;
        this.statsFactory = statsFactory;
        this.calculatorResolver = calculatorResolver;
    }

    public CharacterResponse create(CreateCharacterRequest req) {
        Stats stats = statsFactory.createForJob(req.getJob());
        Character c = Character.builder()
                .id(UUID.randomUUID())
                .name(req.getName())
                .job(req.getJob())
                .stats(stats)
                .build();
        repository.save(c);
        return toResponse(c);
    }

    public List<CharacterResponse> list() {
        return repository.findAll().stream().map(this::toResponse).collect(toList());
    }

    public CharacterResponse get(UUID id) {
        Character c = repository.findById(id).orElseThrow();
        return toResponse(c);
    }

    private CharacterResponse toResponse(Character c) {
        StatsCalculator calc = calculatorResolver.forJob(c.getJob());
        int attack = calc.attack(c.getStats());
        int speed = calc.speed(c.getStats());
        return CharacterResponse.builder()
                .id(c.getId().toString())
                .name(c.getName())
                .job(c.getJob())
                .alive(c.isAlive())
                .currentLifePoints(c.getCurrentLifePoints())
                .maxLifePoints(c.getStats().getLifePoints())
                .strength(c.getStats().getStrength())
                .dexterity(c.getStats().getDexterity())
                .intelligence(c.getStats().getIntelligence())
                .attack(attack)
                .speed(speed)
                .build();
    }
}

