package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.DigestiveSystem;

public record DigestiveSystemInput(int digestiveAbsorption, int impurityCleaning, int ketosisEfficiency) {

    public DigestiveSystem toDomain() {
        return new DigestiveSystem(digestiveAbsorption, impurityCleaning, ketosisEfficiency);
    }
}
