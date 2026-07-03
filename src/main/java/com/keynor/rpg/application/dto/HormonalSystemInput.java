package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.HormonalSystem;

public record HormonalSystemInput(int thyroid, int adrenalGlands) {

    public HormonalSystem toDomain() {
        return new HormonalSystem(thyroid, adrenalGlands);
    }
}
