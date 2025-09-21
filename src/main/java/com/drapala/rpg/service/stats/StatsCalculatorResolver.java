package com.drapala.rpg.service.stats;

import com.drapala.rpg.model.Job;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class StatsCalculatorResolver {
    private final Map<Job, StatsCalculator> calculators = new EnumMap<>(Job.class);

    public StatsCalculatorResolver(WarriorStatsCalculator warrior,
                                   ThiefStatsCalculator thief,
                                   MageStatsCalculator mage) {
        calculators.put(Job.WARRIOR, warrior);
        calculators.put(Job.THIEF, thief);
        calculators.put(Job.MAGE, mage);
    }

    public StatsCalculator forJob(Job job) {
        return calculators.get(job);
    }
}

