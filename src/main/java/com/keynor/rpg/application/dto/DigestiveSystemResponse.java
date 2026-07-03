package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.DigestiveSystem;

public record DigestiveSystemResponse(int nutrientAbsorption, int impurityCleaning, int ketosisQuality) {

    public static DigestiveSystemResponse from(DigestiveSystem digestiveSystem) {
        return new DigestiveSystemResponse(digestiveSystem.getNutrientAbsorption(),
                digestiveSystem.getImpurityCleaning(), digestiveSystem.getKetosisQuality());
    }
}
