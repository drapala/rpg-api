package com.drapala.rpg.controller;

import com.drapala.rpg.dto.CharacterResponse;
import com.drapala.rpg.dto.CreateCharacterRequest;
import com.drapala.rpg.service.CharacterService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Characters", description = "Create, list, and retrieve characters")
@RequestMapping(value = "/api/characters", produces = "application/json")
public class CharacterController {
    private final CharacterService characters;

    public CharacterController(CharacterService characters) {
        this.characters = characters;
    }

    @PostMapping
    @Operation(summary = "Create character", description = "Creates a new character with validated name and job")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = CharacterResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Invalid state", content = @Content)
    })
    public ResponseEntity<CharacterResponse> create(@Valid @RequestBody CreateCharacterRequest request) {
        CharacterResponse res = characters.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    @Operation(summary = "List characters", description = "Returns all existing characters")
    public List<CharacterResponse> list() {
        return characters.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get character", description = "Returns character details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = CharacterResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content)
    })
    public CharacterResponse get(@PathVariable("id") UUID id) {
        return characters.get(id);
    }
}
