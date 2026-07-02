package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

public record BodyResponse(BiomechanicsResponse biomechanics, BodySystemsResponse bodySystems,
                            SpatialIntelligenceResponse spatialIntelligence, AttributesResponse attributes,
                            CalculatedValuesResponse calculatedValues, LoadCapacityResponse loadCapacity,
                            PointBudgetResponse geneticPoints, PointBudgetResponse trainingPoints) {

    public static BodyResponse from(PlayableCharacter character) {
        return new BodyResponse(
                BiomechanicsResponse.from(character.getBody().getBiomechanics()),
                BodySystemsResponse.from(character.getBody().getBodySystems()),
                SpatialIntelligenceResponse.from(character.getBody().getSpatialIntelligence()),
                AttributesResponse.from(character),
                CalculatedValuesResponse.from(character),
                LoadCapacityResponse.from(character),
                PointBudgetResponse.from(character.getBody().getGeneticPoints()),
                PointBudgetResponse.from(character.getBody().getTrainingPoints()));
    }
}
