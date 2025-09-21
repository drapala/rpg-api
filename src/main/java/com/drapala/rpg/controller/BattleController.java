package com.drapala.rpg.controller;

import com.drapala.rpg.dto.BattleRequest;
import com.drapala.rpg.dto.BattleResponse;
import com.drapala.rpg.service.BattleService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Battles", description = "Simulate battles with detailed logs")
@RequestMapping(value = "/api/battles", produces = "application/json")
public class BattleController {
    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping
    @Operation(operationId = "startBattle", summary = "Start battle", description = "Simulates a battle between two characters and returns a log and outcome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Battle result",
                    content = @Content(schema = @Schema(implementation = BattleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Entity not found",
                    content = @Content(schema = @Schema(implementation = com.drapala.rpg.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Invalid state (dead/same character)",
                    content = @Content(schema = @Schema(implementation = com.drapala.rpg.dto.ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = com.drapala.rpg.dto.ErrorResponse.class)))
    })
    public BattleResponse battle(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Battle participants",
                    content = @Content(
                            schema = @Schema(implementation = BattleRequest.class),
                            examples = @ExampleObject(value = "{\n  \"attackerId\": \"550e8400-e29b-41d4-a716-446655440000\",\n  \"defenderId\": \"6ba7b810-9dad-11d1-80b4-00c04fd430c8\"\n}")))
            @Valid @org.springframework.web.bind.annotation.RequestBody BattleRequest request) {
        return battleService.battle(request);
    }
}
