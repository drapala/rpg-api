package com.drapala.rpg.repository;

import com.drapala.rpg.model.Character;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("prod")
public class JpaCharacterRepository implements CharacterRepository {
    @Override
    public Character save(Character character) {
        throw new UnsupportedOperationException("JPA repository not implemented in this assignment");
    }

    @Override
    public Optional<Character> findById(UUID id) {
        throw new UnsupportedOperationException("JPA repository not implemented in this assignment");
    }

    @Override
    public List<Character> findAll() {
        throw new UnsupportedOperationException("JPA repository not implemented in this assignment");
    }
}

