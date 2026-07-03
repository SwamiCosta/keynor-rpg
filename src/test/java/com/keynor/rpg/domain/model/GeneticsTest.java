package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeneticsTest {

    @Test
    void defaults_returnsMidRangeSomatotypeAndNeutralHeightLimbRatio() {
        Genetics genetics = Genetics.defaults();

        assertThat(genetics.getEndomorphy()).isEqualTo(5);
        assertThat(genetics.getMesomorphy()).isEqualTo(5);
        assertThat(genetics.getEctomorphy()).isEqualTo(5);
        assertThat(genetics.getHeight()).isEqualTo(7);
        assertThat(genetics.getLimbRatio()).isEqualTo(3);
    }

    @Test
    void constructor_storesEachAxisIndependently() {
        Genetics genetics = new Genetics(8, 2, 6, 12, 4);

        assertThat(genetics.getEndomorphy()).isEqualTo(8);
        assertThat(genetics.getMesomorphy()).isEqualTo(2);
        assertThat(genetics.getEctomorphy()).isEqualTo(6);
        assertThat(genetics.getHeight()).isEqualTo(12);
        assertThat(genetics.getLimbRatio()).isEqualTo(4);
    }
}
