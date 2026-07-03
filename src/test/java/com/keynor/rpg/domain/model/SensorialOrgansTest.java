package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SensorialOrgansTest {

    @Test
    void defaults_returnsMidRangeForAllThreeFields() {
        SensorialOrgans sensorialOrgans = SensorialOrgans.defaults();

        assertThat(sensorialOrgans.getEyesSensitivity()).isEqualTo(5);
        assertThat(sensorialOrgans.getEarsSensitivity()).isEqualTo(5);
        assertThat(sensorialOrgans.getNoseSensitivity()).isEqualTo(5);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        SensorialOrgans sensorialOrgans = SensorialOrgans.defaults();

        sensorialOrgans.setEyesSensitivity(9);
        sensorialOrgans.setEarsSensitivity(1);
        sensorialOrgans.setNoseSensitivity(7);

        assertThat(sensorialOrgans.getEyesSensitivity()).isEqualTo(9);
        assertThat(sensorialOrgans.getEarsSensitivity()).isEqualTo(1);
        assertThat(sensorialOrgans.getNoseSensitivity()).isEqualTo(7);
    }

    @Test
    void constructor_storesEachFieldIndependently() {
        SensorialOrgans sensorialOrgans = new SensorialOrgans(6, 7, 8);

        assertThat(sensorialOrgans.getEyesSensitivity()).isEqualTo(6);
        assertThat(sensorialOrgans.getEarsSensitivity()).isEqualTo(7);
        assertThat(sensorialOrgans.getNoseSensitivity()).isEqualTo(8);
    }
}
