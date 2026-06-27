package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PulmonarySystem;

public record PulmonarySystemResponse(double pulmonaryCapacity) {

    public static PulmonarySystemResponse from(PulmonarySystem pulmonarySystem) {
        return new PulmonarySystemResponse(pulmonarySystem.getPulmonaryCapacity());
    }
}
