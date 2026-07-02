package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BloodSystem;

public record BloodSystemResponse(int oxygenCarryingCapacity) {

    public static BloodSystemResponse from(BloodSystem bloodSystem) {
        return new BloodSystemResponse(bloodSystem.getOxygenCarryingCapacity());
    }
}
