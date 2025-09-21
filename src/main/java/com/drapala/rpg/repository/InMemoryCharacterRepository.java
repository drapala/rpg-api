package com.drapala.rpg.repository;

import com.drapala.rpg.model.Character;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryCharacterRepository implements CharacterRepository {
    private final Map<UUID, Character> store = new ConcurrentHashMap<>();

    @Override
    public Character save(Character character) {
        store.put(character.getId(), character);
        return character;
    }

    @Override
    public Optional<Character> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Character> findAll() {
        return new ArrayList<>(store.values());
    }
}

