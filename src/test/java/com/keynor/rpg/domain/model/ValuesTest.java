package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValuesTest {

    @Test
    void defaults_everyValueIsOne() {
        Values values = Values.defaults();

        assertThat(values.getEgo()).isEqualTo(1);
        assertThat(values.getLoyalty()).isEqualTo(1);
        assertThat(values.getOrganization()).isEqualTo(1);
        assertThat(values.getFreedom()).isEqualTo(1);
        assertThat(values.getSociety()).isEqualTo(1);
        assertThat(values.getDivinity()).isEqualTo(1);
        assertThat(values.getTruth()).isEqualTo(1);
        assertThat(values.getKnowledge()).isEqualTo(1);
        assertThat(values.getNature()).isEqualTo(1);
        assertThat(values.getMorality()).isEqualTo(1);
        assertThat(values.getTradition()).isEqualTo(1);
        assertThat(values.getJustice()).isEqualTo(1);
        assertThat(values.getProgress()).isEqualTo(1);
        assertThat(values.getPeace()).isEqualTo(1);
    }

    @Test
    void setters_updateIndependently() {
        Values values = Values.defaults();

        values.setKnowledge(5);
        values.setTruth(3);

        assertThat(values.getKnowledge()).isEqualTo(5);
        assertThat(values.getTruth()).isEqualTo(3);
        assertThat(values.getLoyalty()).isEqualTo(1);
    }
}
