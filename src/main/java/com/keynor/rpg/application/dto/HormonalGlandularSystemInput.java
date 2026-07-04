package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.HormonalGlandularSystem;

public record HormonalGlandularSystemInput(int thyroid, int adrenalGlands, int predominantMorphicHormone,
                                            int subtleEpiphysealGland) {

    public HormonalGlandularSystem toDomain() {
        return new HormonalGlandularSystem(thyroid, adrenalGlands, predominantMorphicHormone, subtleEpiphysealGland);
    }
}
