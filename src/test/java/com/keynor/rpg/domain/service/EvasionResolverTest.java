package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.EvasionOutcome;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvasionResolverTest {

    private final EvasionResolver resolver = new EvasionResolver();

    @Test
    void resolve_higherEvasion_evadesTheAttack() {
        assertThat(resolver.resolve(65, 70)).isEqualTo(EvasionOutcome.EVADED);
    }

    @Test
    void resolve_tie_isAGrazingHit() {
        assertThat(resolver.resolve(65, 65)).isEqualTo(EvasionOutcome.GRAZING_HIT);
    }

    @Test
    void resolve_lowerEvasion_isAFullHit() {
        assertThat(resolver.resolve(65, 60)).isEqualTo(EvasionOutcome.FULL_HIT);
    }

    @Test
    void applyToFinalDamage_halvesOnGrazingHit_zeroesOnEvaded_unchangedOnFullHit() {
        assertThat(resolver.applyToFinalDamage(100, EvasionOutcome.EVADED)).isZero();
        assertThat(resolver.applyToFinalDamage(100, EvasionOutcome.GRAZING_HIT)).isEqualTo(50);
        assertThat(resolver.applyToFinalDamage(100, EvasionOutcome.FULL_HIT)).isEqualTo(100);
    }
}
