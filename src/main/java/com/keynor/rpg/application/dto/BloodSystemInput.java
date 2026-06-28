package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BloodSystem;

public record BloodSystemInput(double oxygenCarryingCapacity) {

    public BloodSystem toDomain() {
        return new BloodSystem(oxygenCarryingCapacity);
    }
}
