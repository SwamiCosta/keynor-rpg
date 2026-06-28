package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodyComposition;

public record BodyCompositionInput(double totalMass, double bodyFatPercentage, double muscleMass,
                                    double dominantFiberType, double neuromuscularEfficiency) {

    public BodyComposition toDomain() {
        return new BodyComposition(totalMass, bodyFatPercentage, muscleMass, dominantFiberType,
                neuromuscularEfficiency);
    }
}
