package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodyComposition;

public record BodyCompositionResponse(double totalMass, double bodyFatPercentage, double fatMass, double muscleMass,
                                       double dominantFiberType, double neuromuscularEfficiency) {

    public static BodyCompositionResponse from(BodyComposition composition) {
        return new BodyCompositionResponse(composition.getTotalMass(), composition.getBodyFatPercentage(),
                composition.getFatMass(), composition.getMuscleMass(), composition.getDominantFiberType(),
                composition.getNeuromuscularEfficiency());
    }
}
