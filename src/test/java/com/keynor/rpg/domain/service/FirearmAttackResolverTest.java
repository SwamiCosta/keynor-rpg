package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.FirearmProfile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FirearmAttackResolverTest {

    private final FirearmAttackResolver resolver = new FirearmAttackResolver();

    @Test
    void resolveHit_missesAtOrBelowThreshold_hitsAbove() {
        FirearmProfile crossbow = FirearmProfile.HAND_CROSSBOW;

        assertThat(resolver.resolveHit(42, 0, crossbow)).isFalse();
        assertThat(resolver.resolveHit(43, 0, crossbow)).isTrue();
    }

    @Test
    void isWithinRange_falseOncePastMaximumRange() {
        FirearmProfile crossbow = FirearmProfile.HAND_CROSSBOW;

        assertThat(resolver.isWithinRange(220, crossbow)).isTrue();
        assertThat(resolver.isWithinRange(221, crossbow)).isFalse();
    }

    @Test
    void isWithinRange_modernFirearmNeverFallsOff() {
        assertThat(resolver.isWithinRange(100_000, FirearmProfile.MODERN_9MM_PISTOL)).isTrue();
    }

    @Test
    void resolveRawDamage_flooredAtZeroPastMaximumRange() {
        assertThat(resolver.resolveRawDamage(0, FirearmProfile.HAND_CROSSBOW)).isEqualTo(220);
        assertThat(resolver.resolveRawDamage(250, FirearmProfile.HAND_CROSSBOW)).isZero();
    }
}
