package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.HormonalSystem;

public record HormonalSystemResponse(int thyroid, int adrenalGlands) {

    public static HormonalSystemResponse from(HormonalSystem hormonalSystem) {
        return new HormonalSystemResponse(hormonalSystem.getThyroid(), hormonalSystem.getAdrenalGlands());
    }
}
