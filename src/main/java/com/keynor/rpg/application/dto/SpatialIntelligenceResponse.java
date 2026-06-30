package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.SpatialIntelligence;

public record SpatialIntelligenceResponse(double perception, double agility, double precision) {

    public static SpatialIntelligenceResponse from(SpatialIntelligence spatialIntelligence) {
        return new SpatialIntelligenceResponse(spatialIntelligence.getPerception(),
                spatialIntelligence.getAgility(), spatialIntelligence.getPrecision());
    }
}
