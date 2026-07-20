package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.ArmorProtection;
import com.keynor.rpg.domain.model.DamageType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProtectionCalculatorTest {

    private final ProtectionCalculator calculator = new ProtectionCalculator();

    @Test
    void calculate_multipliesBaseDurabilityByDamageTypeModifierAndDimension() {
        double protection = calculator.calculate(ArmorProtection.STEEL_PLATE_BREASTPLATE, DamageType.CHOP);

        assertThat(protection).isEqualTo(240 * 1.5 * 0.8);
    }
}
