package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NeuralSystemTest {

    @Test
    void defaults_returnsMidRangeForAllTenFields() {
        NeuralSystem neuralSystem = NeuralSystem.defaults();

        assertThat(neuralSystem.getNeuralDrive()).isEqualTo(5);
        assertThat(neuralSystem.getNeuromuscularEfficiency()).isEqualTo(5);
        assertThat(neuralSystem.getCerebralCapacity()).isEqualTo(5);
        assertThat(neuralSystem.getSynapsisQuality()).isEqualTo(5);
        assertThat(neuralSystem.getHippocampus()).isEqualTo(5);
        assertThat(neuralSystem.getHypothalamus()).isEqualTo(5);
        assertThat(neuralSystem.getAmygdalaAndCingulum()).isEqualTo(5);
        assertThat(neuralSystem.getImmunity()).isEqualTo(5);
        assertThat(neuralSystem.getAgility()).isEqualTo(5);
        assertThat(neuralSystem.getPrecision()).isEqualTo(5);
    }

    @Test
    void constructor_storesEachFieldIndependently() {
        NeuralSystem neuralSystem = new NeuralSystem(1, 2, 3, 4, 5, 6, 7, 8, 9, 1);

        assertThat(neuralSystem.getNeuralDrive()).isEqualTo(1);
        assertThat(neuralSystem.getNeuromuscularEfficiency()).isEqualTo(2);
        assertThat(neuralSystem.getCerebralCapacity()).isEqualTo(3);
        assertThat(neuralSystem.getSynapsisQuality()).isEqualTo(4);
        assertThat(neuralSystem.getHippocampus()).isEqualTo(5);
        assertThat(neuralSystem.getHypothalamus()).isEqualTo(6);
        assertThat(neuralSystem.getAmygdalaAndCingulum()).isEqualTo(7);
        assertThat(neuralSystem.getImmunity()).isEqualTo(8);
        assertThat(neuralSystem.getAgility()).isEqualTo(9);
        assertThat(neuralSystem.getPrecision()).isEqualTo(1);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        NeuralSystem neuralSystem = NeuralSystem.defaults();

        neuralSystem.setNeuralDrive(8);
        neuralSystem.setCerebralCapacity(9);
        neuralSystem.setHippocampus(7);
        neuralSystem.setAgility(6);
        neuralSystem.setPrecision(4);

        assertThat(neuralSystem.getNeuralDrive()).isEqualTo(8);
        assertThat(neuralSystem.getCerebralCapacity()).isEqualTo(9);
        assertThat(neuralSystem.getHippocampus()).isEqualTo(7);
        assertThat(neuralSystem.getAgility()).isEqualTo(6);
        assertThat(neuralSystem.getPrecision()).isEqualTo(4);
    }
}
