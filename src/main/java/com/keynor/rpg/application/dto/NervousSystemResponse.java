package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.NervousSystem;

public record NervousSystemResponse(int neuralDrive, int neuromuscularEfficiency) {

    public static NervousSystemResponse from(NervousSystem nervousSystem) {
        return new NervousSystemResponse(nervousSystem.getNeuralDrive(),
                nervousSystem.getNeuromuscularEfficiency());
    }
}
