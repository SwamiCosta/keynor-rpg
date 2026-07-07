package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.TrainingAndConditioning;

public record TrainingAndConditioningInput(int vigor, int reflexes) {

    public TrainingAndConditioning toDomain() {
        return new TrainingAndConditioning(vigor, reflexes);
    }
}
