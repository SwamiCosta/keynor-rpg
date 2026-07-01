package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

public record LoadCapacityResponse(double lightLoadKg, double heavyLoadKg, double maxCapacityKg,
                                    double dragCapacityKg) {

    public static LoadCapacityResponse from(PlayableCharacter character) {
        return new LoadCapacityResponse(character.getLightLoadKg(), character.getHeavyLoadKg(),
                character.getMaxCapacityKg(), character.getDragCapacityKg());
    }
}
