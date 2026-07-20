package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.ThrownWeaponProfile;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ThrownAttackResolverTest {

    private final ThrownAttackResolver resolver = new ThrownAttackResolver();

    @Test
    void resolveHit_missesAtOrBelowThreshold_hitsAbove() {
        ThrownWeaponProfile knife = ThrownWeaponProfile.THROWING_KNIFE;
        double threshold = 45 + 5 + knife.getWeaponHandling();

        assertThat(resolver.resolveHit(threshold, 5, knife)).isFalse();
        assertThat(resolver.resolveHit(threshold + 1, 5, knife)).isTrue();
    }

    @Test
    void isWithinRange_falseOncePastMaximumRange() {
        ThrownWeaponProfile rock = ThrownWeaponProfile.THROWN_ROCK;

        assertThat(resolver.isWithinRange(60, 60, 18, rock)).isTrue();
        assertThat(resolver.isWithinRange(60, 60, 19, rock)).isFalse();
    }

    @Test
    void resolveRawDamage_flooredAtZeroPastMaximumRange() {
        ThrownWeaponProfile rock = ThrownWeaponProfile.THROWN_ROCK;

        assertThat(resolver.resolveRawDamage(60, 60, 100, rock)).isZero();
    }
}
