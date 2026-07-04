package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.DigestiveSystem;

public record DigestiveSystemResponse(int digestiveAbsorption, int impurityCleaning, int ketosisEfficiency) {

    public static DigestiveSystemResponse from(DigestiveSystem digestiveSystem) {
        return new DigestiveSystemResponse(digestiveSystem.getDigestiveAbsorption(),
                digestiveSystem.getImpurityCleaning(), digestiveSystem.getKetosisEfficiency());
    }
}
