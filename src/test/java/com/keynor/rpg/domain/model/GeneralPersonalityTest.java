package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeneralPersonalityTest {

    @Test
    void defaults_returnsMidRangeForBothFields() {
        GeneralPersonality generalPersonality = GeneralPersonality.defaults();

        assertThat(generalPersonality.getVanity()).isEqualTo(5);
        assertThat(generalPersonality.getFocus()).isEqualTo(5);
    }

    @Test
    void constructor_storesEachFieldIndependently() {
        GeneralPersonality generalPersonality = new GeneralPersonality(9, 1);

        assertThat(generalPersonality.getVanity()).isEqualTo(9);
        assertThat(generalPersonality.getFocus()).isEqualTo(1);
    }

    @Test
    void setters_mutateStateIndependently() {
        GeneralPersonality generalPersonality = GeneralPersonality.defaults();

        generalPersonality.setVanity(8);
        generalPersonality.setFocus(2);

        assertThat(generalPersonality.getVanity()).isEqualTo(8);
        assertThat(generalPersonality.getFocus()).isEqualTo(2);
    }
}
