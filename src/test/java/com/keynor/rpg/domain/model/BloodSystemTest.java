package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BloodSystemTest {

    @Test
    void defaults_returnsMidRangeOxygenCarryingCapacity() {
        assertThat(BloodSystem.defaults().getOxygenCarryingCapacity()).isEqualTo(5);
    }

    @Test
    void constructor_storesOxygenCarryingCapacity() {
        BloodSystem bloodSystem = new BloodSystem(8);

        assertThat(bloodSystem.getOxygenCarryingCapacity()).isEqualTo(8);
    }
}
