package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Erudition;
import com.keynor.rpg.domain.model.Knowledge;
import com.keynor.rpg.domain.model.PlayableCharacter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Rewritten in rpg-19: {@code Erudition} no longer holds boolean trait selections — it holds a
 * 0-4 level per {@link Knowledge}, spent from a small shared point budget.
 */
public record EruditionResponse(Map<String, Integer> levels, PointBudgetResponse points) {

    public static EruditionResponse from(Erudition erudition, PlayableCharacter character) {
        Map<String, Integer> levels = new LinkedHashMap<>();
        for (Knowledge knowledge : Knowledge.values()) {
            levels.put(knowledge.name(), erudition.getLevel(knowledge));
        }
        int effectivePoints = erudition.getEffectivePoints(character);
        int spent = erudition.getSpentPoints();
        return new EruditionResponse(levels, new PointBudgetResponse(effectivePoints, spent, effectivePoints - spent));
    }
}
