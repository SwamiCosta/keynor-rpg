package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeneticsTest {

    @Test
    void defaults_returnsMidRangeSomatotypeAndAverageHumanMeasurements() {
        Genetics genetics = Genetics.defaults();

        assertThat(genetics.getEndomorphy()).isEqualTo(5);
        assertThat(genetics.getMesomorphy()).isEqualTo(5);
        assertThat(genetics.getEctomorphy()).isEqualTo(5);
        assertThat(genetics.getHeight()).isEqualTo(170);
        assertThat(genetics.getLimbRatio()).isEqualTo(1.0);
        assertThat(genetics.getBoneDensity()).isEqualTo(5);
    }

    @Test
    void constructor_storesEachAxisIndependently() {
        Genetics genetics = new Genetics(8, 2, 6, 182, 1.1, 7);

        assertThat(genetics.getEndomorphy()).isEqualTo(8);
        assertThat(genetics.getMesomorphy()).isEqualTo(2);
        assertThat(genetics.getEctomorphy()).isEqualTo(6);
        assertThat(genetics.getHeight()).isEqualTo(182);
        assertThat(genetics.getLimbRatio()).isEqualTo(1.1);
        assertThat(genetics.getBoneDensity()).isEqualTo(7);
    }
}
