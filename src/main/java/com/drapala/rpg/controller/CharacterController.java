package com.drapala.rpg.controller;

import com.drapala.rpg.dto.CharacterResponse;
import com.drapala.rpg.dto.CreateCharacterRequest;
import com.drapala.rpg.service.CharacterService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(operationId = "createCharacter", summary = "Create character", description = "Creates a new character with validated name and job")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Character created",
                    content = @Content(schema = @Schema(implementation = CharacterResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = com.drapala.rpg.dto.ErrorResponse.class),
                            examples = @ExampleObject(value = "{\n  \"code\": \"VALIDATION_ERROR\",\n  \"message\": \"Invalid input\",\n  \"details\": {\n    \"name\": \"Name must be 4-15 characters long\"\n  },\n  \"timestamp\": \"2025-09-21T12:00:00Z\"\n}"))),
            @ApiResponse(responseCode = "409", description = "Invalid state",
                    content = @Content(schema = @Schema(implementation = com.drapala.rpg.dto.ErrorResponse.class),
                            examples = @ExampleObject(value = "{\n  \"code\": \"CONFLICT\",\n  \"message\": \"Invalid state\",\n  \"timestamp\": \"2025-09-21T12:00:00Z\"\n}")))
    })
    public ResponseEntity<CharacterResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Character payload",
                    content = @Content(
                            schema = @Schema(implementation = CreateCharacterRequest.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\n  \"name\": \"Arthur_Hero\",\n  \"job\": \"WARRIOR\"\n}")))
            @Valid @RequestBody CreateCharacterRequest request) {
        CharacterResponse res = characters.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    @Operation(operationId = "listCharacters", summary = "List characters", description = "Returns all existing characters")
    public List<CharacterResponse> list() {
        return characters.list();
    }

    @GetMapping("/{id}")
    @Operation(operationId = "getCharacter", summary = "Get character", description = "Returns character details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Character retrieved",
                    content = @Content(schema = @Schema(implementation = CharacterResponse.class))),
            @ApiResponse(responseCode = "404", description = "Entity not found",
                    content = @Content(schema = @Schema(implementation = com.drapala.rpg.dto.ErrorResponse.class),
                            examples = @ExampleObject(value = "{\n  \"code\": \"NOT_FOUND\",\n  \"message\": \"Character not found: 550e8400-e29b-41d4-a716-446655440000\",\n  \"timestamp\": \"2025-09-21T12:00:00Z\"\n}")))
    })
    public CharacterResponse get(@Parameter(description = "Character ID", example = "550e8400-e29b-41d4-a716-446655440000")
                                 @PathVariable("id") UUID id) {
        return characters.get(id);
    }
}
