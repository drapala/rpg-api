package com.drapala.rpg.service.stats;

import com.drapala.rpg.model.Job;
import com.drapala.rpg.model.Stats;
import org.springframework.stereotype.Component;

@Component
public class StatsFactory {
    public Stats createForJob(Job job) {
        return switch (job) {
            case WARRIOR -> Stats.builder().lifePoints(20).strength(10).dexterity(5).intelligence(5).build().validate();
            case THIEF -> Stats.builder().lifePoints(15).strength(4).dexterity(10).intelligence(4).build().validate();
            case MAGE -> Stats.builder().lifePoints(12).strength(5).dexterity(6).intelligence(10).build().validate();
        };
    }
}

