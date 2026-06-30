package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodySystems;

public record BodySystemsInput(BloodSystemInput bloodSystem, CardiacSystemInput cardiacSystem,
                                PulmonarySystemInput pulmonarySystem, NervousSystemInput nervousSystem) {

    public BodySystems toDomain() {
        return new BodySystems(bloodSystem.toDomain(), cardiacSystem.toDomain(),
                pulmonarySystem.toDomain(), nervousSystem.toDomain());
    }
}
