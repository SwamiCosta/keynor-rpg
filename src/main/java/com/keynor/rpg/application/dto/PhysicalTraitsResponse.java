package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PhysicalTraits;

public record PhysicalTraitsResponse(SensorialOrgansResponse sensorialOrgans, BodyStructureResponse bodyStructure,
                                      TrainingAndConditioningResponse trainingAndConditioning) {

    public static PhysicalTraitsResponse from(PhysicalTraits physicalTraits) {
        return new PhysicalTraitsResponse(SensorialOrgansResponse.from(physicalTraits.getSensorialOrgans()),
                BodyStructureResponse.from(physicalTraits.getBodyStructure()),
                TrainingAndConditioningResponse.from(physicalTraits.getTrainingAndConditioning()));
    }
}
