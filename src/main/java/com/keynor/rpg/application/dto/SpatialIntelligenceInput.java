package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.SpatialIntelligence;

public record SpatialIntelligenceInput(double perception, double agility, double precision) {

    public SpatialIntelligence toDomain() {
        return new SpatialIntelligence(perception, agility, precision);
    }
}
