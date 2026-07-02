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
 * <p>The Load Capacity group ({@code kMaxCapacityDivisor} = 150) reads {@code Strength}
 * directly (truncated to an {@code int}, per the design document) — recalibrated in rpg-12
 * to work off the baseline-60 {@code Strength} with no separate offset, superseding the
 * short-lived rpg-11 {@code kLoadCapacityStrengthOffset} correction.
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
    private double kStaminaPoolNutrientAbsorption = 2; // added rpg-13

    // FatigueResistance
    private double kFatigueResistanceCardiac = 3;
    private double kFatigueResistancePulmonary = 1;
    private double kFatigueResistanceOxygen = 1;
    private double kFatigueResistanceNeuromuscular = 2;
    private double kFatigueResistanceMassNeutral = 25;
    private double kFatigueResistanceMassDivisor = 2;
    private double kFatigueResistanceMuscleMass = 1;
    private double kFatigueResistanceHypothalamus = 1; // added rpg-13
    private double kFatigueResistanceThyroid = 2; // added rpg-13

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
    private double kMaxCapacityDivisor = 150;
    private double kLightLoadDivisor = 3;
    private double kHeavyLoadMultiplier = 2;
    private double kHeavyLoadDivisor = 3;
    private double kDragCapacityMultiplier = 2;
    private double kDragCapacityMassFraction = 0.5;

    // Cognitive/Mental (rpg-13)
    private double kMemoryPoolCerebral = 8;
    private double kMemoryPoolHippocampus = 2;
    private double kReasoningSynapsis = 10;
    private double kShortMemoryCerebral = 4;
    private double kShortMemorySynapsis = 4;
    private double kShortMemoryHippocampus = 2;
    private double kMentalHealthAmygdala = 10; // shared by MentalHealthPool and Will

    // Sensory / Hormonal / Stress (rpg-13)
    private double kBalanceHippocampus = 3;
    private double kBalanceNeuralDrive = 1;
    private double kStressResistanceAmygdala = 5;
    private double kStressResistanceAdrenal = 5;

    // Biological defense (rpg-13)
    private double kPoisonResistanceImmunity = 5;
    private double kPoisonResistanceCardiac = 3;
    private double kPoisonResistanceBloodThickness = 4;
    private double kDiseaseResistanceImmunity = 9;
    private double kDiseaseResistanceAmygdala = 1;
    private double kBleedingResistanceBloodThickness = 10;
    private double kBleedingResistanceCardiac = 5;

    // Metabolic / survival (rpg-13)
    private double kThermalResistanceSkin = 5;
    private double kThermalResistanceBodyFat = 2;
    private double kThermalResistanceHypothalamus = 1;
    private double kBreathOutputPulmonary = 10;
    private double kDehydrationResistanceHypothalamus = 5;
    private double kDehydrationResistanceKetosis = 5;
    private double kStarvationResistanceHypothalamus = 4;
    private double kStarvationResistanceNutrient = 3;
    private double kStarvationResistanceKetosis = 3;
    private double kFoodPoisoningImpurity = 5;
    private double kFoodPoisoningImmunity = 5;

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

    public double getKStaminaPoolNutrientAbsorption() { return kStaminaPoolNutrientAbsorption; }
    public void setKStaminaPoolNutrientAbsorption(double v) { this.kStaminaPoolNutrientAbsorption = v; }

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

    public double getKFatigueResistanceHypothalamus() { return kFatigueResistanceHypothalamus; }
    public void setKFatigueResistanceHypothalamus(double v) { this.kFatigueResistanceHypothalamus = v; }

    public double getKFatigueResistanceThyroid() { return kFatigueResistanceThyroid; }
    public void setKFatigueResistanceThyroid(double v) { this.kFatigueResistanceThyroid = v; }

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

    public double getKMaxCapacityDivisor() { return kMaxCapacityDivisor; }
    public void setKMaxCapacityDivisor(double v) { this.kMaxCapacityDivisor = v; }

    public double getKLightLoadDivisor() { return kLightLoadDivisor; }
    public void setKLightLoadDivisor(double v) { this.kLightLoadDivisor = v; }

    public double getKHeavyLoadMultiplier() { return kHeavyLoadMultiplier; }
    public void setKHeavyLoadMultiplier(double v) { this.kHeavyLoadMultiplier = v; }

    public double getKHeavyLoadDivisor() { return kHeavyLoadDivisor; }
    public void setKHeavyLoadDivisor(double v) { this.kHeavyLoadDivisor = v; }

    public double getKDragCapacityMultiplier() { return kDragCapacityMultiplier; }
    public void setKDragCapacityMultiplier(double v) { this.kDragCapacityMultiplier = v; }

    public double getKDragCapacityMassFraction() { return kDragCapacityMassFraction; }
    public void setKDragCapacityMassFraction(double v) { this.kDragCapacityMassFraction = v; }

    public double getKMemoryPoolCerebral() { return kMemoryPoolCerebral; }
    public void setKMemoryPoolCerebral(double v) { this.kMemoryPoolCerebral = v; }

    public double getKMemoryPoolHippocampus() { return kMemoryPoolHippocampus; }
    public void setKMemoryPoolHippocampus(double v) { this.kMemoryPoolHippocampus = v; }

    public double getKReasoningSynapsis() { return kReasoningSynapsis; }
    public void setKReasoningSynapsis(double v) { this.kReasoningSynapsis = v; }

    public double getKShortMemoryCerebral() { return kShortMemoryCerebral; }
    public void setKShortMemoryCerebral(double v) { this.kShortMemoryCerebral = v; }

    public double getKShortMemorySynapsis() { return kShortMemorySynapsis; }
    public void setKShortMemorySynapsis(double v) { this.kShortMemorySynapsis = v; }

    public double getKShortMemoryHippocampus() { return kShortMemoryHippocampus; }
    public void setKShortMemoryHippocampus(double v) { this.kShortMemoryHippocampus = v; }

    public double getKMentalHealthAmygdala() { return kMentalHealthAmygdala; }
    public void setKMentalHealthAmygdala(double v) { this.kMentalHealthAmygdala = v; }

    public double getKBalanceHippocampus() { return kBalanceHippocampus; }
    public void setKBalanceHippocampus(double v) { this.kBalanceHippocampus = v; }

    public double getKBalanceNeuralDrive() { return kBalanceNeuralDrive; }
    public void setKBalanceNeuralDrive(double v) { this.kBalanceNeuralDrive = v; }

    public double getKStressResistanceAmygdala() { return kStressResistanceAmygdala; }
    public void setKStressResistanceAmygdala(double v) { this.kStressResistanceAmygdala = v; }

    public double getKStressResistanceAdrenal() { return kStressResistanceAdrenal; }
    public void setKStressResistanceAdrenal(double v) { this.kStressResistanceAdrenal = v; }

    public double getKPoisonResistanceImmunity() { return kPoisonResistanceImmunity; }
    public void setKPoisonResistanceImmunity(double v) { this.kPoisonResistanceImmunity = v; }

    public double getKPoisonResistanceCardiac() { return kPoisonResistanceCardiac; }
    public void setKPoisonResistanceCardiac(double v) { this.kPoisonResistanceCardiac = v; }

    public double getKPoisonResistanceBloodThickness() { return kPoisonResistanceBloodThickness; }
    public void setKPoisonResistanceBloodThickness(double v) { this.kPoisonResistanceBloodThickness = v; }

    public double getKDiseaseResistanceImmunity() { return kDiseaseResistanceImmunity; }
    public void setKDiseaseResistanceImmunity(double v) { this.kDiseaseResistanceImmunity = v; }

    public double getKDiseaseResistanceAmygdala() { return kDiseaseResistanceAmygdala; }
    public void setKDiseaseResistanceAmygdala(double v) { this.kDiseaseResistanceAmygdala = v; }

    public double getKBleedingResistanceBloodThickness() { return kBleedingResistanceBloodThickness; }
    public void setKBleedingResistanceBloodThickness(double v) { this.kBleedingResistanceBloodThickness = v; }

    public double getKBleedingResistanceCardiac() { return kBleedingResistanceCardiac; }
    public void setKBleedingResistanceCardiac(double v) { this.kBleedingResistanceCardiac = v; }

    public double getKThermalResistanceSkin() { return kThermalResistanceSkin; }
    public void setKThermalResistanceSkin(double v) { this.kThermalResistanceSkin = v; }

    public double getKThermalResistanceBodyFat() { return kThermalResistanceBodyFat; }
    public void setKThermalResistanceBodyFat(double v) { this.kThermalResistanceBodyFat = v; }

    public double getKThermalResistanceHypothalamus() { return kThermalResistanceHypothalamus; }
    public void setKThermalResistanceHypothalamus(double v) { this.kThermalResistanceHypothalamus = v; }

    public double getKBreathOutputPulmonary() { return kBreathOutputPulmonary; }
    public void setKBreathOutputPulmonary(double v) { this.kBreathOutputPulmonary = v; }

    public double getKDehydrationResistanceHypothalamus() { return kDehydrationResistanceHypothalamus; }
    public void setKDehydrationResistanceHypothalamus(double v) { this.kDehydrationResistanceHypothalamus = v; }

    public double getKDehydrationResistanceKetosis() { return kDehydrationResistanceKetosis; }
    public void setKDehydrationResistanceKetosis(double v) { this.kDehydrationResistanceKetosis = v; }

    public double getKStarvationResistanceHypothalamus() { return kStarvationResistanceHypothalamus; }
    public void setKStarvationResistanceHypothalamus(double v) { this.kStarvationResistanceHypothalamus = v; }

    public double getKStarvationResistanceNutrient() { return kStarvationResistanceNutrient; }
    public void setKStarvationResistanceNutrient(double v) { this.kStarvationResistanceNutrient = v; }

    public double getKStarvationResistanceKetosis() { return kStarvationResistanceKetosis; }
    public void setKStarvationResistanceKetosis(double v) { this.kStarvationResistanceKetosis = v; }

    public double getKFoodPoisoningImpurity() { return kFoodPoisoningImpurity; }
    public void setKFoodPoisoningImpurity(double v) { this.kFoodPoisoningImpurity = v; }

    public double getKFoodPoisoningImmunity() { return kFoodPoisoningImmunity; }
    public void setKFoodPoisoningImmunity(double v) { this.kFoodPoisoningImmunity = v; }

    public double getAttributeFloor() { return attributeFloor; }
    public void setAttributeFloor(double attributeFloor) { this.attributeFloor = attributeFloor; }
}
