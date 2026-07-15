package com.keynor.rpg.domain.port.in;

import com.keynor.rpg.domain.model.CombatActionTimeResult;
import com.keynor.rpg.domain.model.CombatActionType;
import com.keynor.rpg.domain.model.CombatAttributeInputs;

public interface CalculateCombatActionTimeUseCase {

    CombatActionTimeResult calculate(CombatActionType action, CombatAttributeInputs inputs);
}
