package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Biomechanics;

public record BiomechanicsResponse(GeneticsResponse genetics, BodyCompositionResponse bodyComposition,
                                    CalculatedValuesResponse calculatedValues, BloodSystemResponse bloodSystem,
                                    CardiacSystemResponse cardiacSystem, PulmonarySystemResponse pulmonarySystem,
                                    NervousSystemResponse nervousSystem, AttributesResponse attributes,
                                    PointBudgetResponse geneticPoints, PointBudgetResponse trainingPoints) {

    public static BiomechanicsResponse from(Biomechanics biomechanics) {
        return new BiomechanicsResponse(
                GeneticsResponse.from(biomechanics.getGenetics()),
                BodyCompositionResponse.from(biomechanics.getBodyComposition()),
                CalculatedValuesResponse.from(biomechanics),
                BloodSystemResponse.from(biomechanics.getBloodSystem()),
                CardiacSystemResponse.from(biomechanics.getCardiacSystem()),
                PulmonarySystemResponse.from(biomechanics.getPulmonarySystem()),
                NervousSystemResponse.from(biomechanics.getNervousSystem()),
                AttributesResponse.from(biomechanics),
                PointBudgetResponse.from(biomechanics.getGeneticPoints()),
                PointBudgetResponse.from(biomechanics.getTrainingPoints()));
    }
}
