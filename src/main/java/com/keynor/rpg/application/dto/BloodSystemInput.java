package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BloodSystem;

public record BloodSystemInput(int oxygenCarryingCapacity, int bloodThickness) {

    public BloodSystem toDomain() {
        return new BloodSystem(oxygenCarryingCapacity, bloodThickness);
    }
}
