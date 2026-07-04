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
 * <p>The Load Capacity group ({@code kMaxCapacityDivisor} = 150) now reads {@code LiftStrength}
 * (Delta V4, was {@code Strength}) directly (truncated to an {@code int}, per the design
 * document) — recalibrated in rpg-12 to work off the baseline-60 attribute with no separate
 * offset, superseding the short-lived rpg-11 {@code kLoadCapacityStrengthOffset} correction.
 *
 * <p>Neutral points (5 for every 1-9 trait, 3 for {@code limbRatio}... — see each domain
 * class) are not coefficients: they are fixed scale midpoints, not tunable weights, so they
 * stay as literals inside {@link PlayableCharacter}'s formulas, matching the pre-rpg-11
 * convention (e.g. {@code muscleDistributionDeviation()}).
 */
public class BodyCoefficients {

    private double baseline = 60; // shared additive baseline for every attribute formula

    // Mean Strength (Delta V4) — hidden engine, never exposed via any DTO. Replaces the old
    // global Strength's three core terms (LimbRatio/MuscleDistribution/Tendons moved to the
    // four specialized strengths below).
    private double kMeanStrengthMuscleMass = 4;
    private double kMeanStrengthNeuromuscular = 2;
    private double kMeanStrengthFiberType = 1;

    // 4 specialized strengths (Delta V4) — each anchored on the hidden Mean Strength, not the
    // baseline directly. Height neutral is 7, matching Genetics.height's own neutral.
    private double kPushStrengthLimbRatio = 2;
    private double kPushStrengthMuscleDistribution = 1;
    private double kPushStrengthTendons = 1;
    private double kPushStrengthHeight = 0.5;
    private double kLegDriveLimbRatio = 2;
    private double kLegDriveMuscleDistribution = 1; // applied as (5 - MuscleDistribution)
    private double kLegDriveTendons = 1;
    private double kLegDriveHeight = 0.5;
    private double kGripStrengthMuscleDistribution = 1;
    private double kGripStrengthTendons = 2;
    private double kLiftStrengthLimbRatio = 2; // subtracted
    private double kLiftStrengthTendons = 1;

    // FatGainRate / MuscleGainRate (rpg-14) — zero-baseline rate attributes, not baseline 60
    private double kFatGainRateEndomorphy = 1.0;
    private double kFatGainRateEctomorphy = 1.0;
    private double kFatGainRateDigestiveAbsorption = 1.0; // renamed from kFatGainRateNutrientAbsorption, Delta V4
    private double kFatGainRateKetosis = 1.0;
    private double kFatGainRateCellularHealth = 0.5;
    private double kMuscleGainRateMesomorphy = 1.0;
    private double kMuscleGainRateEctomorphy = 1.0;
    private double kMuscleGainRateDigestiveAbsorption = 1.0; // renamed from kMuscleGainRateNutrientAbsorption, Delta V4
    private double kMuscleGainRateTmod = 1.0;

    // Intimidation / Diplomacy / Enfactuation / Command (rpg-14) — social attributes
    private double kIntimidationShapeAesthetics = 5;
    private double kIntimidationTmod = 5;
    private double kIntimidationMass = 2;
    private double kIntimidationMassNeutral = 25; // SymbolicTotalMass neutral point, not the attribute baseline
    private double kDiplomacyShapeAesthetics = 7;
    private double kDiplomacyPmod = 3;
    private double kEnfactuationShapeAesthetics = 7;
    private double kEnfactuationPmod = 3;
    private double kCommandShapeAesthetics = 10;

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
    private double kStaminaPoolDigestiveAbsorption = 2; // renamed from kStaminaPoolNutrientAbsorption, Delta V4

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
    private double kDurabilitySkin = 1; // added rpg-14

    // Sight / Hearing / Smell (rpg-14: diverged from one shared formula to one per sense;
    // Delta V4: Hippocampus term swapped for Thalamus on all three, same weights)
    private double kSightEyesSensitivity = 6;
    private double kSightThalamus = 1; // renamed from kSightHippocampus, Delta V4
    private double kSightNeuralDrive = 1;
    private double kSightPmod = 2;
    private double kHearingEarsSensitivity = 6;
    private double kHearingThalamus = 1; // renamed from kHearingHippocampus, Delta V4
    private double kHearingNeuralDrive = 1;
    private double kHearingPmod = 2;
    private double kSmellNoseSensitivity = 6;
    private double kSmellThalamus = 1; // renamed from kSmellHippocampus, Delta V4
    private double kSmellNeuralDrive = 1;
    private double kSmellPmod = 2;

    // Acrobatics
    private double kAcrobaticsAgility = 2;
    private double kAcrobaticsFlexibility = 2;

    // MeleeAccuracy
    private double kMeleeAccuracyPrecision = 3;
    private double kMeleeAccuracyAgility = 1;

    // Aim (Delta V4: Precision reweighted, Hippocampus swapped for Thalamus and reweighted)
    private double kAimPrecision = 5; // was 3
    private double kAimThalamus = 3; // renamed from kAimPerception (read Hippocampus), was 1

    // Evasion (extends Speed)
    private double kEvasionAgility = 2;
    private double kEvasionNeuralDrive = 1;
    private double kEvasionFlexibility = 1;

    // MaxMovementSpeed (extends Speed)
    private double kMaxMovementSpeedLimbRatio = 2;
    private double kMaxMovementSpeedMuscleDistribution = 1;
    private double kMaxMovementSpeedHeight = 0.5; // added Delta V4

    // Mass model
    private double kSymbolicMassBase = 10;
    private double kMuscleKgMultiplier = 5;
    private double kMuscleKgOffset = 5;
    private double kFatKgMultiplier = 5;
    private double kFrameKgBase = 16;
    private double kFrameKgHeightMultiplier = 3;
    private double kFrameKgDivisor = 2;

    // Load capacity — now reads LiftStrength (Delta V4, was Strength)
    private double kMaxCapacityDivisor = 150;
    private double kLightLoadDivisor = 3;
    private double kHeavyLoadMultiplier = 2;
    private double kHeavyLoadDivisor = 3;
    private double kDragCapacityMultiplier = 2;
    private double kDragCapacityMassFraction = 0.5;

    // Cognitive/Mental (rpg-13, Memory/ShortMemory reweighted Delta V4)
    private double kMemoryPoolCerebral = 6; // was 8
    private double kMemoryPoolHippocampus = 4; // was 2
    private double kReasoningSynapsis = 10;
    private double kShortMemoryCerebral = 3; // was 4
    private double kShortMemorySynapsis = 3; // was 4
    private double kShortMemoryHippocampus = 4; // was 2
    private double kMentalHealthAmygdala = 5; // shared by MentalHealthPool and Will; reweighted 10->5 in rpg-14
    private double kMentalHealthTmod = 5; // added rpg-14
    private double kMentalHealthPmod = 5; // added rpg-14

    // Sensory / Hormonal / Stress (rpg-13, recalibrated rpg-14, Balance rebuilt Delta V4)
    private double kBalanceThalamus = 4; // added Delta V4 (replaces kBalanceHippocampus)
    private double kBalanceNeuralDrive = 1;
    private double kBalanceLegDrive = 0.2; // added Delta V4 — applied as (LegDrive - 60)
    private double kStressResistanceAmygdala = 5;
    private double kStressResistanceAdrenal = 5;

    // Biological defense (rpg-13, cellular health term added rpg-14, digestive term Delta V4)
    private double kPoisonResistanceImmunity = 5;
    private double kPoisonResistanceCardiac = 3;
    private double kPoisonResistanceBloodThickness = 4;
    private double kPoisonResistanceCellularHealth = 2; // added rpg-14
    private double kDiseaseResistanceImmunity = 9;
    private double kDiseaseResistanceAmygdala = 1;
    private double kDiseaseResistanceCellularHealth = 2; // added rpg-14
    private double kBleedingResistanceBloodThickness = 10;
    private double kBleedingResistanceCardiac = 5;

    // Anger / Fear Resistance, Pain Threshold (Delta V4 — new attributes)
    private double kAngerResistanceAmygdala = 10;
    private double kFearResistanceAmygdala = 10;
    private double kPainThresholdBodyFat = 3;
    private double kPainThresholdSkin = 3;
    private double kPainThresholdAmygdala = 4;

    // Metabolic / survival (rpg-13)
    private double kThermalResistanceSkin = 5;
    private double kThermalResistanceBodyFat = 2;
    private double kThermalResistanceHypothalamus = 1;
    private double kBreathOutputPulmonary = 10;
    private double kDehydrationResistanceHypothalamus = 5;
    private double kDehydrationResistanceKetosis = 5;
    private double kStarvationResistanceHypothalamus = 4;
    private double kStarvationResistanceDigestiveAbsorption = 3; // renamed from kStarvationResistanceNutrient, Delta V4
    private double kStarvationResistanceKetosis = 3;
    private double kFoodPoisoningImpurity = 5;
    private double kFoodPoisoningImmunity = 5;
    private double kFoodPoisoningCellularHealth = 2; // added rpg-14
    private double kFoodPoisoningDigestiveAbsorption = 1; // added Delta V4 — subtracted (light penalty)

    // Arcane organs — magical races only, absent (0) for the human default template. Each reads
    // a single input around a neutral point of 6 (not the usual 5), with a wider weight (8).
    private double kManaPoolEpiphyseal = 8;
    private double kArcaneOutputVentriculum = 8;
    private double kSixthSenseNoeticPlexus = 8;

    // Safety floor shared by Strength-family (now Push/Leg/Grip/Lift Strength), FatigueResistance,
    // Evasion, MaxMovementSpeed
    private double attributeFloor = 5;

    public static BodyCoefficients defaults() {
        return new BodyCoefficients();
    }

    public double getBaseline() { return baseline; }
    public void setBaseline(double baseline) { this.baseline = baseline; }

    public double getKMeanStrengthMuscleMass() { return kMeanStrengthMuscleMass; }
    public void setKMeanStrengthMuscleMass(double v) { this.kMeanStrengthMuscleMass = v; }

    public double getKMeanStrengthNeuromuscular() { return kMeanStrengthNeuromuscular; }
    public void setKMeanStrengthNeuromuscular(double v) { this.kMeanStrengthNeuromuscular = v; }

    public double getKMeanStrengthFiberType() { return kMeanStrengthFiberType; }
    public void setKMeanStrengthFiberType(double v) { this.kMeanStrengthFiberType = v; }

    public double getKPushStrengthLimbRatio() { return kPushStrengthLimbRatio; }
    public void setKPushStrengthLimbRatio(double v) { this.kPushStrengthLimbRatio = v; }

    public double getKPushStrengthMuscleDistribution() { return kPushStrengthMuscleDistribution; }
    public void setKPushStrengthMuscleDistribution(double v) { this.kPushStrengthMuscleDistribution = v; }

    public double getKPushStrengthTendons() { return kPushStrengthTendons; }
    public void setKPushStrengthTendons(double v) { this.kPushStrengthTendons = v; }

    public double getKPushStrengthHeight() { return kPushStrengthHeight; }
    public void setKPushStrengthHeight(double v) { this.kPushStrengthHeight = v; }

    public double getKLegDriveLimbRatio() { return kLegDriveLimbRatio; }
    public void setKLegDriveLimbRatio(double v) { this.kLegDriveLimbRatio = v; }

    public double getKLegDriveMuscleDistribution() { return kLegDriveMuscleDistribution; }
    public void setKLegDriveMuscleDistribution(double v) { this.kLegDriveMuscleDistribution = v; }

    public double getKLegDriveTendons() { return kLegDriveTendons; }
    public void setKLegDriveTendons(double v) { this.kLegDriveTendons = v; }

    public double getKLegDriveHeight() { return kLegDriveHeight; }
    public void setKLegDriveHeight(double v) { this.kLegDriveHeight = v; }

    public double getKGripStrengthMuscleDistribution() { return kGripStrengthMuscleDistribution; }
    public void setKGripStrengthMuscleDistribution(double v) { this.kGripStrengthMuscleDistribution = v; }

    public double getKGripStrengthTendons() { return kGripStrengthTendons; }
    public void setKGripStrengthTendons(double v) { this.kGripStrengthTendons = v; }

    public double getKLiftStrengthLimbRatio() { return kLiftStrengthLimbRatio; }
    public void setKLiftStrengthLimbRatio(double v) { this.kLiftStrengthLimbRatio = v; }

    public double getKLiftStrengthTendons() { return kLiftStrengthTendons; }
    public void setKLiftStrengthTendons(double v) { this.kLiftStrengthTendons = v; }

    public double getKFatGainRateEndomorphy() { return kFatGainRateEndomorphy; }
    public void setKFatGainRateEndomorphy(double v) { this.kFatGainRateEndomorphy = v; }

    public double getKFatGainRateEctomorphy() { return kFatGainRateEctomorphy; }
    public void setKFatGainRateEctomorphy(double v) { this.kFatGainRateEctomorphy = v; }

    public double getKFatGainRateDigestiveAbsorption() { return kFatGainRateDigestiveAbsorption; }
    public void setKFatGainRateDigestiveAbsorption(double v) { this.kFatGainRateDigestiveAbsorption = v; }

    public double getKFatGainRateKetosis() { return kFatGainRateKetosis; }
    public void setKFatGainRateKetosis(double v) { this.kFatGainRateKetosis = v; }

    public double getKFatGainRateCellularHealth() { return kFatGainRateCellularHealth; }
    public void setKFatGainRateCellularHealth(double v) { this.kFatGainRateCellularHealth = v; }

    public double getKMuscleGainRateMesomorphy() { return kMuscleGainRateMesomorphy; }
    public void setKMuscleGainRateMesomorphy(double v) { this.kMuscleGainRateMesomorphy = v; }

    public double getKMuscleGainRateEctomorphy() { return kMuscleGainRateEctomorphy; }
    public void setKMuscleGainRateEctomorphy(double v) { this.kMuscleGainRateEctomorphy = v; }

    public double getKMuscleGainRateDigestiveAbsorption() { return kMuscleGainRateDigestiveAbsorption; }
    public void setKMuscleGainRateDigestiveAbsorption(double v) { this.kMuscleGainRateDigestiveAbsorption = v; }

    public double getKMuscleGainRateTmod() { return kMuscleGainRateTmod; }
    public void setKMuscleGainRateTmod(double v) { this.kMuscleGainRateTmod = v; }

    public double getKIntimidationShapeAesthetics() { return kIntimidationShapeAesthetics; }
    public void setKIntimidationShapeAesthetics(double v) { this.kIntimidationShapeAesthetics = v; }

    public double getKIntimidationTmod() { return kIntimidationTmod; }
    public void setKIntimidationTmod(double v) { this.kIntimidationTmod = v; }

    public double getKIntimidationMass() { return kIntimidationMass; }
    public void setKIntimidationMass(double v) { this.kIntimidationMass = v; }

    public double getKIntimidationMassNeutral() { return kIntimidationMassNeutral; }
    public void setKIntimidationMassNeutral(double v) { this.kIntimidationMassNeutral = v; }

    public double getKDiplomacyShapeAesthetics() { return kDiplomacyShapeAesthetics; }
    public void setKDiplomacyShapeAesthetics(double v) { this.kDiplomacyShapeAesthetics = v; }

    public double getKDiplomacyPmod() { return kDiplomacyPmod; }
    public void setKDiplomacyPmod(double v) { this.kDiplomacyPmod = v; }

    public double getKEnfactuationShapeAesthetics() { return kEnfactuationShapeAesthetics; }
    public void setKEnfactuationShapeAesthetics(double v) { this.kEnfactuationShapeAesthetics = v; }

    public double getKEnfactuationPmod() { return kEnfactuationPmod; }
    public void setKEnfactuationPmod(double v) { this.kEnfactuationPmod = v; }

    public double getKCommandShapeAesthetics() { return kCommandShapeAesthetics; }
    public void setKCommandShapeAesthetics(double v) { this.kCommandShapeAesthetics = v; }

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

    public double getKStaminaPoolDigestiveAbsorption() { return kStaminaPoolDigestiveAbsorption; }
    public void setKStaminaPoolDigestiveAbsorption(double v) { this.kStaminaPoolDigestiveAbsorption = v; }

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

    public double getKDurabilitySkin() { return kDurabilitySkin; }
    public void setKDurabilitySkin(double v) { this.kDurabilitySkin = v; }

    public double getKSightEyesSensitivity() { return kSightEyesSensitivity; }
    public void setKSightEyesSensitivity(double v) { this.kSightEyesSensitivity = v; }

    public double getKSightThalamus() { return kSightThalamus; }
    public void setKSightThalamus(double v) { this.kSightThalamus = v; }

    public double getKSightNeuralDrive() { return kSightNeuralDrive; }
    public void setKSightNeuralDrive(double v) { this.kSightNeuralDrive = v; }

    public double getKSightPmod() { return kSightPmod; }
    public void setKSightPmod(double v) { this.kSightPmod = v; }

    public double getKHearingEarsSensitivity() { return kHearingEarsSensitivity; }
    public void setKHearingEarsSensitivity(double v) { this.kHearingEarsSensitivity = v; }

    public double getKHearingThalamus() { return kHearingThalamus; }
    public void setKHearingThalamus(double v) { this.kHearingThalamus = v; }

    public double getKHearingNeuralDrive() { return kHearingNeuralDrive; }
    public void setKHearingNeuralDrive(double v) { this.kHearingNeuralDrive = v; }

    public double getKHearingPmod() { return kHearingPmod; }
    public void setKHearingPmod(double v) { this.kHearingPmod = v; }

    public double getKSmellNoseSensitivity() { return kSmellNoseSensitivity; }
    public void setKSmellNoseSensitivity(double v) { this.kSmellNoseSensitivity = v; }

    public double getKSmellThalamus() { return kSmellThalamus; }
    public void setKSmellThalamus(double v) { this.kSmellThalamus = v; }

    public double getKSmellNeuralDrive() { return kSmellNeuralDrive; }
    public void setKSmellNeuralDrive(double v) { this.kSmellNeuralDrive = v; }

    public double getKSmellPmod() { return kSmellPmod; }
    public void setKSmellPmod(double v) { this.kSmellPmod = v; }

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

    public double getKAimThalamus() { return kAimThalamus; }
    public void setKAimThalamus(double v) { this.kAimThalamus = v; }

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

    public double getKMaxMovementSpeedHeight() { return kMaxMovementSpeedHeight; }
    public void setKMaxMovementSpeedHeight(double v) { this.kMaxMovementSpeedHeight = v; }

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

    public double getKMentalHealthTmod() { return kMentalHealthTmod; }
    public void setKMentalHealthTmod(double v) { this.kMentalHealthTmod = v; }

    public double getKMentalHealthPmod() { return kMentalHealthPmod; }
    public void setKMentalHealthPmod(double v) { this.kMentalHealthPmod = v; }

    public double getKBalanceThalamus() { return kBalanceThalamus; }
    public void setKBalanceThalamus(double v) { this.kBalanceThalamus = v; }

    public double getKBalanceNeuralDrive() { return kBalanceNeuralDrive; }
    public void setKBalanceNeuralDrive(double v) { this.kBalanceNeuralDrive = v; }

    public double getKBalanceLegDrive() { return kBalanceLegDrive; }
    public void setKBalanceLegDrive(double v) { this.kBalanceLegDrive = v; }

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

    public double getKPoisonResistanceCellularHealth() { return kPoisonResistanceCellularHealth; }
    public void setKPoisonResistanceCellularHealth(double v) { this.kPoisonResistanceCellularHealth = v; }

    public double getKDiseaseResistanceImmunity() { return kDiseaseResistanceImmunity; }
    public void setKDiseaseResistanceImmunity(double v) { this.kDiseaseResistanceImmunity = v; }

    public double getKDiseaseResistanceAmygdala() { return kDiseaseResistanceAmygdala; }
    public void setKDiseaseResistanceAmygdala(double v) { this.kDiseaseResistanceAmygdala = v; }

    public double getKDiseaseResistanceCellularHealth() { return kDiseaseResistanceCellularHealth; }
    public void setKDiseaseResistanceCellularHealth(double v) { this.kDiseaseResistanceCellularHealth = v; }

    public double getKBleedingResistanceBloodThickness() { return kBleedingResistanceBloodThickness; }
    public void setKBleedingResistanceBloodThickness(double v) { this.kBleedingResistanceBloodThickness = v; }

    public double getKBleedingResistanceCardiac() { return kBleedingResistanceCardiac; }
    public void setKBleedingResistanceCardiac(double v) { this.kBleedingResistanceCardiac = v; }

    public double getKAngerResistanceAmygdala() { return kAngerResistanceAmygdala; }
    public void setKAngerResistanceAmygdala(double v) { this.kAngerResistanceAmygdala = v; }

    public double getKFearResistanceAmygdala() { return kFearResistanceAmygdala; }
    public void setKFearResistanceAmygdala(double v) { this.kFearResistanceAmygdala = v; }

    public double getKPainThresholdBodyFat() { return kPainThresholdBodyFat; }
    public void setKPainThresholdBodyFat(double v) { this.kPainThresholdBodyFat = v; }

    public double getKPainThresholdSkin() { return kPainThresholdSkin; }
    public void setKPainThresholdSkin(double v) { this.kPainThresholdSkin = v; }

    public double getKPainThresholdAmygdala() { return kPainThresholdAmygdala; }
    public void setKPainThresholdAmygdala(double v) { this.kPainThresholdAmygdala = v; }

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

    public double getKStarvationResistanceDigestiveAbsorption() { return kStarvationResistanceDigestiveAbsorption; }
    public void setKStarvationResistanceDigestiveAbsorption(double v) { this.kStarvationResistanceDigestiveAbsorption = v; }

    public double getKStarvationResistanceKetosis() { return kStarvationResistanceKetosis; }
    public void setKStarvationResistanceKetosis(double v) { this.kStarvationResistanceKetosis = v; }

    public double getKFoodPoisoningImpurity() { return kFoodPoisoningImpurity; }
    public void setKFoodPoisoningImpurity(double v) { this.kFoodPoisoningImpurity = v; }

    public double getKFoodPoisoningImmunity() { return kFoodPoisoningImmunity; }
    public void setKFoodPoisoningImmunity(double v) { this.kFoodPoisoningImmunity = v; }

    public double getKFoodPoisoningCellularHealth() { return kFoodPoisoningCellularHealth; }
    public void setKFoodPoisoningCellularHealth(double v) { this.kFoodPoisoningCellularHealth = v; }

    public double getKFoodPoisoningDigestiveAbsorption() { return kFoodPoisoningDigestiveAbsorption; }
    public void setKFoodPoisoningDigestiveAbsorption(double v) { this.kFoodPoisoningDigestiveAbsorption = v; }

    public double getKManaPoolEpiphyseal() { return kManaPoolEpiphyseal; }
    public void setKManaPoolEpiphyseal(double v) { this.kManaPoolEpiphyseal = v; }

    public double getKArcaneOutputVentriculum() { return kArcaneOutputVentriculum; }
    public void setKArcaneOutputVentriculum(double v) { this.kArcaneOutputVentriculum = v; }

    public double getKSixthSenseNoeticPlexus() { return kSixthSenseNoeticPlexus; }
    public void setKSixthSenseNoeticPlexus(double v) { this.kSixthSenseNoeticPlexus = v; }

    public double getAttributeFloor() { return attributeFloor; }
    public void setAttributeFloor(double attributeFloor) { this.attributeFloor = attributeFloor; }
}
