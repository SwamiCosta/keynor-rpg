package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodySystems;

public record BodySystemsResponse(BloodSystemResponse bloodSystem, CardiacSystemResponse cardiacSystem,
                                   PulmonarySystemResponse pulmonarySystem, NeuralSystemResponse neuralSystem,
                                   HormonalGlandularSystemResponse hormonalGlandularSystem,
                                   DigestiveSystemResponse digestiveSystem) {

    public static BodySystemsResponse from(BodySystems bodySystems) {
        return new BodySystemsResponse(
                BloodSystemResponse.from(bodySystems.getBloodSystem()),
                CardiacSystemResponse.from(bodySystems.getCardiacSystem()),
                PulmonarySystemResponse.from(bodySystems.getPulmonarySystem()),
                NeuralSystemResponse.from(bodySystems.getNeuralSystem()),
                HormonalGlandularSystemResponse.from(bodySystems.getHormonalGlandularSystem()),
                DigestiveSystemResponse.from(bodySystems.getDigestiveSystem()));
    }
}
