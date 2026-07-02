package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodyComposition;

public record BodyCompositionInput(int bodyFat, int muscleMass, int dominantFiberType, int muscleDistribution,
                                    int flexibility, int boneDensity, int tendonsAndLigaments) {

    public BodyComposition toDomain() {
        return new BodyComposition(bodyFat, muscleMass, dominantFiberType, muscleDistribution, flexibility,
                boneDensity, tendonsAndLigaments);
    }
}
