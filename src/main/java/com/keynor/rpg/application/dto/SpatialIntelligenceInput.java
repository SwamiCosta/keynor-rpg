package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.SpatialIntelligence;

public record SpatialIntelligenceInput(int perception, int agility, int precision) {

    public SpatialIntelligence toDomain() {
        return new SpatialIntelligence(perception, agility, precision);
    }
}
