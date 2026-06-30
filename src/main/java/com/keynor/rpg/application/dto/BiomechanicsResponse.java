package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Biomechanics;

public record BiomechanicsResponse(GeneticsResponse genetics, BodyCompositionResponse bodyComposition) {

    public static BiomechanicsResponse from(Biomechanics biomechanics) {
        return new BiomechanicsResponse(
                GeneticsResponse.from(biomechanics.getGenetics()),
                BodyCompositionResponse.from(biomechanics.getBodyComposition()));
    }
}
