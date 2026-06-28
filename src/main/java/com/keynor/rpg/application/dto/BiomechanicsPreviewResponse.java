package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Biomechanics;

public record BiomechanicsPreviewResponse(AttributesResponse attributes, CalculatedValuesResponse calculatedValues) {

    public static BiomechanicsPreviewResponse from(Biomechanics biomechanics) {
        return new BiomechanicsPreviewResponse(AttributesResponse.from(biomechanics),
                CalculatedValuesResponse.from(biomechanics));
    }
}
