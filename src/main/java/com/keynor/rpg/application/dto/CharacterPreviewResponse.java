package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Language;
import com.keynor.rpg.domain.model.PlayableCharacter;

/**
 * Replaces {@code BiomechanicsPreviewResponse} — same shape (attributes are already a
 * whole-character concern, computed by {@link PlayableCharacter} from both pillars), renamed to
 * match {@link CharacterPreviewRequest} now that the request carries both Body and Mind.
 */
public record CharacterPreviewResponse(AttributesResponse attributes,
                                        AttributeBreakdownsResponse attributeBreakdowns,
                                        PoolAttributesResponse poolAttributes,
                                        CalculatedValuesResponse calculatedValues,
                                        LoadCapacityResponse loadCapacity) {

    public static CharacterPreviewResponse from(PlayableCharacter character, Language language) {
        return new CharacterPreviewResponse(AttributesResponse.from(character),
                AttributeBreakdownsResponse.from(character, language), PoolAttributesResponse.from(character),
                CalculatedValuesResponse.from(character),
                LoadCapacityResponse.from(character));
    }
}
