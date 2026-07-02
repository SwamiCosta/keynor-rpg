package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PulmonarySystem;

public record PulmonarySystemInput(int pulmonaryCapacity) {

    public PulmonarySystem toDomain() {
        return new PulmonarySystem(pulmonaryCapacity);
    }
}
