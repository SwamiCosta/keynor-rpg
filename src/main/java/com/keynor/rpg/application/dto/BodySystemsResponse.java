package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodySystems;

public record BodySystemsResponse(BloodSystemResponse bloodSystem, CardiacSystemResponse cardiacSystem,
                                   PulmonarySystemResponse pulmonarySystem, NervousSystemResponse nervousSystem) {

    public static BodySystemsResponse from(BodySystems bodySystems) {
        return new BodySystemsResponse(
                BloodSystemResponse.from(bodySystems.getBloodSystem()),
                CardiacSystemResponse.from(bodySystems.getCardiacSystem()),
                PulmonarySystemResponse.from(bodySystems.getPulmonarySystem()),
                NervousSystemResponse.from(bodySystems.getNervousSystem()));
    }
}
