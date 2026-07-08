package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WeaponTest {

    @Test
    void getNature_isAlwaysTrained() {
        for (Weapon weapon : Weapon.values()) {
            assertThat(weapon.getNature()).isEqualTo(InputNature.TRAINED);
        }
    }

    @Test
    void hasThirteenWeapons() {
        assertThat(Weapon.values()).hasSize(13);
    }
}
