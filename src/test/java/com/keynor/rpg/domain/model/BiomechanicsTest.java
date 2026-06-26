package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BiomechanicsTest {

    @Test
    void humanDefaults_wiresUpGeneticAndTrainableLayersWithFullPointBudgets() {
        Biomechanics biomechanics = Biomechanics.humanDefaults();

        assertThat(biomechanics.getGenetics()).isNotNull();
        assertThat(biomechanics.getBloodSystem()).isNotNull();
        assertThat(biomechanics.getBodyComposition()).isNotNull();
        assertThat(biomechanics.getNervousSystem()).isNotNull();
        assertThat(biomechanics.getCardiacSystem()).isNotNull();
        assertThat(biomechanics.getPulmonarySystem()).isNotNull();

        assertThat(biomechanics.getGeneticPoints().remainingPoints()).isEqualTo(20);
        assertThat(biomechanics.getTrainingPoints().remainingPoints()).isEqualTo(20);
    }
}
