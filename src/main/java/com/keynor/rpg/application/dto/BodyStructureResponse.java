package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodyStructure;

public record BodyStructureResponse(int skinThickness, int shapeAesthetics, int cellularHealth) {

    public static BodyStructureResponse from(BodyStructure bodyStructure) {
        return new BodyStructureResponse(bodyStructure.getSkinThickness(), bodyStructure.getShapeAesthetics(),
                bodyStructure.getCellularHealth());
    }
}
