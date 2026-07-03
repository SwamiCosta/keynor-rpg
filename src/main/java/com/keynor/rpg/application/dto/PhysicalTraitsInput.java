package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PhysicalTraits;

public record PhysicalTraitsInput(SensorialOrgansInput sensorialOrgans, BodyStructureInput bodyStructure) {

    public PhysicalTraits toDomain() {
        return new PhysicalTraits(sensorialOrgans.toDomain(), bodyStructure.toDomain());
    }
}
