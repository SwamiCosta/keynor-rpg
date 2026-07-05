package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Body;

/**
 * Structural Body-pillar inputs only. {@code attributes}, {@code attributeBreakdowns},
 * {@code calculatedValues}, and {@code loadCapacity} moved up to {@link CharacterResponse}
 * (unified preview contract) since they are now a whole-character concern computed from both
 * Body and Mind, not a Body-only one.
 */
public record BodyResponse(BiomechanicsResponse biomechanics, BodySystemsResponse bodySystems,
                            PhysicalTraitsResponse physicalTraits, PointBudgetResponse geneticPoints,
                            PointBudgetResponse trainingPoints) {

    public static BodyResponse from(Body body) {
        return new BodyResponse(
                BiomechanicsResponse.from(body.getBiomechanics()),
                BodySystemsResponse.from(body.getBodySystems()),
                PhysicalTraitsResponse.from(body.getPhysicalTraits()),
                PointBudgetResponse.from(body.getGeneticPoints()),
                PointBudgetResponse.from(body.getTrainingPoints()));
    }
}
