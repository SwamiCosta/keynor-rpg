package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BodyCompositionTest {

    @Test
    void defaults_returnsAverageHumanComposition() {
        BodyComposition composition = BodyComposition.defaults();

        assertThat(composition.getBodyFat()).isEqualTo(14);
        assertThat(composition.getMuscleMass()).isEqualTo(30);
        assertThat(composition.getDominantFiberType()).isEqualTo(0.0);
        assertThat(composition.getNeuromuscularEfficiency()).isEqualTo(0.5);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        BodyComposition composition = BodyComposition.defaults();

        composition.setMuscleMass(35);
        composition.setBodyFat(10);
        composition.setDominantFiberType(0.4);
        composition.setNeuromuscularEfficiency(0.7);

        assertThat(composition.getMuscleMass()).isEqualTo(35);
        assertThat(composition.getBodyFat()).isEqualTo(10);
        assertThat(composition.getDominantFiberType()).isEqualTo(0.4);
        assertThat(composition.getNeuromuscularEfficiency()).isEqualTo(0.7);
    }
}
