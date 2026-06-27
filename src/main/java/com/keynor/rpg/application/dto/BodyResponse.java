package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Body;

public record BodyResponse(BiomechanicsResponse biomechanics) {

    public static BodyResponse from(Body body) {
        return new BodyResponse(BiomechanicsResponse.from(body.getBiomechanics()));
    }
}
