package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BodyCompositionTest {

    @Test
    void defaults_returnsNeutralBodyComposition() {
        BodyComposition composition = BodyComposition.defaults();

        assertThat(composition.getBodyFat()).isEqualTo(3);
        assertThat(composition.getMuscleMass()).isEqualTo(5);
        assertThat(composition.getDominantFiberType()).isEqualTo(5);
        assertThat(composition.getMuscleDistribution()).isEqualTo(5);
        assertThat(composition.getFlexibility()).isEqualTo(5);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        BodyComposition composition = BodyComposition.defaults();

        composition.setMuscleMass(9);
        composition.setBodyFat(6);
        composition.setDominantFiberType(7);
        composition.setMuscleDistribution(7);
        composition.setFlexibility(8);

        assertThat(composition.getMuscleMass()).isEqualTo(9);
        assertThat(composition.getBodyFat()).isEqualTo(6);
        assertThat(composition.getDominantFiberType()).isEqualTo(7);
        assertThat(composition.getMuscleDistribution()).isEqualTo(7);
        assertThat(composition.getFlexibility()).isEqualTo(8);
    }
}
