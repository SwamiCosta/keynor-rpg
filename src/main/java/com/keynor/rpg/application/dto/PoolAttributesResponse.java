package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

/**
 * The six "Pool Attributes" (Stamina Pool, Mental Health Pool, Memory Pool, Mana Pool, Chi
 * Pool, Valor) — each carries a total and a current value, tracked as two separate fields
 * rather than folded into the flat {@link AttributesResponse}. Sibling of {@code attributes} on
 * both {@link CharacterResponse} and {@link CharacterPreviewResponse}. Current always equals
 * total for the first five — see {@code PoolAttribute}'s own javadoc for why. **Valor is the
 * first exception (2026-07-18):** its {@code current} is coupled to Physical Integrity loss —
 * see {@code PlayableCharacter#getValorAttribute()}.
 */
public record PoolAttributesResponse(PoolAttributeResponse staminaPool, PoolAttributeResponse mentalHealthPool,
                                      PoolAttributeResponse memoryPool, PoolAttributeResponse manaPool,
                                      PoolAttributeResponse chiPool, PoolAttributeResponse valor) {

    public static PoolAttributesResponse from(PlayableCharacter character) {
        return new PoolAttributesResponse(
                PoolAttributeResponse.from(character.getStaminaPoolAttribute()),
                PoolAttributeResponse.from(character.getMentalHealthPoolAttribute()),
                PoolAttributeResponse.from(character.getMemoryPoolAttribute()),
                PoolAttributeResponse.from(character.getManaPoolAttribute()),
                PoolAttributeResponse.from(character.getChiPoolAttribute()),
                PoolAttributeResponse.from(character.getValorAttribute()));
    }
}
