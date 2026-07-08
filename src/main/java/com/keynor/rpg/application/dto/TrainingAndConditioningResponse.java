package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.TrainingAndConditioning;

public record TrainingAndConditioningResponse(int vigor, int reflexes, int intensity, int coordination,
                                                int resilience, int fighting, int weaponPracticing, int shooting) {

    public static TrainingAndConditioningResponse from(TrainingAndConditioning trainingAndConditioning) {
        return new TrainingAndConditioningResponse(
                trainingAndConditioning.getVigor(),
                trainingAndConditioning.getReflexes(),
                trainingAndConditioning.getIntensity(),
                trainingAndConditioning.getCoordination(),
                trainingAndConditioning.getResilience(),
                trainingAndConditioning.getFighting(),
                trainingAndConditioning.getWeaponPracticing(),
                trainingAndConditioning.getShooting());
    }
}
