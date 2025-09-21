package com.drapala.rpg.config;

import com.drapala.rpg.dto.CreateCharacterRequest;
import com.drapala.rpg.model.Job;
import com.drapala.rpg.service.CharacterService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevDataSeeder {

    @Bean
    ApplicationRunner seedCharacters(CharacterService characters) {
        return args -> {
            if (characters.list().isEmpty()) {
                CreateCharacterRequest w = new CreateCharacterRequest();
                w.setName("Arthur_Hero");
                w.setJob(Job.WARRIOR);
                characters.create(w);

                CreateCharacterRequest t = new CreateCharacterRequest();
                t.setName("Shadow_Thief");
                t.setJob(Job.THIEF);
                characters.create(t);
            }
        };
    }
}

