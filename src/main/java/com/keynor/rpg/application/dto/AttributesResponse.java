package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

public record AttributesResponse(double strength, double speed, double maxMovementSpeed, double staminaPool,
                                  double fatigueResistance, double staminaRecovery, double durability,
                                  double sight, double hearing, double smell, double evasion, double acrobatics,
                                  double meleeAccuracy, double aim) {

    public static AttributesResponse from(PlayableCharacter character) {
        return new AttributesResponse(
                character.getStrength(),
                character.getSpeed(),
                character.getMaxMovementSpeed(),
                character.getStaminaPool(),
                character.getFatigueResistance(),
                character.getStaminaRecovery(),
                character.getDurability(),
                character.getSight(),
                character.getHearing(),
                character.getSmell(),
                character.getEvasion(),
                character.getAcrobatics(),
                character.getMeleeAccuracy(),
                character.getAim());
    }
}
