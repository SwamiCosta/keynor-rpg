package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BloodSystem;

public record BloodSystemResponse(int oxygenCarryingCapacity, int bloodThickness) {

    public static BloodSystemResponse from(BloodSystem bloodSystem) {
        return new BloodSystemResponse(bloodSystem.getOxygenCarryingCapacity(), bloodSystem.getBloodThickness());
    }
}
