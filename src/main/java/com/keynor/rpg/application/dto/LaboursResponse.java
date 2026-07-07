package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Job;
import com.keynor.rpg.domain.model.Labours;
import com.keynor.rpg.domain.model.PlayableCharacter;
import java.util.LinkedHashMap;
import java.util.Map;

/** New in rpg-19: the Mind pillar's "Labours" tab — a 0-4 level per {@link Job}. */
public record LaboursResponse(Map<String, Integer> levels, PointBudgetResponse points) {

    public static LaboursResponse from(Labours labours, PlayableCharacter character) {
        Map<String, Integer> levels = new LinkedHashMap<>();
        for (Job job : Job.values()) {
            levels.put(job.name(), labours.getLevel(job));
        }
        int effectivePoints = labours.getEffectivePoints(character);
        int spent = labours.getSpentPoints();
        return new LaboursResponse(levels, new PointBudgetResponse(effectivePoints, spent, effectivePoints - spent));
    }
}
