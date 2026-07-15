package com.keynor.rpg.domain.model;

/**
 * Every free coefficient behind {@link CombatActionTimeCalculator}'s UT formula: one
 * {@code utBaseXxx} field per {@link CombatActionType}, plus the weight fields feeding that
 * action's attribute score. Defaults come straight from the UT balancing report (2026-07-14).
 * Mutable, independently settable fields — same convention as {@link BodyCoefficients} — so
 * combat pacing can be tuned without touching {@link CombatActionTimeCalculator}'s code.
 */
public class CombatTimingCoefficients {

    private int utBaseWalk1m = 5;
    private double kWalk1mSpeed = 1.0;

    private int utBaseRun1m = 2;
    private double kRun1mSpeed = 1.0;

    private int utBaseJab = 2;
    private double kJabSpeed = 0.7;
    private double kJabCloseCombat = 0.3;

    private int utBaseBodyStrike = 4;
    private double kBodyStrikeSpeed = 0.6;
    private double kBodyStrikeCloseCombat = 0.4;

    private int utBasePiercingAttack = 3;
    private double kPiercingAttackSpeed = 0.7;
    private double kPiercingAttackShortRangeCombat = 0.3;

    private int utBaseLightSwingAttack = 4;
    private double kLightSwingAttackSpeed = 0.7;
    private double kLightSwingAttackShortRangeCombat = 0.3;

    private int utBaseHeavySwingAttack = 8;
    private double kHeavySwingAttackSpeed = 0.65;
    private double kHeavySwingAttackShortRangeCombat = 0.35;

    private int utBaseDrinkPotion = 12;
    private double kDrinkPotionSpeed = 1.0;

    private int utBaseDrawMeleeWeapon = 5;
    private double kDrawMeleeWeaponSpeed = 0.6;
    private double kDrawMeleeWeaponShortRangeCombat = 0.4;

    private int utBaseDrawRangedWeapon = 5;
    private double kDrawRangedWeaponSpeed = 0.6;
    private double kDrawRangedWeaponLongRangeCombat = 0.4;

    private int utBaseDrawFromBackpack = 40;
    private double kDrawFromBackpackSpeed = 1.0;

    private int utBaseReloadPistol = 15;
    private double kReloadPistolLongRangeCombat = 0.7;
    private double kReloadPistolSpeed = 0.3;

    private int utBaseReloadLongGun = 25;
    private double kReloadLongGunLongRangeCombat = 0.75;
    private double kReloadLongGunSpeed = 0.25;

    private int utBaseEvasion = 3;
    private double kEvasionEvasion = 0.7;
    private double kEvasionSpeed = 0.3;

    private int utBaseBlock = 2;
    private double kBlockCognitiveSpeed = 0.4;
    private double kBlockShortRangeCombat = 0.3;
    private double kBlockSpeed = 0.3;

    private int utBaseStandUp = 10;
    private double kStandUpSpeed = 1.0;

    private int utBaseAim = 4;
    private double kAimLongRangeCombat = 0.7;
    private double kAimSpeed = 0.3;

    private int utBaseDrawHeavyWeapon = 8;
    private double kDrawHeavyWeaponSpeed = 0.7;
    private double kDrawHeavyWeaponShortRangeCombat = 0.3;

    private int utBaseTurnAround = 2;
    private double kTurnAroundSpeed = 1.0;

    private int utBaseAnalyzeSurroundings = 3;
    private double kAnalyzeSurroundingsCognitiveSpeed = 1.0;

    private int utBaseCastSpell = 10;
    private double kCastSpellSpeed = 1.0;

    public int getUtBaseWalk1m() { return utBaseWalk1m; }
    public void setUtBaseWalk1m(int v) { this.utBaseWalk1m = v; }

    public double getKWalk1mSpeed() { return kWalk1mSpeed; }
    public void setKWalk1mSpeed(double v) { this.kWalk1mSpeed = v; }

    public int getUtBaseRun1m() { return utBaseRun1m; }
    public void setUtBaseRun1m(int v) { this.utBaseRun1m = v; }

    public double getKRun1mSpeed() { return kRun1mSpeed; }
    public void setKRun1mSpeed(double v) { this.kRun1mSpeed = v; }

    public int getUtBaseJab() { return utBaseJab; }
    public void setUtBaseJab(int v) { this.utBaseJab = v; }

    public double getKJabSpeed() { return kJabSpeed; }
    public void setKJabSpeed(double v) { this.kJabSpeed = v; }

    public double getKJabCloseCombat() { return kJabCloseCombat; }
    public void setKJabCloseCombat(double v) { this.kJabCloseCombat = v; }

    public int getUtBaseBodyStrike() { return utBaseBodyStrike; }
    public void setUtBaseBodyStrike(int v) { this.utBaseBodyStrike = v; }

    public double getKBodyStrikeSpeed() { return kBodyStrikeSpeed; }
    public void setKBodyStrikeSpeed(double v) { this.kBodyStrikeSpeed = v; }

    public double getKBodyStrikeCloseCombat() { return kBodyStrikeCloseCombat; }
    public void setKBodyStrikeCloseCombat(double v) { this.kBodyStrikeCloseCombat = v; }

    public int getUtBasePiercingAttack() { return utBasePiercingAttack; }
    public void setUtBasePiercingAttack(int v) { this.utBasePiercingAttack = v; }

    public double getKPiercingAttackSpeed() { return kPiercingAttackSpeed; }
    public void setKPiercingAttackSpeed(double v) { this.kPiercingAttackSpeed = v; }

    public double getKPiercingAttackShortRangeCombat() { return kPiercingAttackShortRangeCombat; }
    public void setKPiercingAttackShortRangeCombat(double v) { this.kPiercingAttackShortRangeCombat = v; }

    public int getUtBaseLightSwingAttack() { return utBaseLightSwingAttack; }
    public void setUtBaseLightSwingAttack(int v) { this.utBaseLightSwingAttack = v; }

    public double getKLightSwingAttackSpeed() { return kLightSwingAttackSpeed; }
    public void setKLightSwingAttackSpeed(double v) { this.kLightSwingAttackSpeed = v; }

    public double getKLightSwingAttackShortRangeCombat() { return kLightSwingAttackShortRangeCombat; }
    public void setKLightSwingAttackShortRangeCombat(double v) { this.kLightSwingAttackShortRangeCombat = v; }

    public int getUtBaseHeavySwingAttack() { return utBaseHeavySwingAttack; }
    public void setUtBaseHeavySwingAttack(int v) { this.utBaseHeavySwingAttack = v; }

    public double getKHeavySwingAttackSpeed() { return kHeavySwingAttackSpeed; }
    public void setKHeavySwingAttackSpeed(double v) { this.kHeavySwingAttackSpeed = v; }

    public double getKHeavySwingAttackShortRangeCombat() { return kHeavySwingAttackShortRangeCombat; }
    public void setKHeavySwingAttackShortRangeCombat(double v) { this.kHeavySwingAttackShortRangeCombat = v; }

    public int getUtBaseDrinkPotion() { return utBaseDrinkPotion; }
    public void setUtBaseDrinkPotion(int v) { this.utBaseDrinkPotion = v; }

    public double getKDrinkPotionSpeed() { return kDrinkPotionSpeed; }
    public void setKDrinkPotionSpeed(double v) { this.kDrinkPotionSpeed = v; }

    public int getUtBaseDrawMeleeWeapon() { return utBaseDrawMeleeWeapon; }
    public void setUtBaseDrawMeleeWeapon(int v) { this.utBaseDrawMeleeWeapon = v; }

    public double getKDrawMeleeWeaponSpeed() { return kDrawMeleeWeaponSpeed; }
    public void setKDrawMeleeWeaponSpeed(double v) { this.kDrawMeleeWeaponSpeed = v; }

    public double getKDrawMeleeWeaponShortRangeCombat() { return kDrawMeleeWeaponShortRangeCombat; }
    public void setKDrawMeleeWeaponShortRangeCombat(double v) { this.kDrawMeleeWeaponShortRangeCombat = v; }

    public int getUtBaseDrawRangedWeapon() { return utBaseDrawRangedWeapon; }
    public void setUtBaseDrawRangedWeapon(int v) { this.utBaseDrawRangedWeapon = v; }

    public double getKDrawRangedWeaponSpeed() { return kDrawRangedWeaponSpeed; }
    public void setKDrawRangedWeaponSpeed(double v) { this.kDrawRangedWeaponSpeed = v; }

    public double getKDrawRangedWeaponLongRangeCombat() { return kDrawRangedWeaponLongRangeCombat; }
    public void setKDrawRangedWeaponLongRangeCombat(double v) { this.kDrawRangedWeaponLongRangeCombat = v; }

    public int getUtBaseDrawFromBackpack() { return utBaseDrawFromBackpack; }
    public void setUtBaseDrawFromBackpack(int v) { this.utBaseDrawFromBackpack = v; }

    public double getKDrawFromBackpackSpeed() { return kDrawFromBackpackSpeed; }
    public void setKDrawFromBackpackSpeed(double v) { this.kDrawFromBackpackSpeed = v; }

    public int getUtBaseReloadPistol() { return utBaseReloadPistol; }
    public void setUtBaseReloadPistol(int v) { this.utBaseReloadPistol = v; }

    public double getKReloadPistolLongRangeCombat() { return kReloadPistolLongRangeCombat; }
    public void setKReloadPistolLongRangeCombat(double v) { this.kReloadPistolLongRangeCombat = v; }

    public double getKReloadPistolSpeed() { return kReloadPistolSpeed; }
    public void setKReloadPistolSpeed(double v) { this.kReloadPistolSpeed = v; }

    public int getUtBaseReloadLongGun() { return utBaseReloadLongGun; }
    public void setUtBaseReloadLongGun(int v) { this.utBaseReloadLongGun = v; }

    public double getKReloadLongGunLongRangeCombat() { return kReloadLongGunLongRangeCombat; }
    public void setKReloadLongGunLongRangeCombat(double v) { this.kReloadLongGunLongRangeCombat = v; }

    public double getKReloadLongGunSpeed() { return kReloadLongGunSpeed; }
    public void setKReloadLongGunSpeed(double v) { this.kReloadLongGunSpeed = v; }

    public int getUtBaseEvasion() { return utBaseEvasion; }
    public void setUtBaseEvasion(int v) { this.utBaseEvasion = v; }

    public double getKEvasionEvasion() { return kEvasionEvasion; }
    public void setKEvasionEvasion(double v) { this.kEvasionEvasion = v; }

    public double getKEvasionSpeed() { return kEvasionSpeed; }
    public void setKEvasionSpeed(double v) { this.kEvasionSpeed = v; }

    public int getUtBaseBlock() { return utBaseBlock; }
    public void setUtBaseBlock(int v) { this.utBaseBlock = v; }

    public double getKBlockCognitiveSpeed() { return kBlockCognitiveSpeed; }
    public void setKBlockCognitiveSpeed(double v) { this.kBlockCognitiveSpeed = v; }

    public double getKBlockShortRangeCombat() { return kBlockShortRangeCombat; }
    public void setKBlockShortRangeCombat(double v) { this.kBlockShortRangeCombat = v; }

    public double getKBlockSpeed() { return kBlockSpeed; }
    public void setKBlockSpeed(double v) { this.kBlockSpeed = v; }

    public int getUtBaseStandUp() { return utBaseStandUp; }
    public void setUtBaseStandUp(int v) { this.utBaseStandUp = v; }

    public double getKStandUpSpeed() { return kStandUpSpeed; }
    public void setKStandUpSpeed(double v) { this.kStandUpSpeed = v; }

    public int getUtBaseAim() { return utBaseAim; }
    public void setUtBaseAim(int v) { this.utBaseAim = v; }

    public double getKAimLongRangeCombat() { return kAimLongRangeCombat; }
    public void setKAimLongRangeCombat(double v) { this.kAimLongRangeCombat = v; }

    public double getKAimSpeed() { return kAimSpeed; }
    public void setKAimSpeed(double v) { this.kAimSpeed = v; }

    public int getUtBaseDrawHeavyWeapon() { return utBaseDrawHeavyWeapon; }
    public void setUtBaseDrawHeavyWeapon(int v) { this.utBaseDrawHeavyWeapon = v; }

    public double getKDrawHeavyWeaponSpeed() { return kDrawHeavyWeaponSpeed; }
    public void setKDrawHeavyWeaponSpeed(double v) { this.kDrawHeavyWeaponSpeed = v; }

    public double getKDrawHeavyWeaponShortRangeCombat() { return kDrawHeavyWeaponShortRangeCombat; }
    public void setKDrawHeavyWeaponShortRangeCombat(double v) { this.kDrawHeavyWeaponShortRangeCombat = v; }

    public int getUtBaseTurnAround() { return utBaseTurnAround; }
    public void setUtBaseTurnAround(int v) { this.utBaseTurnAround = v; }

    public double getKTurnAroundSpeed() { return kTurnAroundSpeed; }
    public void setKTurnAroundSpeed(double v) { this.kTurnAroundSpeed = v; }

    public int getUtBaseAnalyzeSurroundings() { return utBaseAnalyzeSurroundings; }
    public void setUtBaseAnalyzeSurroundings(int v) { this.utBaseAnalyzeSurroundings = v; }

    public double getKAnalyzeSurroundingsCognitiveSpeed() { return kAnalyzeSurroundingsCognitiveSpeed; }
    public void setKAnalyzeSurroundingsCognitiveSpeed(double v) { this.kAnalyzeSurroundingsCognitiveSpeed = v; }

    public int getUtBaseCastSpell() { return utBaseCastSpell; }
    public void setUtBaseCastSpell(int v) { this.utBaseCastSpell = v; }

    public double getKCastSpellSpeed() { return kCastSpellSpeed; }
    public void setKCastSpellSpeed(double v) { this.kCastSpellSpeed = v; }

    public static CombatTimingCoefficients defaults() {
        return new CombatTimingCoefficients();
    }
}
