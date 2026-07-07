package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LaboursTest {

    private final PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

    @Test
    void defaults_everyJobAtZero() {
        Labours labours = Labours.defaults();

        for (Job job : Job.values()) {
            assertThat(labours.getLevel(job)).isZero();
        }
    }

    @Test
    void setLevel_upToBasePoints_succeeds() {
        Labours labours = Labours.defaults();

        labours.setLevel(Job.MASONRY, 2, character);

        assertThat(labours.getLevel(Job.MASONRY)).isEqualTo(2);
    }

    @Test
    void setLevel_beyondEffectivePoints_throws() {
        Labours labours = Labours.defaults();
        labours.setLevel(Job.MASONRY, 2, character);

        assertThatThrownBy(() -> labours.setLevel(Job.COOKING, 1, character))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void constructor_seedsFromGivenMap() {
        Labours labours = new Labours(Map.of(Job.COOKING, 1));

        assertThat(labours.getLevel(Job.COOKING)).isEqualTo(1);
        assertThat(labours.getLevel(Job.MASONRY)).isZero();
    }
}
