package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpatialIntelligenceTest {

    @Test
    void defaults_returnsBalancedMidRangeAxes() {
        SpatialIntelligence si = SpatialIntelligence.defaults();

        assertThat(si.getPerception()).isEqualTo(5);
        assertThat(si.getAgility()).isEqualTo(5);
        assertThat(si.getPrecision()).isEqualTo(5);
    }

    @Test
    void constructor_storesEachAxisAsProvided() {
        SpatialIntelligence si = new SpatialIntelligence(7, 8, 6);

        assertThat(si.getPerception()).isEqualTo(7);
        assertThat(si.getAgility()).isEqualTo(8);
        assertThat(si.getPrecision()).isEqualTo(6);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        SpatialIntelligence si = SpatialIntelligence.defaults();

        si.setPerception(9);
        si.setAgility(7);
        si.setPrecision(8);

        assertThat(si.getPerception()).isEqualTo(9);
        assertThat(si.getAgility()).isEqualTo(7);
        assertThat(si.getPrecision()).isEqualTo(8);
    }
}
