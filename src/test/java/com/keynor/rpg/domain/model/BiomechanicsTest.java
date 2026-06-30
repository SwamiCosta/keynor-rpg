package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BiomechanicsTest {

    @Test
    void defaults_wiresUpGeneticsAndBodyCompositionWithHumanDefaults() {
        Biomechanics biomechanics = Biomechanics.defaults();

        assertThat(biomechanics.getGenetics()).isNotNull();
        assertThat(biomechanics.getBodyComposition()).isNotNull();
        assertThat(biomechanics.getGenetics().getHeight()).isEqualTo(170);
        assertThat(biomechanics.getBodyComposition().getMuscleMass()).isEqualTo(30);
    }

    @Test
    void constructor_storesGeneticsAndBodyCompositionAsProvided() {
        Genetics genetics = new Genetics(7, 3, 5, 180, 1.05, 6);
        BodyComposition composition = new BodyComposition(12, 40, 0.3, 6.0, 4.0);
        Biomechanics biomechanics = new Biomechanics(genetics, composition);

        assertThat(biomechanics.getGenetics()).isSameAs(genetics);
        assertThat(biomechanics.getBodyComposition()).isSameAs(composition);
    }
}
