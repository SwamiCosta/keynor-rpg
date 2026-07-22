package com.keynor.rpg.domain.model;

/**
 * Which of {@code PlayableCharacter}'s three specialized strengths (Delta V4) a
 * {@link MeleeAttackProfile} rolls as Tstr. Mapping confirmed by the user (2026-07-20): thrust
 * attacks (Estocada) use Upper Strike; every swing-based attack (chop, slice, crush, tear, etc.)
 * uses Swing Power. No weapon in the melee table uses Leg Drive (kicks aren't a listed weapon
 * attack type) — it stays in the enum since {@code game-rules.md} documents it as a real Tstr
 * source, ready for a future unarmed/kick attack profile.
 */
public enum MeleeForceAttribute {
    LEG_DRIVE,
    UPPER_STRIKE,
    SWING_POWER
}
