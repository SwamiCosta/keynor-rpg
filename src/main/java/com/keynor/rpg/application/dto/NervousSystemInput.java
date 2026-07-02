package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.NervousSystem;

public record NervousSystemInput(int neuralDrive, int neuromuscularEfficiency) {

    public NervousSystem toDomain() {
        return new NervousSystem(neuralDrive, neuromuscularEfficiency);
    }
}
