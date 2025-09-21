package com.drapala.rpg.dto;

import com.drapala.rpg.model.Job;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static org.junit.jupiter.api.Assertions.*;

class DtoValidationTests {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void invalidNameTooShort() {
        CreateCharacterRequest req = new CreateCharacterRequest();
        req.setName("abc");
        req.setJob(Job.WARRIOR);
        assertFalse(validator.validate(req).isEmpty());
    }

    @Test
    void validRequestPasses() {
        CreateCharacterRequest req = new CreateCharacterRequest();
        req.setName("Arthur_Hero");
        req.setJob(Job.WARRIOR);
        assertTrue(validator.validate(req).isEmpty());
    }
}

