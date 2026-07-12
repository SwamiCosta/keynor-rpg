package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.TrainingAndConditioning;

public record TrainingAndConditioningInput(int vigor, int reflexes, int intensity, int coordination,
                                             int resilience, int fighting, int weaponPracticing, int shooting) {

    public TrainingAndConditioning toDomain() {
        return new TrainingAndConditioning(vigor, reflexes, intensity, coordination, resilience, fighting,
                weaponPracticing, shooting);
    }
}
