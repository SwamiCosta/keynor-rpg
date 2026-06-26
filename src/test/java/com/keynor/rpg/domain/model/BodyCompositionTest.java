package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BodyCompositionTest {

    @Test
    void defaults_returnsAverageHumanComposition() {
        BodyComposition composition = BodyComposition.defaults();

        assertThat(composition.getTotalMass()).isEqualTo(70);
        assertThat(composition.getBodyFatPercentage()).isEqualTo(0.20);
        assertThat(composition.getMuscleMass()).isEqualTo(30);
        assertThat(composition.getDominantFiberType()).isEqualTo(0.0);
        assertThat(composition.getNeuromuscularEfficiency()).isEqualTo(0.5);
    }

    @Test
    void getFatMass_isDerivedFromTotalMassTimesBodyFatPercentage() {
        BodyComposition composition = new BodyComposition(80, 0.25, 32, 0.3, 0.6);

        assertThat(composition.getFatMass()).isEqualTo(20.0);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        BodyComposition composition = BodyComposition.defaults();

        composition.setMuscleMass(35);
        composition.setBodyFatPercentage(0.15);
        composition.setDominantFiberType(0.4);
        composition.setNeuromuscularEfficiency(0.7);
        composition.setTotalMass(75);

        assertThat(composition.getMuscleMass()).isEqualTo(35);
        assertThat(composition.getBodyFatPercentage()).isEqualTo(0.15);
        assertThat(composition.getDominantFiberType()).isEqualTo(0.4);
        assertThat(composition.getNeuromuscularEfficiency()).isEqualTo(0.7);
        assertThat(composition.getTotalMass()).isEqualTo(75);
    }
}
