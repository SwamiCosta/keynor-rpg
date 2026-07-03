package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodyComposition;

public record BodyCompositionResponse(int bodyFat, int muscleMass, int dominantFiberType, int muscleDistribution,
                                       int flexibility, int boneDensity, int tendonsAndLigaments) {

    public static BodyCompositionResponse from(BodyComposition composition) {
        return new BodyCompositionResponse(composition.getBodyFat(), composition.getMuscleMass(),
                composition.getDominantFiberType(), composition.getMuscleDistribution(),
                composition.getFlexibility(), composition.getBoneDensity(), composition.getTendonsAndLigaments());
    }
}
