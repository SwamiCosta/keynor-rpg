package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.port.out.RandomSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SpecialAttackTestResolverTest {

    @Test
    void roll_worstD20_subtractsNineFromAttribute() {
        SpecialAttackTestResolver resolver = new SpecialAttackTestResolver(fixedD20(1));

        assertThat(resolver.roll(60)).isEqualTo(51);
    }

    @Test
    void roll_bestD20_addsTenToAttribute() {
        SpecialAttackTestResolver resolver = new SpecialAttackTestResolver(fixedD20(20));

        assertThat(resolver.roll(60)).isEqualTo(70);
    }

    private static RandomSource fixedD20(int rolledValue) {
        return new RandomSource() {
            @Override
            public int nextInt(int bound) {
                return rolledValue - 1;
            }

            @Override
            public double nextDouble() {
                return 0;
            }
        };
    }
}
