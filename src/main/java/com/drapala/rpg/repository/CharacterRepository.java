package com.drapala.rpg.repository;

import com.drapala.rpg.model.Character;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CharacterRepository {
    Character save(Character character);
    Optional<Character> findById(UUID id);
    List<Character> findAll();
}

