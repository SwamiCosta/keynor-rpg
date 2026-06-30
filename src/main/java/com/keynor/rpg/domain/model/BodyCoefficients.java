package com.keynor.rpg.domain.model;

/**
 * Free, mutable coefficients for every {@link PlayableCharacter} physical attribute formula.
 * k1..k9 and c match the naming used in the user's design document; kBmr/kActivityCost/kEfficiency
 * are additional coefficients introduced to make the EnergyCost formula concrete, since the
 * document left {@code ActivityCost} and {@code Eficiencia} as named-but-unspecified terms.
 * kSense/kEvasion/kEvasionNeural/kEvasionFlex/kAcrobatics/kMelee/kAim are coefficients for the
 * {@link SpatialIntelligence}-derived attribute formulas.
 *
 * <p>Most default to 1.0 (neutral multiplier): there is no "scientifically correct" value, only
 * what plays well — same caveat as {@code Body}'s illustrative hit point placeholders.
 *
 * <p><b>Exceptions to the 1.0 default:</b>
 * <ul>
 *   <li>{@code kBoneMass} (2.7), {@code kBoneDensity} (0.06), {@code kOrganWaterMass} (6.3) —
 *       calibrated so {@link PlayableCharacter#getTotalMass()} reproduces the previous hardcoded
 *       70 kg human default almost exactly at {@code Genetics.defaults()}.
 *   <li>{@code kMuscleDistributionStrength} (0.02) and {@code kMuscleDistributionSpeed} (0.04) —
 *       scale a deviation term ({@code muscleDistribution - 5}, range -5..+5) rather than a whole
 *       formula, so 1.0 would swing the result by up to +-500%.
 *   <li>{@code kEvasionNeural} (0.1) and {@code kEvasionFlex} (0.1) — scale raw 0-10 trait values
 *       inside a {@code (1 + k * x)} modifier; 1.0 would produce multipliers up to 11x.
 * </ul>
 */
public class BodyCoefficients {

    private double k1 = 1; // Strength
    private double c = 1;  // Strength's leverage term
    private double k2 = 1; // Speed
    private double k3 = 1; // StaminaPool
    private double k4 = 1; // FatigueRate - Kleiber mass term
    private double k5 = 1; // FatigueRate - muscle mass x intensity term
    private double k6 = 1; // FatigueRate - cardiovascular recovery term
    private double kBmr = 1;          // EnergyCost - BMR base (Kleiber)
    private double kActivityCost = 1; // EnergyCost - activity term
    private double kEfficiency = 1;   // EnergyCost - efficiency subtraction
    private double k7 = 1; // Durability - bone density / mesomorphy term
    private double k8 = 1; // Durability - mass inertia term
    private double k9 = 1; // Durability - fat cushion term
    private double kFlexibilityDurability = 1; // Durability - flexibility deviation modifier
    private double kBoneMass = 2.7;        // BoneMass - height^2 base term
    private double kBoneDensity = 0.06;    // BoneMass - density deviation modifier
    private double kOrganWaterMass = 6.3;  // OrganWaterMass - height^2 base term
    private double kMuscleDistributionStrength = 0.02; // Strength - muscleDistribution deviation modifier
    private double kMuscleDistributionSpeed = 0.04;    // MaxMovementSpeed - muscleDistribution deviation modifier
    private double kSense = 1;          // Sight / Hearing / Smell
    private double kEvasion = 1;        // Evasion - leading scale
    private double kEvasionNeural = 0.1; // Evasion - neural drive modifier
    private double kEvasionFlex = 0.1;   // Evasion - flexibility modifier
    private double kAcrobatics = 1;     // Acrobatics
    private double kMelee = 1;          // Melee Accuracy
    private double kAim = 1;            // Aim

    public static BodyCoefficients defaults() {
        return new BodyCoefficients();
    }

    public double getK1() { return k1; }
    public void setK1(double k1) { this.k1 = k1; }

    public double getC() { return c; }
    public void setC(double c) { this.c = c; }

    public double getK2() { return k2; }
    public void setK2(double k2) { this.k2 = k2; }

    public double getK3() { return k3; }
    public void setK3(double k3) { this.k3 = k3; }

    public double getK4() { return k4; }
    public void setK4(double k4) { this.k4 = k4; }

    public double getK5() { return k5; }
    public void setK5(double k5) { this.k5 = k5; }

    public double getK6() { return k6; }
    public void setK6(double k6) { this.k6 = k6; }

    public double getKBmr() { return kBmr; }
    public void setKBmr(double kBmr) { this.kBmr = kBmr; }

    public double getKActivityCost() { return kActivityCost; }
    public void setKActivityCost(double kActivityCost) { this.kActivityCost = kActivityCost; }

    public double getKEfficiency() { return kEfficiency; }
    public void setKEfficiency(double kEfficiency) { this.kEfficiency = kEfficiency; }

    public double getK7() { return k7; }
    public void setK7(double k7) { this.k7 = k7; }

    public double getK8() { return k8; }
    public void setK8(double k8) { this.k8 = k8; }

    public double getK9() { return k9; }
    public void setK9(double k9) { this.k9 = k9; }

    public double getKFlexibilityDurability() { return kFlexibilityDurability; }
    public void setKFlexibilityDurability(double kFlexibilityDurability) {
        this.kFlexibilityDurability = kFlexibilityDurability;
    }

    public double getKBoneMass() { return kBoneMass; }
    public void setKBoneMass(double kBoneMass) { this.kBoneMass = kBoneMass; }

    public double getKBoneDensity() { return kBoneDensity; }
    public void setKBoneDensity(double kBoneDensity) { this.kBoneDensity = kBoneDensity; }

    public double getKOrganWaterMass() { return kOrganWaterMass; }
    public void setKOrganWaterMass(double kOrganWaterMass) { this.kOrganWaterMass = kOrganWaterMass; }

    public double getKMuscleDistributionStrength() { return kMuscleDistributionStrength; }
    public void setKMuscleDistributionStrength(double kMuscleDistributionStrength) {
        this.kMuscleDistributionStrength = kMuscleDistributionStrength;
    }

    public double getKMuscleDistributionSpeed() { return kMuscleDistributionSpeed; }
    public void setKMuscleDistributionSpeed(double kMuscleDistributionSpeed) {
        this.kMuscleDistributionSpeed = kMuscleDistributionSpeed;
    }

    public double getKSense() { return kSense; }
    public void setKSense(double kSense) { this.kSense = kSense; }

    public double getKEvasion() { return kEvasion; }
    public void setKEvasion(double kEvasion) { this.kEvasion = kEvasion; }

    public double getKEvasionNeural() { return kEvasionNeural; }
    public void setKEvasionNeural(double kEvasionNeural) { this.kEvasionNeural = kEvasionNeural; }

    public double getKEvasionFlex() { return kEvasionFlex; }
    public void setKEvasionFlex(double kEvasionFlex) { this.kEvasionFlex = kEvasionFlex; }

    public double getKAcrobatics() { return kAcrobatics; }
    public void setKAcrobatics(double kAcrobatics) { this.kAcrobatics = kAcrobatics; }

    public double getKMelee() { return kMelee; }
    public void setKMelee(double kMelee) { this.kMelee = kMelee; }

    public double getKAim() { return kAim; }
    public void setKAim(double kAim) { this.kAim = kAim; }
}
