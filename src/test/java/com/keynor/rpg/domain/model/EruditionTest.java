package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EruditionTest {

    private final PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

    @Test
    void defaults_everyKnowledgeAtZero() {
        Erudition erudition = Erudition.defaults();

        for (Knowledge knowledge : Knowledge.values()) {
            assertThat(erudition.getLevel(knowledge)).isZero();
        }
        assertThat(erudition.getSpentPoints()).isZero();
    }

    @Test
    void setLevel_twoPointsInOneKnowledge_succeeds() {
        Erudition erudition = Erudition.defaults();

        erudition.setLevel(Knowledge.ECOLOGY, 2, character);

        assertThat(erudition.getLevel(Knowledge.ECOLOGY)).isEqualTo(2);
        assertThat(erudition.getSpentPoints()).isEqualTo(2);
    }

    @Test
    void setLevel_onePointEachInTwoKnowledges_succeeds() {
        Erudition erudition = Erudition.defaults();

        erudition.setLevel(Knowledge.ECOLOGY, 1, character);
        erudition.setLevel(Knowledge.BIOLOGY, 1, character);

        assertThat(erudition.getLevel(Knowledge.ECOLOGY)).isEqualTo(1);
        assertThat(erudition.getLevel(Knowledge.BIOLOGY)).isEqualTo(1);
        assertThat(erudition.getSpentPoints()).isEqualTo(2);
    }

    @Test
    void setLevel_beyondEffectivePoints_throws() {
        Erudition erudition = Erudition.defaults();
        erudition.setLevel(Knowledge.ECOLOGY, 2, character);

        assertThatThrownBy(() -> erudition.setLevel(Knowledge.BIOLOGY, 1, character))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void canSetLevel_beyondMaxLevel_isFalse() {
        Erudition erudition = Erudition.defaults();

        assertThat(erudition.canSetLevel(Knowledge.ECOLOGY, Knowledge.MAX_LEVEL + 1, character)).isFalse();
        assertThat(erudition.canSetLevel(Knowledge.ECOLOGY, Knowledge.MAX_LEVEL, character)).isFalse(); // exceeds 2-point budget
        assertThat(erudition.canSetLevel(Knowledge.ECOLOGY, 2, character)).isTrue();
    }

    @Test
    void reAssigningTheSameKnowledge_doesNotDoubleCountItsOwnPreviousLevel() {
        Erudition erudition = Erudition.defaults();
        erudition.setLevel(Knowledge.ECOLOGY, 2, character);

        erudition.setLevel(Knowledge.ECOLOGY, 1, character);

        assertThat(erudition.getLevel(Knowledge.ECOLOGY)).isEqualTo(1);
        assertThat(erudition.getSpentPoints()).isEqualTo(1);
    }

    @Test
    void getEffectivePoints_defaultsToBasePoints() {
        Erudition erudition = Erudition.defaults();

        assertThat(erudition.getEffectivePoints(character)).isEqualTo(Erudition.BASE_POINTS);
    }

    @Test
    void constructor_seedsFromGivenMap() {
        Erudition erudition = new Erudition(Map.of(Knowledge.ECOLOGY, 2));

        assertThat(erudition.getLevel(Knowledge.ECOLOGY)).isEqualTo(2);
        assertThat(erudition.getLevel(Knowledge.BIOLOGY)).isZero();
    }
}
