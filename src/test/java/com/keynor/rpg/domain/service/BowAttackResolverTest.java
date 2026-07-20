package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.BowProfile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BowAttackResolverTest {

    private final BowAttackResolver resolver = new BowAttackResolver();

    @Test
    void canDrawBow_failsAtOrBelowMinimumPull() {
        BowProfile shortBow = BowProfile.SHORT_BOW;

        assertThat(resolver.canDrawBow(50, shortBow)).isFalse();
        assertThat(resolver.canDrawBow(51, shortBow)).isTrue();
    }

    @Test
    void resolveHit_missesAtOrBelowThreshold_hitsAbove() {
        BowProfile shortBow = BowProfile.SHORT_BOW;

        assertThat(resolver.resolveHit(40, 0, shortBow)).isFalse();
        assertThat(resolver.resolveHit(41, 0, shortBow)).isTrue();
    }

    @Test
    void effectivePullStrength_cappedAtMaximumPull() {
        BowProfile shortBow = BowProfile.SHORT_BOW;

        assertThat(resolver.effectivePullStrength(70, shortBow)).isEqualTo(62);
        assertThat(resolver.effectivePullStrength(55, shortBow)).isEqualTo(55);
    }

    @Test
    void resolveRawDamage_atZeroDistanceWithMaxedPull_matchesDesignDocMaximumDamage() {
        double db = resolver.resolveRawDamage(70, 0, BowProfile.SHORT_BOW);

        assertThat(db).isEqualTo(217.0);
    }
}
