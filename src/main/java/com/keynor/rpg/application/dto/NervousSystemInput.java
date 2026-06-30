package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.NervousSystem;

public record NervousSystemInput(double neuralDrive, double neuromuscularEfficiency) {

    public NervousSystem toDomain() {
        return new NervousSystem(neuralDrive, neuromuscularEfficiency);
    }
}
