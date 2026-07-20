package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.CombatActionTimeResult;
import com.keynor.rpg.domain.model.CombatActionType;
import com.keynor.rpg.domain.model.CombatAttributeInputs;
import com.keynor.rpg.domain.model.CombatTimingCoefficients;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Values below are taken directly from the UT balancing report (2026-07-14). Every profile
 * (Abismal/Baixo/Padrão/Treinado/Elite) uses the same value for every attribute an action reads,
 * which is exactly how the report itself was simulated — so a single {@code fullProfile(value)}
 * helper covers every action regardless of how many attributes its formula combines.
 */
class CombatActionTimeCalculatorTest {

    private final CombatActionTimeCalculator calculator = new CombatActionTimeCalculator();
    private final CombatTimingCoefficients coeff = CombatTimingCoefficients.defaults();

    private static CombatAttributeInputs fullProfile(double value) {
        return new CombatAttributeInputs(value, value, value, value);
    }

    private int ut(CombatActionType action, double profileValue) {
        CombatActionTimeResult result = calculator.calculate(action, fullProfile(profileValue), coeff);
        return result.ut();
    }

    @ParameterizedTest(name = "{0} at Padrão (60) resolves to its own utBase ({1} UT)")
    @CsvSource({
            "WALK_1M, 5",
            "RUN_1M, 2",
            "JAB, 2",
            "BODY_STRIKE, 4",
            "PIERCING_ATTACK, 3",
            "LIGHT_SWING_ATTACK, 4",
            "HEAVY_SWING_ATTACK, 8",
            "DRINK_POTION, 12",
            "DRAW_MELEE_WEAPON, 5",
            "DRAW_RANGED_WEAPON, 5",
            "DRAW_FROM_BACKPACK, 40",
            "RELOAD_PISTOL, 15",
            "RELOAD_LONG_GUN, 25",
            "EVASION, 3",
            "BLOCK, 2",
            "STAND_UP, 10",
            "AIM, 4",
            "DRAW_HEAVY_WEAPON, 8",
            "TURN_AROUND, 2",
            "ANALYZE_SURROUNDINGS, 3",
            "CAST_SPELL, 10",
    })
    void everyAction_atPadraoProfile_resolvesToItsOwnUtBase(CombatActionType action, int utBase) {
        assertEquals(utBase, ut(action, 60));
    }

    @ParameterizedTest(name = "WALK_1M at profile {0} resolves to {1} UT")
    @CsvSource({ "20, 15", "40, 7", "60, 5", "80, 3", "100, 3" })
    void walk1m_acrossEveryProfile_matchesReport(double profileValue, int expectedUt) {
        assertEquals(expectedUt, ut(CombatActionType.WALK_1M, profileValue));
    }

    @ParameterizedTest(name = "JAB (2-attribute formula) at profile {0} resolves to {1} UT")
    @CsvSource({ "20, 6", "40, 3", "60, 2", "80, 1", "100, 1" })
    void jab_acrossEveryProfile_matchesReport(double profileValue, int expectedUt) {
        assertEquals(expectedUt, ut(CombatActionType.JAB, profileValue));
    }

    @ParameterizedTest(name = "BLOCK (3-attribute formula) at profile {0} resolves to {1} UT")
    @CsvSource({ "20, 6", "40, 3", "60, 2", "80, 1", "100, 1" })
    void block_acrossEveryProfile_matchesReport(double profileValue, int expectedUt) {
        assertEquals(expectedUt, ut(CombatActionType.BLOCK, profileValue));
    }

    @Test
    void calculate_missingRequiredAttribute_throws() {
        CombatAttributeInputs missingSpeed = new CombatAttributeInputs(null, null, 60.0, null);
        assertThrows(IllegalArgumentException.class,
                () -> calculator.calculate(CombatActionType.WALK_1M, missingSpeed, coeff));
    }
}
