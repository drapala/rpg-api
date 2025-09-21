package com.drapala.rpg.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class ErrorResponse {
    @Schema(example = "VALIDATION_ERROR")
    String code;
    @Schema(example = "Invalid input")
    String message;
    @Schema(description = "Field errors or additional details")
    Map<String, String> details;
    @Schema(example = "2025-09-21T12:00:00Z")
    String timestamp;
}

