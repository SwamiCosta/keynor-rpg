package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

public record BiomechanicsPreviewResponse(AttributesResponse attributes,
                                           AttributeBreakdownsResponse attributeBreakdowns,
                                           CalculatedValuesResponse calculatedValues,
                                           LoadCapacityResponse loadCapacity) {

    public static BiomechanicsPreviewResponse from(PlayableCharacter character) {
        return new BiomechanicsPreviewResponse(AttributesResponse.from(character),
                AttributeBreakdownsResponse.from(character), CalculatedValuesResponse.from(character),
                LoadCapacityResponse.from(character));
    }
}
