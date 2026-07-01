package com.keynor.rpg.domain.model;

/**
 * Free, mutable coefficients for every {@link PlayableCharacter} physical attribute formula,
 * under the additive standard introduced in rpg-11: every derived attribute is
 * {@code baseline + sum(weight x (input - neutral))}, replacing the previous multiplicative
 * model. {@code baseline} defaults to 60 (raised from the original 35 design-doc value by
 * user request, so every formula's constant term is +25 higher).
 *
 * <p>Field names follow {@code k<Formula><Term>} so each coefficient's origin formula and
 * role is readable without cross-referencing the design document. Most weights are small
 * integers taken directly from the design document — they are still exposed as mutable
 * fields (not inlined into {@link PlayableCharacter}) so game balance can be tuned without
 * touching formula code, same principle as the pre-rpg-11 coefficient set.
 *
 * <p>{@code kLoadCapacityStrengthOffset} (25) is a deliberate exception to the "baseline
 * feeds every formula" rule: the Load Capacity group subtracts it from {@code Strength}
 * before applying {@link PlayableCharacter#getMaxCapacityKg()}'s formula, so load numbers
 * stay calibrated to the original baseline-35 design rather than inflating when
 * {@code baseline} was later raised to 60.
 *
 * <p>Neutral points (5 for every 1-9 trait, 3 for {@code limbRatio}... — see each domain
 * class) are not coefficients: they are fixed scale midpoints, not tunable weights, so they
 * stay as literals inside {@link PlayableCharacter}'s formulas, matching the pre-rpg-11
 * convention (e.g. {@code muscleDistributionDeviation()}).
 */
public class BodyCoefficients {

    private double baseline = 60; // shared additive baseline for every attribute formula

    // Strength
    private double kStrengthMuscleMass = 4;
    private double kStrengthNeuromuscular = 2;
    private double kStrengthFiberType = 1;
    private double kStrengthLimbRatio = 2;
    private double kStrengthMuscleDistribution = 1;

    // Speed
    private double kSpeedNeuromuscular = 4;
    private double kSpeedMuscleMass = 1;
    private double kSpeedFiberType = 2;
    private double kSpeedMassNeutral = 25; // SymbolicTotalMass neutral point, not the attribute baseline
    private double kSpeedMassDivisor = 3;

    // StaminaPool
    private double kStaminaPoolPulmonary = 3;
    private double kStaminaPoolCardiac = 1;
    private double kStaminaPoolOxygen = 1;
    private double kStaminaPoolFiberType = 2;

    // FatigueResistance
    private double kFatigueResistanceCardiac = 3;
    private double kFatigueResistancePulmonary = 1;
    private double kFatigueResistanceOxygen = 1;
    private double kFatigueResistanceNeuromuscular = 2;
    private double kFatigueResistanceMassNeutral = 25;
    private double kFatigueResistanceMassDivisor = 2;
    private double kFatigueResistanceMuscleMass = 1;

    // StaminaRecovery
    private double kStaminaRecoveryOxygen = 3;
    private double kStaminaRecoveryPulmonary = 1;
    private double kStaminaRecoveryCardiac = 1;
    private double kStaminaRecoveryFiberType = 1;

    // Durability
    private double kDurabilityBoneDensity = 2;
    private double kDurabilityMesomorphy = 1;
    private double kDurabilityBodyFat = 1;
    private double kDurabilityFlexibility = 1;

    // Sight / Hearing / Smell
    private double kSensePerception = 3;
    private double kSenseNeuralDrive = 1;

    // Acrobatics
    private double kAcrobaticsAgility = 2;
    private double kAcrobaticsFlexibility = 2;

    // MeleeAccuracy
    private double kMeleeAccuracyPrecision = 3;
    private double kMeleeAccuracyAgility = 1;

    // Aim
    private double kAimPrecision = 3;
    private double kAimPerception = 1;

    // Evasion (extends Speed)
    private double kEvasionAgility = 2;
    private double kEvasionNeuralDrive = 1;
    private double kEvasionFlexibility = 1;

    // MaxMovementSpeed (extends Speed)
    private double kMaxMovementSpeedLimbRatio = 2;
    private double kMaxMovementSpeedMuscleDistribution = 1;

    // Mass model
    private double kSymbolicMassBase = 10;
    private double kMuscleKgMultiplier = 5;
    private double kMuscleKgOffset = 5;
    private double kFatKgMultiplier = 5;
    private double kFrameKgBase = 16;
    private double kFrameKgHeightMultiplier = 3;
    private double kFrameKgDivisor = 2;

    // Load capacity
    private double kLightLoadFraction = 0.3;
    private double kHeavyLoadFraction = 0.7;
    private double kMaxCapacityDivisor = 25;
    private double kDragCapacityMultiplier = 2;
    private double kDragCapacityMassFraction = 0.5;
    private double kLoadCapacityStrengthOffset = 25; // undoes the baseline-60 shift for load formulas only

    // Safety floor shared by Strength, FatigueResistance, Evasion, MaxMovementSpeed
    private double attributeFloor = 5;

    public static BodyCoefficients defaults() {
        return new BodyCoefficients();
    }

    public double getBaseline() { return baseline; }
    public void setBaseline(double baseline) { this.baseline = baseline; }

    public double getKStrengthMuscleMass() { return kStrengthMuscleMass; }
    public void setKStrengthMuscleMass(double v) { this.kStrengthMuscleMass = v; }

    public double getKStrengthNeuromuscular() { return kStrengthNeuromuscular; }
    public void setKStrengthNeuromuscular(double v) { this.kStrengthNeuromuscular = v; }

    public double getKStrengthFiberType() { return kStrengthFiberType; }
    public void setKStrengthFiberType(double v) { this.kStrengthFiberType = v; }

    public double getKStrengthLimbRatio() { return kStrengthLimbRatio; }
    public void setKStrengthLimbRatio(double v) { this.kStrengthLimbRatio = v; }

    public double getKStrengthMuscleDistribution() { return kStrengthMuscleDistribution; }
    public void setKStrengthMuscleDistribution(double v) { this.kStrengthMuscleDistribution = v; }

    public double getKSpeedNeuromuscular() { return kSpeedNeuromuscular; }
    public void setKSpeedNeuromuscular(double v) { this.kSpeedNeuromuscular = v; }

    public double getKSpeedMuscleMass() { return kSpeedMuscleMass; }
    public void setKSpeedMuscleMass(double v) { this.kSpeedMuscleMass = v; }

    public double getKSpeedFiberType() { return kSpeedFiberType; }
    public void setKSpeedFiberType(double v) { this.kSpeedFiberType = v; }

    public double getKSpeedMassNeutral() { return kSpeedMassNeutral; }
    public void setKSpeedMassNeutral(double v) { this.kSpeedMassNeutral = v; }

    public double getKSpeedMassDivisor() { return kSpeedMassDivisor; }
    public void setKSpeedMassDivisor(double v) { this.kSpeedMassDivisor = v; }

    public double getKStaminaPoolPulmonary() { return kStaminaPoolPulmonary; }
    public void setKStaminaPoolPulmonary(double v) { this.kStaminaPoolPulmonary = v; }

    public double getKStaminaPoolCardiac() { return kStaminaPoolCardiac; }
    public void setKStaminaPoolCardiac(double v) { this.kStaminaPoolCardiac = v; }

    public double getKStaminaPoolOxygen() { return kStaminaPoolOxygen; }
    public void setKStaminaPoolOxygen(double v) { this.kStaminaPoolOxygen = v; }

    public double getKStaminaPoolFiberType() { return kStaminaPoolFiberType; }
    public void setKStaminaPoolFiberType(double v) { this.kStaminaPoolFiberType = v; }

    public double getKFatigueResistanceCardiac() { return kFatigueResistanceCardiac; }
    public void setKFatigueResistanceCardiac(double v) { this.kFatigueResistanceCardiac = v; }

    public double getKFatigueResistancePulmonary() { return kFatigueResistancePulmonary; }
    public void setKFatigueResistancePulmonary(double v) { this.kFatigueResistancePulmonary = v; }

    public double getKFatigueResistanceOxygen() { return kFatigueResistanceOxygen; }
    public void setKFatigueResistanceOxygen(double v) { this.kFatigueResistanceOxygen = v; }

    public double getKFatigueResistanceNeuromuscular() { return kFatigueResistanceNeuromuscular; }
    public void setKFatigueResistanceNeuromuscular(double v) { this.kFatigueResistanceNeuromuscular = v; }

    public double getKFatigueResistanceMassNeutral() { return kFatigueResistanceMassNeutral; }
    public void setKFatigueResistanceMassNeutral(double v) { this.kFatigueResistanceMassNeutral = v; }

    public double getKFatigueResistanceMassDivisor() { return kFatigueResistanceMassDivisor; }
    public void setKFatigueResistanceMassDivisor(double v) { this.kFatigueResistanceMassDivisor = v; }

    public double getKFatigueResistanceMuscleMass() { return kFatigueResistanceMuscleMass; }
    public void setKFatigueResistanceMuscleMass(double v) { this.kFatigueResistanceMuscleMass = v; }

    public double getKStaminaRecoveryOxygen() { return kStaminaRecoveryOxygen; }
    public void setKStaminaRecoveryOxygen(double v) { this.kStaminaRecoveryOxygen = v; }

    public double getKStaminaRecoveryPulmonary() { return kStaminaRecoveryPulmonary; }
    public void setKStaminaRecoveryPulmonary(double v) { this.kStaminaRecoveryPulmonary = v; }

    public double getKStaminaRecoveryCardiac() { return kStaminaRecoveryCardiac; }
    public void setKStaminaRecoveryCardiac(double v) { this.kStaminaRecoveryCardiac = v; }

    public double getKStaminaRecoveryFiberType() { return kStaminaRecoveryFiberType; }
    public void setKStaminaRecoveryFiberType(double v) { this.kStaminaRecoveryFiberType = v; }

    public double getKDurabilityBoneDensity() { return kDurabilityBoneDensity; }
    public void setKDurabilityBoneDensity(double v) { this.kDurabilityBoneDensity = v; }

    public double getKDurabilityMesomorphy() { return kDurabilityMesomorphy; }
    public void setKDurabilityMesomorphy(double v) { this.kDurabilityMesomorphy = v; }

    public double getKDurabilityBodyFat() { return kDurabilityBodyFat; }
    public void setKDurabilityBodyFat(double v) { this.kDurabilityBodyFat = v; }

    public double getKDurabilityFlexibility() { return kDurabilityFlexibility; }
    public void setKDurabilityFlexibility(double v) { this.kDurabilityFlexibility = v; }

    public double getKSensePerception() { return kSensePerception; }
    public void setKSensePerception(double v) { this.kSensePerception = v; }

    public double getKSenseNeuralDrive() { return kSenseNeuralDrive; }
    public void setKSenseNeuralDrive(double v) { this.kSenseNeuralDrive = v; }

    public double getKAcrobaticsAgility() { return kAcrobaticsAgility; }
    public void setKAcrobaticsAgility(double v) { this.kAcrobaticsAgility = v; }

    public double getKAcrobaticsFlexibility() { return kAcrobaticsFlexibility; }
    public void setKAcrobaticsFlexibility(double v) { this.kAcrobaticsFlexibility = v; }

    public double getKMeleeAccuracyPrecision() { return kMeleeAccuracyPrecision; }
    public void setKMeleeAccuracyPrecision(double v) { this.kMeleeAccuracyPrecision = v; }

    public double getKMeleeAccuracyAgility() { return kMeleeAccuracyAgility; }
    public void setKMeleeAccuracyAgility(double v) { this.kMeleeAccuracyAgility = v; }

    public double getKAimPrecision() { return kAimPrecision; }
    public void setKAimPrecision(double v) { this.kAimPrecision = v; }

    public double getKAimPerception() { return kAimPerception; }
    public void setKAimPerception(double v) { this.kAimPerception = v; }

    public double getKEvasionAgility() { return kEvasionAgility; }
    public void setKEvasionAgility(double v) { this.kEvasionAgility = v; }

    public double getKEvasionNeuralDrive() { return kEvasionNeuralDrive; }
    public void setKEvasionNeuralDrive(double v) { this.kEvasionNeuralDrive = v; }

    public double getKEvasionFlexibility() { return kEvasionFlexibility; }
    public void setKEvasionFlexibility(double v) { this.kEvasionFlexibility = v; }

    public double getKMaxMovementSpeedLimbRatio() { return kMaxMovementSpeedLimbRatio; }
    public void setKMaxMovementSpeedLimbRatio(double v) { this.kMaxMovementSpeedLimbRatio = v; }

    public double getKMaxMovementSpeedMuscleDistribution() { return kMaxMovementSpeedMuscleDistribution; }
    public void setKMaxMovementSpeedMuscleDistribution(double v) { this.kMaxMovementSpeedMuscleDistribution = v; }

    public double getKSymbolicMassBase() { return kSymbolicMassBase; }
    public void setKSymbolicMassBase(double v) { this.kSymbolicMassBase = v; }

    public double getKMuscleKgMultiplier() { return kMuscleKgMultiplier; }
    public void setKMuscleKgMultiplier(double v) { this.kMuscleKgMultiplier = v; }

    public double getKMuscleKgOffset() { return kMuscleKgOffset; }
    public void setKMuscleKgOffset(double v) { this.kMuscleKgOffset = v; }

    public double getKFatKgMultiplier() { return kFatKgMultiplier; }
    public void setKFatKgMultiplier(double v) { this.kFatKgMultiplier = v; }

    public double getKFrameKgBase() { return kFrameKgBase; }
    public void setKFrameKgBase(double v) { this.kFrameKgBase = v; }

    public double getKFrameKgHeightMultiplier() { return kFrameKgHeightMultiplier; }
    public void setKFrameKgHeightMultiplier(double v) { this.kFrameKgHeightMultiplier = v; }

    public double getKFrameKgDivisor() { return kFrameKgDivisor; }
    public void setKFrameKgDivisor(double v) { this.kFrameKgDivisor = v; }

    public double getKLightLoadFraction() { return kLightLoadFraction; }
    public void setKLightLoadFraction(double v) { this.kLightLoadFraction = v; }

    public double getKHeavyLoadFraction() { return kHeavyLoadFraction; }
    public void setKHeavyLoadFraction(double v) { this.kHeavyLoadFraction = v; }

    public double getKMaxCapacityDivisor() { return kMaxCapacityDivisor; }
    public void setKMaxCapacityDivisor(double v) { this.kMaxCapacityDivisor = v; }

    public double getKDragCapacityMultiplier() { return kDragCapacityMultiplier; }
    public void setKDragCapacityMultiplier(double v) { this.kDragCapacityMultiplier = v; }

    public double getKDragCapacityMassFraction() { return kDragCapacityMassFraction; }
    public void setKDragCapacityMassFraction(double v) { this.kDragCapacityMassFraction = v; }

    public double getKLoadCapacityStrengthOffset() { return kLoadCapacityStrengthOffset; }
    public void setKLoadCapacityStrengthOffset(double v) { this.kLoadCapacityStrengthOffset = v; }

    public double getAttributeFloor() { return attributeFloor; }
    public void setAttributeFloor(double attributeFloor) { this.attributeFloor = attributeFloor; }
}
