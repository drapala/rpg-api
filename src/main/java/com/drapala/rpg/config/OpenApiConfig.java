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
                description = "Spring Boot API for characters and battles",
                contact = @io.swagger.v3.oas.annotations.info.Contact(
                        name = "Drapala",
                        email = "joao.drapala@gmail.com"
                ),
                license = @io.swagger.v3.oas.annotations.info.License(
                        name = "Apache-2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @io.swagger.v3.oas.annotations.servers.Server(url = "http://localhost:8080", description = "Local")
        }
)
public class OpenApiConfig {

    @Bean
    GroupedOpenApi publicApi() {
        return GroupedOpenApi
                .builder()
                .group("rpg")
                .pathsToMatch("/api/**")
                .build();
    }
}
