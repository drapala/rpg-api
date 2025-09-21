package com.drapala.rpg.health;

import com.drapala.rpg.repository.CharacterRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("actuator")
public class CharacterRepositoryHealthIndicator implements HealthIndicator {
    private final CharacterRepository repository;

    public CharacterRepositoryHealthIndicator(CharacterRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        int count = repository.findAll().size();
        return Health.up().withDetail("characters", count).build();
    }
}

