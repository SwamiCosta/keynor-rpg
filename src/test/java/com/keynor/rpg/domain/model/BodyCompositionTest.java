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
        assertThat(composition.getBoneDensity()).isEqualTo(5);
        assertThat(composition.getTendonsAndLigaments()).isEqualTo(5);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        BodyComposition composition = BodyComposition.defaults();

        composition.setMuscleMass(9);
        composition.setBodyFat(6);
        composition.setDominantFiberType(7);
        composition.setMuscleDistribution(7);
        composition.setFlexibility(8);
        composition.setBoneDensity(8);
        composition.setTendonsAndLigaments(2);

        assertThat(composition.getMuscleMass()).isEqualTo(9);
        assertThat(composition.getBodyFat()).isEqualTo(6);
        assertThat(composition.getDominantFiberType()).isEqualTo(7);
        assertThat(composition.getMuscleDistribution()).isEqualTo(7);
        assertThat(composition.getFlexibility()).isEqualTo(8);
        assertThat(composition.getBoneDensity()).isEqualTo(8);
        assertThat(composition.getTendonsAndLigaments()).isEqualTo(2);
    }

    @Test
    void constructor_storesEachFieldIndependently() {
        BodyComposition composition = new BodyComposition(6, 9, 7, 6, 4, 8, 3);

        assertThat(composition.getBodyFat()).isEqualTo(6);
        assertThat(composition.getMuscleMass()).isEqualTo(9);
        assertThat(composition.getDominantFiberType()).isEqualTo(7);
        assertThat(composition.getMuscleDistribution()).isEqualTo(6);
        assertThat(composition.getFlexibility()).isEqualTo(4);
        assertThat(composition.getBoneDensity()).isEqualTo(8);
        assertThat(composition.getTendonsAndLigaments()).isEqualTo(3);
    }
}
