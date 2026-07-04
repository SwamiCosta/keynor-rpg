package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HormonalGlandularSystemTest {

    @Test
    void defaults_returnsMidRangeForTheThreeTrainableFieldsAndZeroForTheArcaneOrgan() {
        HormonalGlandularSystem hormonalGlandularSystem = HormonalGlandularSystem.defaults();

        assertThat(hormonalGlandularSystem.getThyroid()).isEqualTo(5);
        assertThat(hormonalGlandularSystem.getAdrenalGlands()).isEqualTo(5);
        assertThat(hormonalGlandularSystem.getPredominantMorphicHormone()).isEqualTo(5);
        assertThat(hormonalGlandularSystem.getSubtleEpiphysealGland()).isEqualTo(0);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        HormonalGlandularSystem hormonalGlandularSystem = HormonalGlandularSystem.defaults();

        hormonalGlandularSystem.setThyroid(8);
        hormonalGlandularSystem.setAdrenalGlands(2);
        hormonalGlandularSystem.setPredominantMorphicHormone(1);
        hormonalGlandularSystem.setSubtleEpiphysealGland(6);

        assertThat(hormonalGlandularSystem.getThyroid()).isEqualTo(8);
        assertThat(hormonalGlandularSystem.getAdrenalGlands()).isEqualTo(2);
        assertThat(hormonalGlandularSystem.getPredominantMorphicHormone()).isEqualTo(1);
        assertThat(hormonalGlandularSystem.getSubtleEpiphysealGland()).isEqualTo(6);
    }

    @Test
    void constructor_storesEachFieldIndependently() {
        HormonalGlandularSystem hormonalGlandularSystem = new HormonalGlandularSystem(6, 7, 9, 3);

        assertThat(hormonalGlandularSystem.getThyroid()).isEqualTo(6);
        assertThat(hormonalGlandularSystem.getAdrenalGlands()).isEqualTo(7);
        assertThat(hormonalGlandularSystem.getPredominantMorphicHormone()).isEqualTo(9);
        assertThat(hormonalGlandularSystem.getSubtleEpiphysealGland()).isEqualTo(3);
    }
}
