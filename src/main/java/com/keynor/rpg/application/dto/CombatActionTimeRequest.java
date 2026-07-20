package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.CombatActionType;
import com.keynor.rpg.domain.model.CombatAttributeInputs;

/**
 * All five attribute fields are optional — the caller only needs to supply whichever subset the
 * requested {@link #action()}'s formula reads; {@link CombatActionTimeCalculator} raises a clear
 * error if a required one is missing. Board tokens are not tied to a real
 * {@code PlayableCharacter}, so this takes raw values directly, same shape as
 * {@link BodyPreviewRequest}'s sibling input DTOs. {@code meleeDexterity} replaces the former
 * separate {@code closeCombat}/{@code shortRangeCombat} fields (2026-07-20).
 */
public record CombatActionTimeRequest(CombatActionType action, Double speed, Double cognitiveSpeed,
                                       Double meleeDexterity, Double evasion) {

    public CombatAttributeInputs toAttributeInputs() {
        return new CombatAttributeInputs(speed, cognitiveSpeed, meleeDexterity, evasion);
    }
}
