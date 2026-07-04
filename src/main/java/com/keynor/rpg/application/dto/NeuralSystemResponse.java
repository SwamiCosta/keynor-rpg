package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.NeuralSystem;

public record NeuralSystemResponse(int neuralDrive, int neuromuscularEfficiency, int cerebralCapacity,
                                    int synapsisQuality, int hippocampus, int thalamus, int hypothalamus,
                                    int amygdalaAndCingulum, int immunity, int agility, int precision,
                                    int noeticPlexus) {

    public static NeuralSystemResponse from(NeuralSystem neuralSystem) {
        return new NeuralSystemResponse(neuralSystem.getNeuralDrive(), neuralSystem.getNeuromuscularEfficiency(),
                neuralSystem.getCerebralCapacity(), neuralSystem.getSynapsisQuality(), neuralSystem.getHippocampus(),
                neuralSystem.getThalamus(), neuralSystem.getHypothalamus(), neuralSystem.getAmygdalaAndCingulum(),
                neuralSystem.getImmunity(), neuralSystem.getAgility(), neuralSystem.getPrecision(),
                neuralSystem.getNoeticPlexus());
    }
}
