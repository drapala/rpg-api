package com.drapala.rpg.controller;

import com.drapala.rpg.dto.BattleRequest;
import com.drapala.rpg.dto.BattleResponse;
import com.drapala.rpg.service.BattleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/battles")
public class BattleController {
    private final BattleService battleService;

    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    @PostMapping
    public BattleResponse battle(@Valid @RequestBody BattleRequest request) {
        return battleService.battle(request);
    }
}

