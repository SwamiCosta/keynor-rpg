package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

public record CalculatedValuesResponse(double totalMass, double boneMass, double organWaterMass) {

    public static CalculatedValuesResponse from(PlayableCharacter character) {
        return new CalculatedValuesResponse(character.getTotalMass(), character.getBoneMass(),
                character.getOrganWaterMass());
    }
}
