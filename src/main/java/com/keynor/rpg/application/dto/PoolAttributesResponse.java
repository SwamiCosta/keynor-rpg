package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

/**
 * The five "Pool Attributes" (Stamina Pool, Mental Health Pool, Memory Pool, Mana Pool, Chi
 * Pool) — each carries a total and a current value, tracked as two separate fields rather than
 * folded into the flat {@link AttributesResponse}. Sibling of {@code attributes} on both
 * {@link CharacterResponse} and {@link CharacterPreviewResponse}. Current always equals total
 * today — see {@code PoolAttribute}'s own javadoc for why.
 */
public record PoolAttributesResponse(PoolAttributeResponse staminaPool, PoolAttributeResponse mentalHealthPool,
                                      PoolAttributeResponse memoryPool, PoolAttributeResponse manaPool,
                                      PoolAttributeResponse chiPool) {

    public static PoolAttributesResponse from(PlayableCharacter character) {
        return new PoolAttributesResponse(
                PoolAttributeResponse.from(character.getStaminaPoolAttribute()),
                PoolAttributeResponse.from(character.getMentalHealthPoolAttribute()),
                PoolAttributeResponse.from(character.getMemoryPoolAttribute()),
                PoolAttributeResponse.from(character.getManaPoolAttribute()),
                PoolAttributeResponse.from(character.getChiPoolAttribute()));
    }
}
