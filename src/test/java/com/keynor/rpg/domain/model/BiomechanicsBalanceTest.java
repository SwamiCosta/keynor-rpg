package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BiomechanicsBalanceTest {

    @Test
    void defaults_areAllNeutralMultipliers() {
        BiomechanicsBalance balance = BiomechanicsBalance.defaults();

        assertThat(balance.getK1()).isEqualTo(1);
        assertThat(balance.getC()).isEqualTo(1);
        assertThat(balance.getK2()).isEqualTo(1);
        assertThat(balance.getK3()).isEqualTo(1);
        assertThat(balance.getK4()).isEqualTo(1);
        assertThat(balance.getK5()).isEqualTo(1);
        assertThat(balance.getK6()).isEqualTo(1);
        assertThat(balance.getKBmr()).isEqualTo(1);
        assertThat(balance.getKActivityCost()).isEqualTo(1);
        assertThat(balance.getKEfficiency()).isEqualTo(1);
        assertThat(balance.getK7()).isEqualTo(1);
        assertThat(balance.getK8()).isEqualTo(1);
        assertThat(balance.getK9()).isEqualTo(1);
    }

    @Test
    void setters_allowRebalancingEachCoefficientIndependently() {
        BiomechanicsBalance balance = BiomechanicsBalance.defaults();

        balance.setK1(2.5);
        balance.setC(0.5);

        assertThat(balance.getK1()).isEqualTo(2.5);
        assertThat(balance.getC()).isEqualTo(0.5);
        assertThat(balance.getK2()).isEqualTo(1);
    }
}
