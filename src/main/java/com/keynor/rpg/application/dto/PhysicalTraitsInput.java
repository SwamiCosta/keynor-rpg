package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PhysicalTraits;

public record PhysicalTraitsInput(SensorialOrgansInput sensorialOrgans, BodyStructureInput bodyStructure,
                                   TrainingAndConditioningInput trainingAndConditioning) {

    public PhysicalTraits toDomain() {
        return new PhysicalTraits(sensorialOrgans.toDomain(), bodyStructure.toDomain(),
                trainingAndConditioning.toDomain());
    }
}
