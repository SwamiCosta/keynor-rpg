package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodyComposition;

public record BodyCompositionInput(double bodyFat, double muscleMass, double dominantFiberType,
                                    double neuromuscularEfficiency) {

    public BodyComposition toDomain() {
        return new BodyComposition(bodyFat, muscleMass, dominantFiberType, neuromuscularEfficiency);
    }
}
