package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PulmonarySystemTest {

    @Test
    void defaults_returnsMidRangePulmonaryCapacity() {
        assertThat(PulmonarySystem.defaults().getPulmonaryCapacity()).isEqualTo(5);
    }

    @Test
    void setPulmonaryCapacity_mutatesStateToModelTrainingProgress() {
        PulmonarySystem pulmonarySystem = PulmonarySystem.defaults();

        pulmonarySystem.setPulmonaryCapacity(6);

        assertThat(pulmonarySystem.getPulmonaryCapacity()).isEqualTo(6);
    }
}
