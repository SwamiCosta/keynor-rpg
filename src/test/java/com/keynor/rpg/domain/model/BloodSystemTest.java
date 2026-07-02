package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BloodSystemTest {

    @Test
    void defaults_returnsMidRangeOxygenCarryingCapacityAndNeutralBloodThickness() {
        BloodSystem bloodSystem = BloodSystem.defaults();

        assertThat(bloodSystem.getOxygenCarryingCapacity()).isEqualTo(5);
        assertThat(bloodSystem.getBloodThickness()).isEqualTo(3);
    }

    @Test
    void constructor_storesOxygenCarryingCapacityAndBloodThickness() {
        BloodSystem bloodSystem = new BloodSystem(8, 5);

        assertThat(bloodSystem.getOxygenCarryingCapacity()).isEqualTo(8);
        assertThat(bloodSystem.getBloodThickness()).isEqualTo(5);
    }
}
