package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodyComposition;

public record BodyCompositionResponse(double bodyFat, double muscleMass, double dominantFiberType,
                                       double muscleDistribution, double flexibility) {

    public static BodyCompositionResponse from(BodyComposition composition) {
        return new BodyCompositionResponse(composition.getBodyFat(), composition.getMuscleMass(),
                composition.getDominantFiberType(), composition.getMuscleDistribution(),
                composition.getFlexibility());
    }
}
