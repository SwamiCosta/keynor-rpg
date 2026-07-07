package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

public record CalculatedValuesResponse(int symbolicTotalMass, double totalMassKg) {

    public static CalculatedValuesResponse from(PlayableCharacter character) {
        return new CalculatedValuesResponse(character.getSymbolicTotalMass(), character.getTotalMassKg());
    }
}
