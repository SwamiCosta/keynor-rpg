package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.CombatActionTimeResult;
import com.keynor.rpg.domain.model.CombatActionType;
import com.keynor.rpg.domain.model.CombatAttributeInputs;
import com.keynor.rpg.domain.model.CombatTimingCoefficients;
import com.keynor.rpg.domain.port.in.CalculateCombatActionTimeUseCase;
import com.keynor.rpg.domain.service.CombatActionTimeCalculator;

/**
 * Stateless, same precedent as {@link PreviewAttributesService}: no character identity or
 * persistence, default coefficients since no per-character tuning exists yet.
 */
public class CombatActionTimeService implements CalculateCombatActionTimeUseCase {

    private final CombatActionTimeCalculator calculator;

    public CombatActionTimeService(CombatActionTimeCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public CombatActionTimeResult calculate(CombatActionType action, CombatAttributeInputs inputs) {
        return calculator.calculate(action, inputs, CombatTimingCoefficients.defaults());
    }
}
