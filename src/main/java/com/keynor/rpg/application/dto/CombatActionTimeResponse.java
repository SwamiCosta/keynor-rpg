package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.CombatActionTimeResult;

public record CombatActionTimeResponse(int ut, double score) {

    public static CombatActionTimeResponse from(CombatActionTimeResult result) {
        return new CombatActionTimeResponse(result.ut(), result.score());
    }
}
