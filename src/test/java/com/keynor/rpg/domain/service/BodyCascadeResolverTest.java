package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.BodyComponent;
import com.keynor.rpg.domain.port.out.RandomSource;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class BodyCascadeResolverTest {

    @Test
    void resistedDamage_subtractsNaturalResistance() {
        BodyComponent skull = BodyComponent.structural("Skull", 20, 5, true, 8);
        BodyCascadeResolver resolver = new BodyCascadeResolver(new StubRandomSource(0, 0));

        assertThat(resolver.resistedDamage(skull, 12)).isEqualTo(7);
    }

    @Test
    void resistedDamage_neverGoesNegative() {
        BodyComponent skull = BodyComponent.structural("Skull", 20, 5, true, 8);
        BodyCascadeResolver resolver = new BodyCascadeResolver(new StubRandomSource(0, 0));

        assertThat(resolver.resistedDamage(skull, 3)).isZero();
    }

    @Test
    void pickOverflowTarget_isEmptyWhenNoProtectedChildren() {
        BodyComponent neck = BodyComponent.structural("Neck", 14, 3, true, 10);
        BodyCascadeResolver resolver = new BodyCascadeResolver(new StubRandomSource(0, 0));

        assertThat(resolver.pickOverflowTarget(neck)).isEmpty();
    }

    @Test
    void pickOverflowTarget_favorsHeavierOrganOnLowRoll() {
        BodyComponent chest = BodyComponent.structural("Chest", 25, 4, false, 7);
        BodyComponent heart = BodyComponent.protectedInternal("Heart", 12, 3, true, 18);
        BodyComponent liver = BodyComponent.protectedInternal("Liver", 6, 2, true, 16);
        chest.addChild(heart);
        chest.addChild(liver);

        BodyCascadeResolver resolver = new BodyCascadeResolver(new StubRandomSource(0, 0));

        Optional<BodyComponent> target = resolver.pickOverflowTarget(chest);

        assertThat(target).contains(heart);
    }

    @Test
    void pickOverflowTarget_picksLighterOrganOnHighRoll() {
        BodyComponent chest = BodyComponent.structural("Chest", 25, 4, false, 7);
        BodyComponent heart = BodyComponent.protectedInternal("Heart", 12, 3, true, 18);
        BodyComponent liver = BodyComponent.protectedInternal("Liver", 6, 2, true, 16);
        chest.addChild(heart);
        chest.addChild(liver);

        BodyCascadeResolver resolver = new BodyCascadeResolver(new StubRandomSource(17, 0));

        Optional<BodyComponent> target = resolver.pickOverflowTarget(chest);

        assertThat(target).contains(liver);
    }

    @Test
    void appendageSlips_isTrueWhenRollBeatsSlipChance() {
        BodyComponent eye = BodyComponent.attachedAppendage("RightEye", 3, 0, false, 16, 0.05, 0.5);
        BodyCascadeResolver resolver = new BodyCascadeResolver(new StubRandomSource(0, 0.01));

        assertThat(resolver.appendageSlips(eye)).isTrue();
    }

    @Test
    void appendageSlips_isFalseWhenRollMissesSlipChance() {
        BodyComponent eye = BodyComponent.attachedAppendage("RightEye", 3, 0, false, 16, 0.05, 0.5);
        BodyCascadeResolver resolver = new BodyCascadeResolver(new StubRandomSource(0, 0.5));

        assertThat(resolver.appendageSlips(eye)).isFalse();
    }

    @Test
    void slippedDamage_appliesFractionToIncomingDamage() {
        BodyComponent eye = BodyComponent.attachedAppendage("RightEye", 3, 0, false, 16, 0.05, 0.5);
        BodyCascadeResolver resolver = new BodyCascadeResolver(new StubRandomSource(0, 0));

        assertThat(resolver.slippedDamage(eye, 10)).isEqualTo(5);
    }

    private static final class StubRandomSource implements RandomSource {

        private final int intValue;
        private final double doubleValue;

        private StubRandomSource(int intValue, double doubleValue) {
            this.intValue = intValue;
            this.doubleValue = doubleValue;
        }

        @Override
        public int nextInt(int bound) {
            return intValue;
        }

        @Override
        public double nextDouble() {
            return doubleValue;
        }
    }
}
