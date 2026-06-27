package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PulmonarySystem;

public record PulmonarySystemInput(double pulmonaryCapacity) {

    public PulmonarySystem toDomain() {
        return new PulmonarySystem(pulmonaryCapacity);
    }
}
