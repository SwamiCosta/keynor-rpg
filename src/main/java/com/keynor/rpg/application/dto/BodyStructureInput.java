package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodyStructure;

public record BodyStructureInput(int skinThickness, int shapeAesthetics, int cellularHealth) {

    public BodyStructure toDomain() {
        return new BodyStructure(skinThickness, shapeAesthetics, cellularHealth);
    }
}
