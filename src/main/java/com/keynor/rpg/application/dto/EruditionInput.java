package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Erudition;
import com.keynor.rpg.domain.model.Knowledge;
import java.util.Map;
import java.util.stream.Collectors;

/** Rewritten in rpg-19: {@code Erudition} now holds a 0-4 level per {@link Knowledge}. */
public record EruditionInput(Map<String, Integer> levels) {

    public Erudition toDomain() {
        Map<Knowledge, Integer> parsed = levels.entrySet().stream()
                .collect(Collectors.toMap(entry -> Knowledge.valueOf(entry.getKey()), Map.Entry::getValue));
        return new Erudition(parsed);
    }
}
