package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HormonalSystemTest {

    @Test
    void defaults_returnsMidRangeForBothFields() {
        HormonalSystem hormonalSystem = HormonalSystem.defaults();

        assertThat(hormonalSystem.getThyroid()).isEqualTo(5);
        assertThat(hormonalSystem.getAdrenalGlands()).isEqualTo(5);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        HormonalSystem hormonalSystem = HormonalSystem.defaults();

        hormonalSystem.setThyroid(8);
        hormonalSystem.setAdrenalGlands(2);

        assertThat(hormonalSystem.getThyroid()).isEqualTo(8);
        assertThat(hormonalSystem.getAdrenalGlands()).isEqualTo(2);
    }
}
