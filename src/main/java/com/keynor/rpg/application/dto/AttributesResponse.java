package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

public record AttributesResponse(double cardiovascularCapacity, double strength, double speed,
                                  double maxMovementSpeed, double staminaPool, double fatigueRate,
                                  double durability, double sight, double hearing, double smell,
                                  double evasion, double acrobatics, double meleeAccuracy, double aim) {

    private static final double BASELINE_INTENSITY = 1.0;

    public static AttributesResponse from(PlayableCharacter character) {
        return new AttributesResponse(
                character.getCardiovascularCapacity(),
                character.getStrength(),
                character.getSpeed(),
                character.getMaxMovementSpeed(),
                character.getStaminaPool(),
                character.getFatigueRate(BASELINE_INTENSITY),
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
