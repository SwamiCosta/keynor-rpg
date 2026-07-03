package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.NeuralSystem;

public record NeuralSystemInput(int neuralDrive, int neuromuscularEfficiency, int cerebralCapacity,
                                 int synapsisQuality, int hippocampus, int thalamus, int hypothalamus,
                                 int amygdalaAndCingulum, int immunity, int agility, int precision) {

    public NeuralSystem toDomain() {
        return new NeuralSystem(neuralDrive, neuromuscularEfficiency, cerebralCapacity, synapsisQuality,
                hippocampus, thalamus, hypothalamus, amygdalaAndCingulum, immunity, agility, precision);
    }
}
