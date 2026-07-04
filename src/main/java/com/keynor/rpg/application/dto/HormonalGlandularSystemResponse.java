package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.HormonalGlandularSystem;

public record HormonalGlandularSystemResponse(int thyroid, int adrenalGlands, int predominantMorphicHormone,
                                               int subtleEpiphysealGland) {

    public static HormonalGlandularSystemResponse from(HormonalGlandularSystem hormonalGlandularSystem) {
        return new HormonalGlandularSystemResponse(hormonalGlandularSystem.getThyroid(),
                hormonalGlandularSystem.getAdrenalGlands(), hormonalGlandularSystem.getPredominantMorphicHormone(),
                hormonalGlandularSystem.getSubtleEpiphysealGland());
    }
}
