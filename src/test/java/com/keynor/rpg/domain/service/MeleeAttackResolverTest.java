package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.MeleeAttackProfile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MeleeAttackResolverTest {

    private final MeleeAttackResolver resolver = new MeleeAttackResolver();

    @Test
    void resolveHit_regularWeapon_missesAtOrBelow40_hitsAbove40() {
        assertThat(resolver.resolveHit(40, MeleeAttackProfile.SHORT_SWORD_CHOP)).isFalse();
        assertThat(resolver.resolveHit(41, MeleeAttackProfile.SHORT_SWORD_CHOP)).isTrue();
    }

    @Test
    void resolveHit_longHaftedWeapon_usesThresholdOf55() {
        assertThat(resolver.resolveHit(50, MeleeAttackProfile.LONG_SPEAR_THRUST)).isFalse();
        assertThat(resolver.resolveHit(56, MeleeAttackProfile.LONG_SPEAR_THRUST)).isTrue();
    }

    @Test
    void resolveRawDamage_appliesForceMultiplierAndProficiencyFactor() {
        double db = resolver.resolveRawDamage(60, 60, MeleeAttackProfile.SHORT_SWORD_CHOP);

        assertThat(db).isEqualTo(60 * 1.9 + 60 * 1.0);
    }
}
