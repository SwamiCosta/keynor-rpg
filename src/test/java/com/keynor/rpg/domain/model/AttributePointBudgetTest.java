package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AttributePointBudgetTest {

    @Test
    void newBudget_hasFullRemainingPointsAndNoneSpent() {
        AttributePointBudget budget = new AttributePointBudget(20);

        assertThat(budget.getTotalPoints()).isEqualTo(20);
        assertThat(budget.getSpentPoints()).isEqualTo(0);
        assertThat(budget.remainingPoints()).isEqualTo(20);
    }

    @Test
    void spend_reducesRemainingPointsAndAccumulatesSpent() {
        AttributePointBudget budget = new AttributePointBudget(20);

        budget.spend(5);
        budget.spend(3);

        assertThat(budget.getSpentPoints()).isEqualTo(8);
        assertThat(budget.remainingPoints()).isEqualTo(12);
    }

    @Test
    void spend_exactlyTheRemainingBalance_isAllowed() {
        AttributePointBudget budget = new AttributePointBudget(10);

        budget.spend(10);

        assertThat(budget.remainingPoints()).isEqualTo(0);
    }

    @Test
    void spend_moreThanRemainingBalance_throws() {
        AttributePointBudget budget = new AttributePointBudget(10);
        budget.spend(8);

        assertThatThrownBy(() -> budget.spend(3)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void spend_negativeAmount_throws() {
        AttributePointBudget budget = new AttributePointBudget(10);

        assertThatThrownBy(() -> budget.spend(-1)).isInstanceOf(IllegalArgumentException.class);
    }
}
