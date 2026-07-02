package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.BodySystems;

public record BodySystemsInput(BloodSystemInput bloodSystem, CardiacSystemInput cardiacSystem,
                                PulmonarySystemInput pulmonarySystem, NeuralSystemInput neuralSystem,
                                HormonalSystemInput hormonalSystem, DigestiveSystemInput digestiveSystem) {

    public BodySystems toDomain() {
        return new BodySystems(bloodSystem.toDomain(), cardiacSystem.toDomain(), pulmonarySystem.toDomain(),
                neuralSystem.toDomain(), hormonalSystem.toDomain(), digestiveSystem.toDomain());
    }
}
