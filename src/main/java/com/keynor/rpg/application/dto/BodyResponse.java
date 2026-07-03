package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

public record BodyResponse(BiomechanicsResponse biomechanics, BodySystemsResponse bodySystems,
                            PhysicalTraitsResponse physicalTraits, AttributesResponse attributes,
                            CalculatedValuesResponse calculatedValues, LoadCapacityResponse loadCapacity,
                            PointBudgetResponse geneticPoints, PointBudgetResponse trainingPoints) {

    public static BodyResponse from(PlayableCharacter character) {
        return new BodyResponse(
                BiomechanicsResponse.from(character.getBody().getBiomechanics()),
                BodySystemsResponse.from(character.getBody().getBodySystems()),
                PhysicalTraitsResponse.from(character.getBody().getPhysicalTraits()),
                AttributesResponse.from(character),
                CalculatedValuesResponse.from(character),
                LoadCapacityResponse.from(character),
                PointBudgetResponse.from(character.getBody().getGeneticPoints()),
                PointBudgetResponse.from(character.getBody().getTrainingPoints()));
    }
}
