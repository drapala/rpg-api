package com.drapala.rpg.controller;

import com.drapala.rpg.dto.CreateCharacterRequest;
import com.drapala.rpg.model.Job;
import com.drapala.rpg.repository.CharacterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BattleControllerTests {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    CharacterRepository repository;

    @Test
    void battleWithDeadCharacterReturns409() throws Exception {
        // Create two characters via API
        CreateCharacterRequest a = new CreateCharacterRequest();
        a.setName("WarriorOne");
        a.setJob(Job.WARRIOR);
        String attackerJson = objectMapper.writeValueAsString(a);

        String attackerResponse = mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(attackerJson))
                .andReturn().getResponse().getContentAsString();

        var attackerId = objectMapper.readTree(attackerResponse).get("id").asText();

        CreateCharacterRequest d = new CreateCharacterRequest();
        d.setName("MageZero");
        d.setJob(Job.MAGE);
        String defenderJson = objectMapper.writeValueAsString(d);

        String defenderResponse = mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(defenderJson))
                .andReturn().getResponse().getContentAsString();

        var defenderId = objectMapper.readTree(defenderResponse).get("id").asText();

        // Mark defender as dead directly via repository
        var def = repository.findById(UUID.fromString(defenderId)).orElseThrow();
        def.setCurrentLifePoints(0);
        def.setAlive(false);
        repository.save(def);

        // Attempt battle → expect 409 Conflict
        String battleJson = "{\"attackerId\":\"" + attackerId + "\",\"defenderId\":\"" + defenderId + "\"}";
        mockMvc.perform(post("/api/battles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(battleJson))
                .andExpect(status().isConflict());
    }

    @Test
    void battleWithSameCharacterReturns409() throws Exception {
        CreateCharacterRequest a = new CreateCharacterRequest();
        a.setName("Solo");
        a.setJob(Job.WARRIOR);
        String attackerJson = objectMapper.writeValueAsString(a);

        String attackerResponse = mockMvc.perform(post("/api/characters")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(attackerJson))
                .andReturn().getResponse().getContentAsString();

        var id = objectMapper.readTree(attackerResponse).get("id").asText();
        String battleJson = "{\"attackerId\":\"" + id + "\",\"defenderId\":\"" + id + "\"}";
        mockMvc.perform(post("/api/battles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(battleJson))
                .andExpect(status().isConflict());
    }
}
