package com.keynor.rpg.domain.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DamageResolverTest {

    private final DamageResolver resolver = new DamageResolver();

    @Test
    void finalDamage_whenRawDamageExceedsProtection_appliesAreaOfEffect() {
        assertThat(resolver.finalDamage(150, 90, 1.2)).isEqualTo((150 - 90) * 1.2);
    }

    @Test
    void finalDamage_whenProtectionAtOrAboveRawDamage_isZero() {
        assertThat(resolver.finalDamage(90, 90, 1.2)).isZero();
        assertThat(resolver.finalDamage(80, 90, 1.2)).isZero();
    }
}
