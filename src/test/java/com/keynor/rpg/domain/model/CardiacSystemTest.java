package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CardiacSystemTest {

    @Test
    void defaults_returnsMidRangeCardiacOutput() {
        assertThat(CardiacSystem.defaults().getCardiacOutput()).isEqualTo(5);
    }

    @Test
    void setCardiacOutput_mutatesStateToModelTrainingProgress() {
        CardiacSystem cardiacSystem = CardiacSystem.defaults();

        cardiacSystem.setCardiacOutput(7);

        assertThat(cardiacSystem.getCardiacOutput()).isEqualTo(7);
    }
}
