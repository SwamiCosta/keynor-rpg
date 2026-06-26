package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NervousSystemTest {

    @Test
    void defaults_returnsMidRangeNeuralDrive() {
        assertThat(NervousSystem.defaults().getNeuralDrive()).isEqualTo(5);
    }

    @Test
    void setNeuralDrive_mutatesStateToModelTrainingProgress() {
        NervousSystem nervousSystem = NervousSystem.defaults();

        nervousSystem.setNeuralDrive(8);

        assertThat(nervousSystem.getNeuralDrive()).isEqualTo(8);
    }
}
