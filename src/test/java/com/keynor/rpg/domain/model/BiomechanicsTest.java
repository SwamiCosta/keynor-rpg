package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BiomechanicsTest {

    @Test
    void defaults_wiresUpGeneticsAndBodyCompositionWithHumanDefaults() {
        Biomechanics biomechanics = Biomechanics.defaults();

        assertThat(biomechanics.getGenetics()).isNotNull();
        assertThat(biomechanics.getBodyComposition()).isNotNull();
        assertThat(biomechanics.getGenetics().getHeight()).isEqualTo(7);
        assertThat(biomechanics.getBodyComposition().getMuscleMass()).isEqualTo(5);
    }

    @Test
    void constructor_storesGeneticsAndBodyCompositionAsProvided() {
        Genetics genetics = new Genetics(7, 3, 5, 10, 4, 6);
        BodyComposition composition = new BodyComposition(5, 8, 7, 6, 4);
        Biomechanics biomechanics = new Biomechanics(genetics, composition);

        assertThat(biomechanics.getGenetics()).isSameAs(genetics);
        assertThat(biomechanics.getBodyComposition()).isSameAs(composition);
    }
}
