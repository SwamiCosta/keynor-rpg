package com.keynor.rpg.domain.service;

import com.keynor.rpg.domain.model.CombatActionTimeResult;
import com.keynor.rpg.domain.model.CombatActionType;
import com.keynor.rpg.domain.model.CombatAttributeInputs;
import com.keynor.rpg.domain.model.CombatTimingCoefficients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the UT balancing report's (2026-07-14) shared formula:
 * {@code S = Σ weight × attribute}, {@code UT = max(1, floor(utBase × (60 / S)))}. One branch
 * per {@link CombatActionType}, each reading only the {@link CombatTimingCoefficients} weights
 * and {@link CombatAttributeInputs} fields its own formula needs — same "one hand-written
 * formula per case, weights externalized as coefficients" convention as
 * {@code PlayableCharacter}'s additive-standard attributes, not a generic data-driven engine.
 */
public class CombatActionTimeCalculator {

    private static final Logger log = LoggerFactory.getLogger(CombatActionTimeCalculator.class);

    public CombatActionTimeResult calculate(CombatActionType action, CombatAttributeInputs inputs,
                                             CombatTimingCoefficients coeff) {
        double score = score(action, inputs, coeff);
        int utBase = utBase(action, coeff);
        int ut = Math.max(1, (int) Math.floor(utBase * (60.0 / score)));
        log.debug("Combat action {} resolved to {} UT (score={}, utBase={})", action, ut, score, utBase);
        return new CombatActionTimeResult(ut, score);
    }

    private double score(CombatActionType action, CombatAttributeInputs inputs, CombatTimingCoefficients coeff) {
        return switch (action) {
            case WALK_1M -> coeff.getKWalk1mSpeed() * speed(inputs, action);
            case RUN_1M -> coeff.getKRun1mSpeed() * speed(inputs, action);
            case JAB -> coeff.getKJabSpeed() * speed(inputs, action)
                    + coeff.getKJabCloseCombat() * closeCombat(inputs, action);
            case BODY_STRIKE -> coeff.getKBodyStrikeSpeed() * speed(inputs, action)
                    + coeff.getKBodyStrikeCloseCombat() * closeCombat(inputs, action);
            case PIERCING_ATTACK -> coeff.getKPiercingAttackSpeed() * speed(inputs, action)
                    + coeff.getKPiercingAttackShortRangeCombat() * shortRangeCombat(inputs, action);
            case LIGHT_SWING_ATTACK -> coeff.getKLightSwingAttackSpeed() * speed(inputs, action)
                    + coeff.getKLightSwingAttackShortRangeCombat() * shortRangeCombat(inputs, action);
            case HEAVY_SWING_ATTACK -> coeff.getKHeavySwingAttackSpeed() * speed(inputs, action)
                    + coeff.getKHeavySwingAttackShortRangeCombat() * shortRangeCombat(inputs, action);
            case DRINK_POTION -> coeff.getKDrinkPotionSpeed() * speed(inputs, action);
            case DRAW_MELEE_WEAPON -> coeff.getKDrawMeleeWeaponSpeed() * speed(inputs, action)
                    + coeff.getKDrawMeleeWeaponShortRangeCombat() * shortRangeCombat(inputs, action);
            case DRAW_RANGED_WEAPON -> coeff.getKDrawRangedWeaponSpeed() * speed(inputs, action);
            case DRAW_FROM_BACKPACK -> coeff.getKDrawFromBackpackSpeed() * speed(inputs, action);
            case RELOAD_PISTOL -> coeff.getKReloadPistolSpeed() * speed(inputs, action);
            case RELOAD_LONG_GUN -> coeff.getKReloadLongGunSpeed() * speed(inputs, action);
            case EVASION -> coeff.getKEvasionEvasion() * evasion(inputs, action)
                    + coeff.getKEvasionSpeed() * speed(inputs, action);
            case BLOCK -> coeff.getKBlockCognitiveSpeed() * cognitiveSpeed(inputs, action)
                    + coeff.getKBlockShortRangeCombat() * shortRangeCombat(inputs, action)
                    + coeff.getKBlockSpeed() * speed(inputs, action);
            case STAND_UP -> coeff.getKStandUpSpeed() * speed(inputs, action);
            case AIM -> coeff.getKAimSpeed() * speed(inputs, action);
            case DRAW_HEAVY_WEAPON -> coeff.getKDrawHeavyWeaponSpeed() * speed(inputs, action)
                    + coeff.getKDrawHeavyWeaponShortRangeCombat() * shortRangeCombat(inputs, action);
            case TURN_AROUND -> coeff.getKTurnAroundSpeed() * speed(inputs, action);
            case ANALYZE_SURROUNDINGS -> coeff.getKAnalyzeSurroundingsCognitiveSpeed() * cognitiveSpeed(inputs, action);
            case CAST_SPELL -> coeff.getKCastSpellSpeed() * speed(inputs, action);
        };
    }

    private int utBase(CombatActionType action, CombatTimingCoefficients coeff) {
        return switch (action) {
            case WALK_1M -> coeff.getUtBaseWalk1m();
            case RUN_1M -> coeff.getUtBaseRun1m();
            case JAB -> coeff.getUtBaseJab();
            case BODY_STRIKE -> coeff.getUtBaseBodyStrike();
            case PIERCING_ATTACK -> coeff.getUtBasePiercingAttack();
            case LIGHT_SWING_ATTACK -> coeff.getUtBaseLightSwingAttack();
            case HEAVY_SWING_ATTACK -> coeff.getUtBaseHeavySwingAttack();
            case DRINK_POTION -> coeff.getUtBaseDrinkPotion();
            case DRAW_MELEE_WEAPON -> coeff.getUtBaseDrawMeleeWeapon();
            case DRAW_RANGED_WEAPON -> coeff.getUtBaseDrawRangedWeapon();
            case DRAW_FROM_BACKPACK -> coeff.getUtBaseDrawFromBackpack();
            case RELOAD_PISTOL -> coeff.getUtBaseReloadPistol();
            case RELOAD_LONG_GUN -> coeff.getUtBaseReloadLongGun();
            case EVASION -> coeff.getUtBaseEvasion();
            case BLOCK -> coeff.getUtBaseBlock();
            case STAND_UP -> coeff.getUtBaseStandUp();
            case AIM -> coeff.getUtBaseAim();
            case DRAW_HEAVY_WEAPON -> coeff.getUtBaseDrawHeavyWeapon();
            case TURN_AROUND -> coeff.getUtBaseTurnAround();
            case ANALYZE_SURROUNDINGS -> coeff.getUtBaseAnalyzeSurroundings();
            case CAST_SPELL -> coeff.getUtBaseCastSpell();
        };
    }

    private double speed(CombatAttributeInputs inputs, CombatActionType action) {
        return require(inputs.speed(), action, "speed");
    }

    private double cognitiveSpeed(CombatAttributeInputs inputs, CombatActionType action) {
        return require(inputs.cognitiveSpeed(), action, "cognitiveSpeed");
    }

    private double closeCombat(CombatAttributeInputs inputs, CombatActionType action) {
        return require(inputs.closeCombat(), action, "closeCombat");
    }

    private double shortRangeCombat(CombatAttributeInputs inputs, CombatActionType action) {
        return require(inputs.shortRangeCombat(), action, "shortRangeCombat");
    }

    private double evasion(CombatAttributeInputs inputs, CombatActionType action) {
        return require(inputs.evasion(), action, "evasion");
    }

    private double require(Double value, CombatActionType action, String attributeName) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "Combat action " + action + " requires '" + attributeName + "' to be provided");
        }
        return value;
    }
}
