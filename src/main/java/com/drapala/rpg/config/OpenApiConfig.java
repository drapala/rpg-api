package com.drapala.rpg.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Neo RPG API",
                version = "1.0.0",
                description = "Spring Boot API for characters and battles"
        )
)
public class OpenApiConfig {

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi
                .builder()
                .group("rpg")
                .packagesToScan("com.drapala.rpg.controller")
                .build();
    }
}

