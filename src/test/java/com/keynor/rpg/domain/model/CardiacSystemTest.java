package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CardiacSystemTest {

    @Test
    void defaults_returnsMidRangeCardiacOutput() {
        assertThat(CardiacSystem.defaults().getCardiacOutput()).isEqualTo(5);
    }

    @Test
    void defaults_astralOrgansAbsentOnHumanTemplate() {
        CardiacSystem cardiacSystem = CardiacSystem.defaults();

        assertThat(cardiacSystem.getAstralVentriculum()).isZero();
        assertThat(cardiacSystem.getAstralAtrium()).isZero();
    }

    @Test
    void setCardiacOutput_mutatesStateToModelTrainingProgress() {
        CardiacSystem cardiacSystem = CardiacSystem.defaults();

        cardiacSystem.setCardiacOutput(7);

        assertThat(cardiacSystem.getCardiacOutput()).isEqualTo(7);
    }

    @Test
    void setAstralAtrium_mutatesStateForMagicalRaces() {
        CardiacSystem cardiacSystem = CardiacSystem.defaults();

        cardiacSystem.setAstralAtrium(6);

        assertThat(cardiacSystem.getAstralAtrium()).isEqualTo(6);
    }
}
