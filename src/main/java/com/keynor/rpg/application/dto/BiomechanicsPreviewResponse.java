package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

public record BiomechanicsPreviewResponse(AttributesResponse attributes, CalculatedValuesResponse calculatedValues) {

    public static BiomechanicsPreviewResponse from(PlayableCharacter character) {
        return new BiomechanicsPreviewResponse(AttributesResponse.from(character),
                CalculatedValuesResponse.from(character));
    }
}
