package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.Biomechanics;

public record AttributesResponse(double cardiovascularCapacity, double strength, double speed,
                                  double maxMovementSpeed, double staminaPool, double fatigueRate,
                                  double durability) {

    private static final double BASELINE_INTENSITY = 1.0;

    public static AttributesResponse from(Biomechanics biomechanics) {
        return new AttributesResponse(biomechanics.getCardiovascularCapacity(), biomechanics.getStrength(),
                biomechanics.getSpeed(), biomechanics.getMaxMovementSpeed(), biomechanics.getStaminaPool(),
                biomechanics.getFatigueRate(BASELINE_INTENSITY), biomechanics.getDurability());
    }
}
