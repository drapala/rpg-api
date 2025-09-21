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
    @Operation(summary = "Start battle", description = "Simulates a battle between two characters and returns a log and outcome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = BattleResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Invalid state (dead/same character)", content = @Content),
            @ApiResponse(responseCode = "422", description = "Validation error", content = @Content)
    })
    public BattleResponse battle(@Valid @RequestBody BattleRequest request) {
        return battleService.battle(request);
    }
}
