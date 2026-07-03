package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.DigestiveSystem;

public record DigestiveSystemInput(int nutrientAbsorption, int impurityCleaning, int ketosisQuality) {

    public DigestiveSystem toDomain() {
        return new DigestiveSystem(nutrientAbsorption, impurityCleaning, ketosisQuality);
    }
}
