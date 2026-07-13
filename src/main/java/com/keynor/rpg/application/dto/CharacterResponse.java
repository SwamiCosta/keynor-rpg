package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Language;
import com.keynor.rpg.domain.model.PlayableCharacter;

/**
 * {@code attributes}/{@code attributeBreakdowns}/{@code calculatedValues}/{@code loadCapacity}
 * moved up here from {@code BodyResponse} (Mind pillar) since several formulas now read both
 * pillars (e.g. Reasoning, Will) — attributes were always a whole-{@link PlayableCharacter}
 * concern, and this now reflects that in the response shape too.
 */
public record CharacterResponse(String id, String name, BodyResponse body, MindResponse mind,
                                 AttributesResponse attributes, AttributeBreakdownsResponse attributeBreakdowns,
                                 PoolAttributesResponse poolAttributes, CalculatedValuesResponse calculatedValues,
                                 LoadCapacityResponse loadCapacity) {

    public static CharacterResponse from(String id, PlayableCharacter character, Language language) {
        return new CharacterResponse(id, character.getName(),
                BodyResponse.from(character.getBody()),
                MindResponse.from(character.getMind(), character),
                AttributesResponse.from(character),
                AttributeBreakdownsResponse.from(character, language),
                PoolAttributesResponse.from(character),
                CalculatedValuesResponse.from(character),
                LoadCapacityResponse.from(character));
    }
}
