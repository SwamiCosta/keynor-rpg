package com.keynor.rpg.infrastructure.web;

import com.keynor.rpg.application.dto.CombatActionTimeRequest;
import com.keynor.rpg.application.dto.CombatActionTimeResponse;
import com.keynor.rpg.domain.port.in.CalculateCombatActionTimeUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Stateless UT (Unit of Time) calculator for the combat board — see
 * {@code game-rules.md}'s "Time tracking" section and {@code CombatActionTimeCalculator}'s
 * javadoc for the formula. Only movement ({@code WALK_1M}) is currently called by
 * {@code keynor-rpg-client}; every other {@code CombatActionType} is reachable here ahead of the
 * frontend gaining a way to trigger them.
 */
@RestController
@RequestMapping("/api/v1/combat")
public class CombatActionTimeController {

    private final CalculateCombatActionTimeUseCase calculateCombatActionTimeUseCase;

    public CombatActionTimeController(CalculateCombatActionTimeUseCase calculateCombatActionTimeUseCase) {
        this.calculateCombatActionTimeUseCase = calculateCombatActionTimeUseCase;
    }

    @PostMapping("/action-time")
    public CombatActionTimeResponse actionTime(@RequestBody CombatActionTimeRequest request) {
        return CombatActionTimeResponse.from(
                calculateCombatActionTimeUseCase.calculate(request.action(), request.toAttributeInputs()));
    }
}
