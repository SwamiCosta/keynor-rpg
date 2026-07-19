package com.keynor.rpg.domain.model;

/**
 * The raw attribute values a {@link CombatActionTimeCalculator} needs for a given
 * {@link CombatActionType} — nullable, since a caller only ever has to supply whichever subset
 * of these six the requested action's formula actually reads. Stateless by design: this is not
 * a snapshot of a real {@link PlayableCharacter}, since board tokens (the current caller) are
 * not tied to one — same "explicit values in, no character identity" shape as
 * {@link com.keynor.rpg.domain.port.in.PreviewAttributesUseCase}.
 */
public record CombatAttributeInputs(Double speed, Double cognitiveSpeed, Double closeCombat,
                                     Double shortRangeCombat, Double evasion) {
}
