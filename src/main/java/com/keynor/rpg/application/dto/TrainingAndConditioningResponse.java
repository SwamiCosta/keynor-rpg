package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.TrainingAndConditioning;

public record TrainingAndConditioningResponse(int vigor, int reflexes) {

    public static TrainingAndConditioningResponse from(TrainingAndConditioning trainingAndConditioning) {
        return new TrainingAndConditioningResponse(trainingAndConditioning.getVigor(),
                trainingAndConditioning.getReflexes());
    }
}
