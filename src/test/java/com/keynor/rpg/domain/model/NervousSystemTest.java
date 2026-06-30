package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NervousSystemTest {

    @Test
    void defaults_returnsMidRangeNeuralDriveAndHalfNeuromuscularEfficiency() {
        NervousSystem nervousSystem = NervousSystem.defaults();

        assertThat(nervousSystem.getNeuralDrive()).isEqualTo(5);
        assertThat(nervousSystem.getNeuromuscularEfficiency()).isEqualTo(0.5);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        NervousSystem nervousSystem = NervousSystem.defaults();

        nervousSystem.setNeuralDrive(8);
        nervousSystem.setNeuromuscularEfficiency(0.9);

        assertThat(nervousSystem.getNeuralDrive()).isEqualTo(8);
        assertThat(nervousSystem.getNeuromuscularEfficiency()).isEqualTo(0.9);
    }
}
