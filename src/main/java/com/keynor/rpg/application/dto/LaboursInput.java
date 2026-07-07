package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Job;
import com.keynor.rpg.domain.model.Labours;
import java.util.Map;
import java.util.stream.Collectors;

/** New in rpg-19: the Mind pillar's "Labours" tab — a 0-4 level per {@link Job}. */
public record LaboursInput(Map<String, Integer> levels) {

    public Labours toDomain() {
        Map<Job, Integer> parsed = levels.entrySet().stream()
                .collect(Collectors.toMap(entry -> Job.valueOf(entry.getKey()), Map.Entry::getValue));
        return new Labours(parsed);
    }
}
