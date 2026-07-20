package com.keynor.rpg.domain.model;

/**
 * The raw attribute values a {@link CombatActionTimeCalculator} needs for a given
 * {@link CombatActionType} — nullable, since a caller only ever has to supply whichever subset
 * of these five the requested action's formula actually reads. Stateless by design: this is not
 * a snapshot of a real {@link PlayableCharacter}, since board tokens (the current caller) are
 * not tied to one — same "explicit values in, no character identity" shape as
 * {@link com.keynor.rpg.domain.port.in.PreviewAttributesUseCase}. {@code meleeDexterity}
 * replaces the former separate {@code closeCombat}/{@code shortRangeCombat} fields (2026-07-20) —
 * both used to weight the same family of melee-timing formulas under two different names; now
 * a single attribute, {@code MeleeDexterity}, feeds all of them.
 */
public record CombatAttributeInputs(Double speed, Double cognitiveSpeed, Double meleeDexterity,
                                     Double evasion) {
}
