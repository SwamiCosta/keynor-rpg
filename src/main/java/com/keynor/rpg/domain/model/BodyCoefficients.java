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

    // Soft Tissue Durability / Bone Durability (rpg-21, replaces the unified Durability outright)
    private double softTissueDurabilityBaseline = 10; // unlike every other attribute, not the shared 60
    private double kSoftTissueDurabilityMesomorphy = 1;
    private double kSoftTissueDurabilityBodyFat = 1;
    private double kSoftTissueDurabilityFlexibility = 1;
    private double kSoftTissueDurabilitySkin = 1;
    private double kSoftTissueDurabilityResilience = 2;
    private double kBoneDurabilityBoneDensity = 2;

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

    // MovementSpeed (extends Speed)
    private double kMovementSpeedLimbRatio = 2;
    private double kMovementSpeedMuscleDistribution = 1;
    private double kMovementSpeedHeight = 0.5; // added Delta V4

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
    private double kMediunityNoeticPlexus = 8;

    // Mind pillar — new attributes (Knowledge + Values), baseline 60 unless noted
    private double kSurvivalSkillsEcology = 2;
    private double kAnimalCaringEcology = 2;
    private double kAnimalCaringBiology = 2;
    private double kDiscretionShapeAesthetics = 10; // same magnitude as kCommandShapeAesthetics, sign inverted

    // rpg-19 — Values-trait bonuses (the 28-trait catalog introduced alongside Knowledge
    // sliders and the Labours tab). The rpg-18 cross-pillar terms these traits replaced
    // (kShortMemoryKnowledge, kReasoningTruth, kEnfactuationLoyalty, kWillMorality,
    // kBluffingTruth, kBluffingMorality, kFaithDivinity, kIllusionResistanceSanityTruth,
    // kCreativityProgress) were deleted outright, not kept as dead fields.
    private double kFearResistanceSelfSacrifice = 4;
    private double kFearResistanceSuicidal = 4;
    private double kPainThresholdSelfSacrifice = 8;
    private double kDiscretionLoneWolf = 8;
    private double kDiscretionBackstabber = 8;
    private double kCommandDominant = 4;
    private double kCommandPossessive = 4;
    private double kManipulationDominant = 4;
    private double kManipulationPossessive = 4;
    private double kManipulationRelativist = 4;
    private double kSurvivalSkillsExpatriated = 10;
    private double kSurvivalSkillsAnarchist = 10;
    private double kMediunityPagan = 5;
    private double kFaithPagan = 10;
    private double kFaithRelativist = 4;
    private double kFaithProfane = 5;
    private double kIntimidationProfane = 4;
    private double kIntimidationBellicose = 6;
    private double kWillRelativist = 4;
    private double kWillPracticalist = 4;
    private double kWillNihilist = 10;
    private double kEnfactuationRelativist = 4;
    private double kEnfactuationBellicose = 4;
    private double kReasoningRelativist = 5;
    private double kReasoningIliterate = 5;
    private double kIllusionResistanceRelativist = 5;
    private double kIllusionResistancePracticalist = 5;
    private double kAngerResistancePracticalist = 4;
    private double kAngerResistanceBellicose = 3;
    private double kMentalHealthPracticalist = 4;
    private double kMentalHealthNihilist = 15;
    private double kMemoryPoolIliterate = 20;
    private double kMemoryPoolPastEraser = 5;
    private double kAnimalCaringAntiNaturalist = 5;
    private double kPoisonResistanceAntiNaturalist = 2;
    private double kFoodPoisoningAntiNaturalist = 2;
    private double kDiseaseResistanceAntiNaturalist = 6;
    private double kCreativityOrphanMind = 5;
    private double kCreativityPastEraser = 5;
    private double kBehaviorReadingDogEatDog = 5;
    private double kAnalysisReasoning = 0.5;
    private double kAnalysisDogEatDog = 5;
    private double kMeleeAccuracyDogEatDog = 5;
    private double kAimDogEatDog = 5;
    private double kArcaneOutputConservative = 5;
    private double kManaPoolConservative = 5;
    private double kCloseCombatBellicose = 4;
    private double kLowRangeCombatBellicose = 4;

    // Psyquism Output / Psyquism Defense — Phaxic Cerebelum, another organ absent (0) for the
    // human default template, neutral point 6 like the arcane organs above.
    private double kPsyquismOutputPhaxicCerebelum = 8;
    private double kPsyquismOutputCerebralCapacity = 1;
    private double kPsyquismDefensePhaxicCerebelum = 8;

    // Charm Resistance / Concentration / Purity — new attributes alongside GeneralPersonality
    // (Vanity/Focus). CharmResistance reads Discretion (already-resolved attribute) as a term,
    // same pattern as Balance's LegDrive term and Analysis's Reasoning term.
    private double kCharmResistanceVanity = 3;
    private double kCharmResistanceDiscretion = 0.5;
    private double kCharmResistanceProtagonist = 3;
    private double kConcentrationFocus = 4;
    private double kConcentrationCerebralCapacity = 1;
    private double kPurityCleanVessel = 6;

    // Vanity modifiers on pre-existing social attributes
    private double kEnfactuationVanity = 2;
    private double kIntimidationVanity = 2;

    // New Values-trait bonuses (12-trait follow-up to the rpg-19 catalog)
    private double kEnfactuationReliable = 6;
    private double kEnfactuationPeacekeeper = 6;
    private double kIntimidationPeacekeeper = 3;
    private double kFaithReligionPractitioner = 6;
    private double kIllusionResistanceRealitic = 6;
    private double kBluffingRealitic = 3;
    private double kReasoningPhilosopher = 6;
    private double kSurvivalSkillsOutdoorLifestyle = 6;
    private double kAnimalCaringOutdoorLifestyle = 6;
    private double kCreativityInventor = 6;

    // Astral Atrium (second CardiacSystem arcane organ), Chi Pool, Training and Conditioning
    // (Vigor/Reflexes), Reaction Speed, Hiding, Sneaking — StaminaPool's new terms read
    // AstralAtrium/Vigor as raw values (not a neutral-6 deviation), same "zero at the
    // organ/training-absent default" shape as the rest of this group.
    private double kStaminaPoolAstralAtrium = 4;
    private double kStaminaPoolVigor = 5;
    private double kChiPoolAstralAtrium = 8;
    private double kReactionSpeedNeuralDrive = 6;
    private double kReactionSpeedReflexes = 5;
    private double kHidingShapeAesthetics = 1;
    private double kSneakingAgility = 1;

    // Training and Conditioning (rpg-21) — Intensity/Coordination/Resilience/Fighting/
    // WeaponPracticing/Shooting join Vigor/Reflexes, same raw-value shape (zero at the
    // training-absent default of 0).
    private double kMeanStrengthIntensity = 2;
    private double kSpeedIntensity = 2;
    private double kAcrobaticsCoordination = 2;
    private double kEvasionCoordination = 1;
    private double kBalanceCoordination = 2;
    private double kPainThresholdResilience = 2;
    private double kCloseCombatFighting = 5;
    private double kLowRangeCombatWeaponPracticing = 5;
    private double kLongRangeCombatShooting = 4;
    private double kAimShooting = 3;

    // Athletism and Martial Arts (rpg-21) — Dancing/Fencing are new Knowledge constants (level
    // 0-4), same "level x weight" shape as Ecology/Biology/Archery.
    private double kAcrobaticsDancing = 2;
    private double kEvasionDancing = 1;
    private double kBalanceDancing = 2;
    private double kLowRangeCombatFencing = 3;

    // Archery (rpg-21) — first real formula effect for this pre-existing Knowledge constant.
    private double kLongRangeCombatArchery = 4;
    private double kAimArchery = 3;

    // Skills (rpg-21) — new craft/practice attributes, all baseline 60, Knowledge-level-driven.
    private double kAlchemyChemistry = 8;
    private double kAlchemyWizardry = 8;
    private double kMachineHandlingEngineering = 8;
    private double kPerformanceCoordination = 1;
    private double kPerformanceDancing = 8;
    private double kPerformanceShapeAesthetics = 2;
    private double kPerformanceArt = 8;
    private double kSciencePracticeBiology = 8;
    private double kSciencePracticeChemistry = 8;
    private double kHealingMedicine = 8;
    private double kHealingBiology = 4;
    private double kHackingAndProgramingComputerScience = 8;

    // Safety floor shared by Strength-family (now Push/Leg/Grip/Lift Strength), FatigueResistance,
    // Evasion, MovementSpeed, SoftTissueDurability (rpg-21)
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

    public double getSoftTissueDurabilityBaseline() { return softTissueDurabilityBaseline; }
    public void setSoftTissueDurabilityBaseline(double v) { this.softTissueDurabilityBaseline = v; }

    public double getKSoftTissueDurabilityMesomorphy() { return kSoftTissueDurabilityMesomorphy; }
    public void setKSoftTissueDurabilityMesomorphy(double v) { this.kSoftTissueDurabilityMesomorphy = v; }

    public double getKSoftTissueDurabilityBodyFat() { return kSoftTissueDurabilityBodyFat; }
    public void setKSoftTissueDurabilityBodyFat(double v) { this.kSoftTissueDurabilityBodyFat = v; }

    public double getKSoftTissueDurabilityFlexibility() { return kSoftTissueDurabilityFlexibility; }
    public void setKSoftTissueDurabilityFlexibility(double v) { this.kSoftTissueDurabilityFlexibility = v; }

    public double getKSoftTissueDurabilitySkin() { return kSoftTissueDurabilitySkin; }
    public void setKSoftTissueDurabilitySkin(double v) { this.kSoftTissueDurabilitySkin = v; }

    public double getKSoftTissueDurabilityResilience() { return kSoftTissueDurabilityResilience; }
    public void setKSoftTissueDurabilityResilience(double v) { this.kSoftTissueDurabilityResilience = v; }

    public double getKBoneDurabilityBoneDensity() { return kBoneDurabilityBoneDensity; }
    public void setKBoneDurabilityBoneDensity(double v) { this.kBoneDurabilityBoneDensity = v; }

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

    public double getKMovementSpeedLimbRatio() { return kMovementSpeedLimbRatio; }
    public void setKMovementSpeedLimbRatio(double v) { this.kMovementSpeedLimbRatio = v; }

    public double getKMovementSpeedMuscleDistribution() { return kMovementSpeedMuscleDistribution; }
    public void setKMovementSpeedMuscleDistribution(double v) { this.kMovementSpeedMuscleDistribution = v; }

    public double getKMovementSpeedHeight() { return kMovementSpeedHeight; }
    public void setKMovementSpeedHeight(double v) { this.kMovementSpeedHeight = v; }

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

    public double getKMediunityNoeticPlexus() { return kMediunityNoeticPlexus; }
    public void setKMediunityNoeticPlexus(double v) { this.kMediunityNoeticPlexus = v; }

    public double getKSurvivalSkillsEcology() { return kSurvivalSkillsEcology; }
    public void setKSurvivalSkillsEcology(double v) { this.kSurvivalSkillsEcology = v; }

    public double getKAnimalCaringEcology() { return kAnimalCaringEcology; }
    public void setKAnimalCaringEcology(double v) { this.kAnimalCaringEcology = v; }

    public double getKAnimalCaringBiology() { return kAnimalCaringBiology; }
    public void setKAnimalCaringBiology(double v) { this.kAnimalCaringBiology = v; }

    public double getKDiscretionShapeAesthetics() { return kDiscretionShapeAesthetics; }
    public void setKDiscretionShapeAesthetics(double v) { this.kDiscretionShapeAesthetics = v; }

    public double getKFearResistanceSelfSacrifice() { return kFearResistanceSelfSacrifice; }
    public void setKFearResistanceSelfSacrifice(double v) { this.kFearResistanceSelfSacrifice = v; }

    public double getKFearResistanceSuicidal() { return kFearResistanceSuicidal; }
    public void setKFearResistanceSuicidal(double v) { this.kFearResistanceSuicidal = v; }

    public double getKPainThresholdSelfSacrifice() { return kPainThresholdSelfSacrifice; }
    public void setKPainThresholdSelfSacrifice(double v) { this.kPainThresholdSelfSacrifice = v; }

    public double getKDiscretionLoneWolf() { return kDiscretionLoneWolf; }
    public void setKDiscretionLoneWolf(double v) { this.kDiscretionLoneWolf = v; }

    public double getKDiscretionBackstabber() { return kDiscretionBackstabber; }
    public void setKDiscretionBackstabber(double v) { this.kDiscretionBackstabber = v; }

    public double getKCommandDominant() { return kCommandDominant; }
    public void setKCommandDominant(double v) { this.kCommandDominant = v; }

    public double getKCommandPossessive() { return kCommandPossessive; }
    public void setKCommandPossessive(double v) { this.kCommandPossessive = v; }

    public double getKManipulationDominant() { return kManipulationDominant; }
    public void setKManipulationDominant(double v) { this.kManipulationDominant = v; }

    public double getKManipulationPossessive() { return kManipulationPossessive; }
    public void setKManipulationPossessive(double v) { this.kManipulationPossessive = v; }

    public double getKManipulationRelativist() { return kManipulationRelativist; }
    public void setKManipulationRelativist(double v) { this.kManipulationRelativist = v; }

    public double getKSurvivalSkillsExpatriated() { return kSurvivalSkillsExpatriated; }
    public void setKSurvivalSkillsExpatriated(double v) { this.kSurvivalSkillsExpatriated = v; }

    public double getKSurvivalSkillsAnarchist() { return kSurvivalSkillsAnarchist; }
    public void setKSurvivalSkillsAnarchist(double v) { this.kSurvivalSkillsAnarchist = v; }

    public double getKMediunityPagan() { return kMediunityPagan; }
    public void setKMediunityPagan(double v) { this.kMediunityPagan = v; }

    public double getKFaithPagan() { return kFaithPagan; }
    public void setKFaithPagan(double v) { this.kFaithPagan = v; }

    public double getKFaithRelativist() { return kFaithRelativist; }
    public void setKFaithRelativist(double v) { this.kFaithRelativist = v; }

    public double getKFaithProfane() { return kFaithProfane; }
    public void setKFaithProfane(double v) { this.kFaithProfane = v; }

    public double getKIntimidationProfane() { return kIntimidationProfane; }
    public void setKIntimidationProfane(double v) { this.kIntimidationProfane = v; }

    public double getKIntimidationBellicose() { return kIntimidationBellicose; }
    public void setKIntimidationBellicose(double v) { this.kIntimidationBellicose = v; }

    public double getKWillRelativist() { return kWillRelativist; }
    public void setKWillRelativist(double v) { this.kWillRelativist = v; }

    public double getKWillPracticalist() { return kWillPracticalist; }
    public void setKWillPracticalist(double v) { this.kWillPracticalist = v; }

    public double getKWillNihilist() { return kWillNihilist; }
    public void setKWillNihilist(double v) { this.kWillNihilist = v; }

    public double getKEnfactuationRelativist() { return kEnfactuationRelativist; }
    public void setKEnfactuationRelativist(double v) { this.kEnfactuationRelativist = v; }

    public double getKEnfactuationBellicose() { return kEnfactuationBellicose; }
    public void setKEnfactuationBellicose(double v) { this.kEnfactuationBellicose = v; }

    public double getKReasoningRelativist() { return kReasoningRelativist; }
    public void setKReasoningRelativist(double v) { this.kReasoningRelativist = v; }

    public double getKReasoningIliterate() { return kReasoningIliterate; }
    public void setKReasoningIliterate(double v) { this.kReasoningIliterate = v; }

    public double getKIllusionResistanceRelativist() { return kIllusionResistanceRelativist; }
    public void setKIllusionResistanceRelativist(double v) { this.kIllusionResistanceRelativist = v; }

    public double getKIllusionResistancePracticalist() { return kIllusionResistancePracticalist; }
    public void setKIllusionResistancePracticalist(double v) { this.kIllusionResistancePracticalist = v; }

    public double getKAngerResistancePracticalist() { return kAngerResistancePracticalist; }
    public void setKAngerResistancePracticalist(double v) { this.kAngerResistancePracticalist = v; }

    public double getKAngerResistanceBellicose() { return kAngerResistanceBellicose; }
    public void setKAngerResistanceBellicose(double v) { this.kAngerResistanceBellicose = v; }

    public double getKMentalHealthPracticalist() { return kMentalHealthPracticalist; }
    public void setKMentalHealthPracticalist(double v) { this.kMentalHealthPracticalist = v; }

    public double getKMentalHealthNihilist() { return kMentalHealthNihilist; }
    public void setKMentalHealthNihilist(double v) { this.kMentalHealthNihilist = v; }

    public double getKMemoryPoolIliterate() { return kMemoryPoolIliterate; }
    public void setKMemoryPoolIliterate(double v) { this.kMemoryPoolIliterate = v; }

    public double getKMemoryPoolPastEraser() { return kMemoryPoolPastEraser; }
    public void setKMemoryPoolPastEraser(double v) { this.kMemoryPoolPastEraser = v; }

    public double getKAnimalCaringAntiNaturalist() { return kAnimalCaringAntiNaturalist; }
    public void setKAnimalCaringAntiNaturalist(double v) { this.kAnimalCaringAntiNaturalist = v; }

    public double getKPoisonResistanceAntiNaturalist() { return kPoisonResistanceAntiNaturalist; }
    public void setKPoisonResistanceAntiNaturalist(double v) { this.kPoisonResistanceAntiNaturalist = v; }

    public double getKFoodPoisoningAntiNaturalist() { return kFoodPoisoningAntiNaturalist; }
    public void setKFoodPoisoningAntiNaturalist(double v) { this.kFoodPoisoningAntiNaturalist = v; }

    public double getKDiseaseResistanceAntiNaturalist() { return kDiseaseResistanceAntiNaturalist; }
    public void setKDiseaseResistanceAntiNaturalist(double v) { this.kDiseaseResistanceAntiNaturalist = v; }

    public double getKCreativityOrphanMind() { return kCreativityOrphanMind; }
    public void setKCreativityOrphanMind(double v) { this.kCreativityOrphanMind = v; }

    public double getKCreativityPastEraser() { return kCreativityPastEraser; }
    public void setKCreativityPastEraser(double v) { this.kCreativityPastEraser = v; }

    public double getKBehaviorReadingDogEatDog() { return kBehaviorReadingDogEatDog; }
    public void setKBehaviorReadingDogEatDog(double v) { this.kBehaviorReadingDogEatDog = v; }

    public double getKAnalysisReasoning() { return kAnalysisReasoning; }
    public void setKAnalysisReasoning(double v) { this.kAnalysisReasoning = v; }

    public double getKAnalysisDogEatDog() { return kAnalysisDogEatDog; }
    public void setKAnalysisDogEatDog(double v) { this.kAnalysisDogEatDog = v; }

    public double getKMeleeAccuracyDogEatDog() { return kMeleeAccuracyDogEatDog; }
    public void setKMeleeAccuracyDogEatDog(double v) { this.kMeleeAccuracyDogEatDog = v; }

    public double getKAimDogEatDog() { return kAimDogEatDog; }
    public void setKAimDogEatDog(double v) { this.kAimDogEatDog = v; }

    public double getKArcaneOutputConservative() { return kArcaneOutputConservative; }
    public void setKArcaneOutputConservative(double v) { this.kArcaneOutputConservative = v; }

    public double getKManaPoolConservative() { return kManaPoolConservative; }
    public void setKManaPoolConservative(double v) { this.kManaPoolConservative = v; }

    public double getKCloseCombatBellicose() { return kCloseCombatBellicose; }
    public void setKCloseCombatBellicose(double v) { this.kCloseCombatBellicose = v; }

    public double getKLowRangeCombatBellicose() { return kLowRangeCombatBellicose; }
    public void setKLowRangeCombatBellicose(double v) { this.kLowRangeCombatBellicose = v; }

    public double getKPsyquismOutputPhaxicCerebelum() { return kPsyquismOutputPhaxicCerebelum; }
    public void setKPsyquismOutputPhaxicCerebelum(double v) { this.kPsyquismOutputPhaxicCerebelum = v; }

    public double getKPsyquismOutputCerebralCapacity() { return kPsyquismOutputCerebralCapacity; }
    public void setKPsyquismOutputCerebralCapacity(double v) { this.kPsyquismOutputCerebralCapacity = v; }

    public double getKPsyquismDefensePhaxicCerebelum() { return kPsyquismDefensePhaxicCerebelum; }
    public void setKPsyquismDefensePhaxicCerebelum(double v) { this.kPsyquismDefensePhaxicCerebelum = v; }

    public double getKCharmResistanceVanity() { return kCharmResistanceVanity; }
    public void setKCharmResistanceVanity(double v) { this.kCharmResistanceVanity = v; }

    public double getKCharmResistanceDiscretion() { return kCharmResistanceDiscretion; }
    public void setKCharmResistanceDiscretion(double v) { this.kCharmResistanceDiscretion = v; }

    public double getKCharmResistanceProtagonist() { return kCharmResistanceProtagonist; }
    public void setKCharmResistanceProtagonist(double v) { this.kCharmResistanceProtagonist = v; }

    public double getKConcentrationFocus() { return kConcentrationFocus; }
    public void setKConcentrationFocus(double v) { this.kConcentrationFocus = v; }

    public double getKConcentrationCerebralCapacity() { return kConcentrationCerebralCapacity; }
    public void setKConcentrationCerebralCapacity(double v) { this.kConcentrationCerebralCapacity = v; }

    public double getKPurityCleanVessel() { return kPurityCleanVessel; }
    public void setKPurityCleanVessel(double v) { this.kPurityCleanVessel = v; }

    public double getKEnfactuationVanity() { return kEnfactuationVanity; }
    public void setKEnfactuationVanity(double v) { this.kEnfactuationVanity = v; }

    public double getKIntimidationVanity() { return kIntimidationVanity; }
    public void setKIntimidationVanity(double v) { this.kIntimidationVanity = v; }

    public double getKEnfactuationReliable() { return kEnfactuationReliable; }
    public void setKEnfactuationReliable(double v) { this.kEnfactuationReliable = v; }

    public double getKEnfactuationPeacekeeper() { return kEnfactuationPeacekeeper; }
    public void setKEnfactuationPeacekeeper(double v) { this.kEnfactuationPeacekeeper = v; }

    public double getKIntimidationPeacekeeper() { return kIntimidationPeacekeeper; }
    public void setKIntimidationPeacekeeper(double v) { this.kIntimidationPeacekeeper = v; }

    public double getKFaithReligionPractitioner() { return kFaithReligionPractitioner; }
    public void setKFaithReligionPractitioner(double v) { this.kFaithReligionPractitioner = v; }

    public double getKIllusionResistanceRealitic() { return kIllusionResistanceRealitic; }
    public void setKIllusionResistanceRealitic(double v) { this.kIllusionResistanceRealitic = v; }

    public double getKBluffingRealitic() { return kBluffingRealitic; }
    public void setKBluffingRealitic(double v) { this.kBluffingRealitic = v; }

    public double getKReasoningPhilosopher() { return kReasoningPhilosopher; }
    public void setKReasoningPhilosopher(double v) { this.kReasoningPhilosopher = v; }

    public double getKSurvivalSkillsOutdoorLifestyle() { return kSurvivalSkillsOutdoorLifestyle; }
    public void setKSurvivalSkillsOutdoorLifestyle(double v) { this.kSurvivalSkillsOutdoorLifestyle = v; }

    public double getKAnimalCaringOutdoorLifestyle() { return kAnimalCaringOutdoorLifestyle; }
    public void setKAnimalCaringOutdoorLifestyle(double v) { this.kAnimalCaringOutdoorLifestyle = v; }

    public double getKCreativityInventor() { return kCreativityInventor; }
    public void setKCreativityInventor(double v) { this.kCreativityInventor = v; }

    public double getKStaminaPoolAstralAtrium() { return kStaminaPoolAstralAtrium; }
    public void setKStaminaPoolAstralAtrium(double v) { this.kStaminaPoolAstralAtrium = v; }

    public double getKStaminaPoolVigor() { return kStaminaPoolVigor; }
    public void setKStaminaPoolVigor(double v) { this.kStaminaPoolVigor = v; }

    public double getKChiPoolAstralAtrium() { return kChiPoolAstralAtrium; }
    public void setKChiPoolAstralAtrium(double v) { this.kChiPoolAstralAtrium = v; }

    public double getKReactionSpeedNeuralDrive() { return kReactionSpeedNeuralDrive; }
    public void setKReactionSpeedNeuralDrive(double v) { this.kReactionSpeedNeuralDrive = v; }

    public double getKReactionSpeedReflexes() { return kReactionSpeedReflexes; }
    public void setKReactionSpeedReflexes(double v) { this.kReactionSpeedReflexes = v; }

    public double getKHidingShapeAesthetics() { return kHidingShapeAesthetics; }
    public void setKHidingShapeAesthetics(double v) { this.kHidingShapeAesthetics = v; }

    public double getKSneakingAgility() { return kSneakingAgility; }
    public void setKSneakingAgility(double v) { this.kSneakingAgility = v; }

    public double getKMeanStrengthIntensity() { return kMeanStrengthIntensity; }
    public void setKMeanStrengthIntensity(double v) { this.kMeanStrengthIntensity = v; }

    public double getKSpeedIntensity() { return kSpeedIntensity; }
    public void setKSpeedIntensity(double v) { this.kSpeedIntensity = v; }

    public double getKAcrobaticsCoordination() { return kAcrobaticsCoordination; }
    public void setKAcrobaticsCoordination(double v) { this.kAcrobaticsCoordination = v; }

    public double getKEvasionCoordination() { return kEvasionCoordination; }
    public void setKEvasionCoordination(double v) { this.kEvasionCoordination = v; }

    public double getKBalanceCoordination() { return kBalanceCoordination; }
    public void setKBalanceCoordination(double v) { this.kBalanceCoordination = v; }

    public double getKPainThresholdResilience() { return kPainThresholdResilience; }
    public void setKPainThresholdResilience(double v) { this.kPainThresholdResilience = v; }

    public double getKCloseCombatFighting() { return kCloseCombatFighting; }
    public void setKCloseCombatFighting(double v) { this.kCloseCombatFighting = v; }

    public double getKLowRangeCombatWeaponPracticing() { return kLowRangeCombatWeaponPracticing; }
    public void setKLowRangeCombatWeaponPracticing(double v) { this.kLowRangeCombatWeaponPracticing = v; }

    public double getKLongRangeCombatShooting() { return kLongRangeCombatShooting; }
    public void setKLongRangeCombatShooting(double v) { this.kLongRangeCombatShooting = v; }

    public double getKAimShooting() { return kAimShooting; }
    public void setKAimShooting(double v) { this.kAimShooting = v; }

    public double getKAcrobaticsDancing() { return kAcrobaticsDancing; }
    public void setKAcrobaticsDancing(double v) { this.kAcrobaticsDancing = v; }

    public double getKEvasionDancing() { return kEvasionDancing; }
    public void setKEvasionDancing(double v) { this.kEvasionDancing = v; }

    public double getKBalanceDancing() { return kBalanceDancing; }
    public void setKBalanceDancing(double v) { this.kBalanceDancing = v; }

    public double getKLowRangeCombatFencing() { return kLowRangeCombatFencing; }
    public void setKLowRangeCombatFencing(double v) { this.kLowRangeCombatFencing = v; }

    public double getKLongRangeCombatArchery() { return kLongRangeCombatArchery; }
    public void setKLongRangeCombatArchery(double v) { this.kLongRangeCombatArchery = v; }

    public double getKAimArchery() { return kAimArchery; }
    public void setKAimArchery(double v) { this.kAimArchery = v; }

    public double getKAlchemyChemistry() { return kAlchemyChemistry; }
    public void setKAlchemyChemistry(double v) { this.kAlchemyChemistry = v; }

    public double getKAlchemyWizardry() { return kAlchemyWizardry; }
    public void setKAlchemyWizardry(double v) { this.kAlchemyWizardry = v; }

    public double getKMachineHandlingEngineering() { return kMachineHandlingEngineering; }
    public void setKMachineHandlingEngineering(double v) { this.kMachineHandlingEngineering = v; }

    public double getKPerformanceCoordination() { return kPerformanceCoordination; }
    public void setKPerformanceCoordination(double v) { this.kPerformanceCoordination = v; }

    public double getKPerformanceDancing() { return kPerformanceDancing; }
    public void setKPerformanceDancing(double v) { this.kPerformanceDancing = v; }

    public double getKPerformanceShapeAesthetics() { return kPerformanceShapeAesthetics; }
    public void setKPerformanceShapeAesthetics(double v) { this.kPerformanceShapeAesthetics = v; }

    public double getKPerformanceArt() { return kPerformanceArt; }
    public void setKPerformanceArt(double v) { this.kPerformanceArt = v; }

    public double getKSciencePracticeBiology() { return kSciencePracticeBiology; }
    public void setKSciencePracticeBiology(double v) { this.kSciencePracticeBiology = v; }

    public double getKSciencePracticeChemistry() { return kSciencePracticeChemistry; }
    public void setKSciencePracticeChemistry(double v) { this.kSciencePracticeChemistry = v; }

    public double getKHealingMedicine() { return kHealingMedicine; }
    public void setKHealingMedicine(double v) { this.kHealingMedicine = v; }

    public double getKHealingBiology() { return kHealingBiology; }
    public void setKHealingBiology(double v) { this.kHealingBiology = v; }

    public double getKHackingAndProgramingComputerScience() { return kHackingAndProgramingComputerScience; }
    public void setKHackingAndProgramingComputerScience(double v) { this.kHackingAndProgramingComputerScience = v; }

    public double getAttributeFloor() { return attributeFloor; }
    public void setAttributeFloor(double attributeFloor) { this.attributeFloor = attributeFloor; }
}
