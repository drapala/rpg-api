package com.drapala.rpg.controller;

import com.drapala.rpg.dto.CharacterResponse;
import com.drapala.rpg.dto.CreateCharacterRequest;
import com.drapala.rpg.service.CharacterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/api/characters", produces = "application/json")
public class CharacterController {
    private final CharacterService characters;

    public CharacterController(CharacterService characters) {
        this.characters = characters;
    }

    @PostMapping
    public ResponseEntity<CharacterResponse> create(@Valid @RequestBody CreateCharacterRequest request) {
        CharacterResponse res = characters.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    public List<CharacterResponse> list() {
        return characters.list();
    }

    @GetMapping("/{id}")
    public CharacterResponse get(@PathVariable("id") UUID id) {
        return characters.get(id);
    }
}
