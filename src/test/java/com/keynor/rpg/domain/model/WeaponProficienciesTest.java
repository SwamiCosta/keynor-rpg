package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WeaponProficienciesTest {

    @Test
    void defaults_everyWeaponAtZero() {
        WeaponProficiencies proficiencies = WeaponProficiencies.defaults();

        for (Weapon weapon : Weapon.values()) {
            assertThat(proficiencies.getLevel(weapon)).isZero();
        }
    }

    @Test
    void constructor_seedsFromGivenMap() {
        WeaponProficiencies proficiencies = new WeaponProficiencies(Map.of(Weapon.DAGGERS, 2));

        assertThat(proficiencies.getLevel(Weapon.DAGGERS)).isEqualTo(2);
        assertThat(proficiencies.getLevel(Weapon.BOWS)).isZero();
    }

    @Test
    void setLevel_withinRange_succeeds() {
        WeaponProficiencies proficiencies = WeaponProficiencies.defaults();

        proficiencies.setLevel(Weapon.LONG_SWORDS, Weapon.MAX_LEVEL);

        assertThat(proficiencies.getLevel(Weapon.LONG_SWORDS)).isEqualTo(Weapon.MAX_LEVEL);
    }

    @Test
    void setLevel_beyondMaxLevel_throws() {
        WeaponProficiencies proficiencies = WeaponProficiencies.defaults();

        assertThatThrownBy(() -> proficiencies.setLevel(Weapon.LONG_SWORDS, Weapon.MAX_LEVEL + 1))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void setLevel_hasNoSharedPointBudget_everyWeaponCanReachMaxIndependently() {
        WeaponProficiencies proficiencies = WeaponProficiencies.defaults();

        for (Weapon weapon : Weapon.values()) {
            proficiencies.setLevel(weapon, Weapon.MAX_LEVEL);
        }

        for (Weapon weapon : Weapon.values()) {
            assertThat(proficiencies.getLevel(weapon)).isEqualTo(Weapon.MAX_LEVEL);
        }
    }

    @Test
    void getLevels_returnsImmutableCopy() {
        WeaponProficiencies proficiencies = WeaponProficiencies.defaults();

        assertThatThrownBy(() -> proficiencies.getLevels().put(Weapon.DAGGERS, 1))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
