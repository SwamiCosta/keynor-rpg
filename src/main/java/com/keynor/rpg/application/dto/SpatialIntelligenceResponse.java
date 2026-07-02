package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.SpatialIntelligence;

public record SpatialIntelligenceResponse(int perception, int agility, int precision) {

    public static SpatialIntelligenceResponse from(SpatialIntelligence spatialIntelligence) {
        return new SpatialIntelligenceResponse(spatialIntelligence.getPerception(),
                spatialIntelligence.getAgility(), spatialIntelligence.getPrecision());
    }
}
