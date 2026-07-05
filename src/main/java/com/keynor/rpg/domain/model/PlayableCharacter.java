package com.keynor.rpg.domain.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Aggregate root for a playable character. Holds the {@link Body} pillar (wound tree +
 * data groups) and exposes all derived physical attribute formulas. Formulas combine inputs
 * from {@link Biomechanics} (genetics + body composition) and {@link BodySystems}
 * (cardiovascular, neural, hormonal, and digestive systems) — none of these groups owns the
 * formulas themselves. {@link NeuralSystem} absorbed the former {@code SpatialIntelligence}
 * group in rpg-13 (perception/agility/precision now live there as
 * hippocampus/agility/precision), and split {@code hippocampus} into
 * {@code hippocampus}/{@code thalamus} in Delta V4 (memory vs. external perception).
 *
 * <p><b>Additive standard (rpg-11, extended rpg-13/rpg-14/Delta V4):</b> every derived
 * attribute is {@code baseline + sum(weight x (input - neutral))} — the previous
 * multiplicative model (square-cube law, power-to-weight ratios, logarithms) is fully
 * replaced. {@code baseline} is 60 (see {@link BodyCoefficients#getBaseline()}). Most inputs
 * are 1-9 with neutral 5; {@code limbRatio} is 1-5 with neutral 3; {@code height} is 1-15 with
 * neutral 7; {@code bodyFat}'s own neutral is 3 (not 5); {@code bloodThickness} is 1-5 neutral
 * 3; {@code skinThickness} is 1-7 neutral 3. {@code height}/{@code muscleMass}/{@code bodyFat}
 * additionally feed {@link #getSymbolicTotalMass()}/{@link #getDisplayMassKg()} directly (not
 * as deviations). See {@code .claude/skills/additive-attribute-formulas.md} for the full
 * design rationale.
 *
 * <p><b>Delta V4 (2026-07-03):</b> the old global {@code Strength} and its Load Capacity group
 * were deprecated outright and replaced by a hidden {@code meanStrength()} engine plus four
 * specialized, player-facing strengths ({@link #getPushStrength()}, {@link #getLegDrive()},
 * {@link #getGripStrength()}, {@link #getLiftStrength()}) and two averaged combat attributes
 * ({@link #getSwingPower()}, {@link #getGrapplingSelfLifting()}). Load Capacity now derives
 * from {@link #getLiftStrength()} instead of the old Strength. Every additive-standard getter
 * now has a companion {@code getXxxBreakdown()} method returning an {@link AttributeBreakdown}
 * — the term-by-term resolved values the frontend renders in its attribute tooltips, so the
 * API is the single source of truth for both the number and its resolved calculation (no
 * duplicate formula logic on the client). {@code Swing Power}/{@code Grappling Self Lifting}
 * (averages of two already-resolved attributes) and the Load Capacity group (non-linear
 * transforms of {@code LiftStrength}) are not additive-standard formulas and do not get a
 * breakdown.
 *
 * <p>All formula coefficients are tunable via {@link Body#getCoefficients()} without
 * modifying any formula code. Default coefficients are not balanced game data — tune
 * through play.
 */
public class PlayableCharacter {

    private final String name;
    private final Body body;
    private final Mind mind;
    private String loreReference;

    /**
     * Convenience constructor for the many call sites (chiefly tests) that only care about the
     * Body pillar — defaults {@link Mind} to {@link Mind#humanTemplate()} so every Mind-driven
     * formula still resolves (every Value sits at its own neutral, contributing zero).
     */
    public PlayableCharacter(String name, Body body) {
        this(name, body, Mind.humanTemplate());
    }

    public PlayableCharacter(String name, Body body, Mind mind) {
        this.name = name;
        this.body = body;
        this.mind = mind;
    }

    // -------------------------------------------------------------------------
    // Derived mass — building blocks for several formulas below
    // -------------------------------------------------------------------------

    /**
     * SymbolicTotalMass = kSymbolicMassBase + Height + MuscleMass + BodyFat + (BoneDensity - 5).
     * Abstract integer mass index used to penalize Speed and FatigueResistance — distinct
     * from {@link #getDisplayMassKg()}, which is the real-kg number shown to the player.
     */
    public int getSymbolicTotalMass() {
        double raw = coeff().getKSymbolicMassBase()
                + genetics().getHeight()
                + composition().getMuscleMass()
                + composition().getBodyFat()
                + (composition().getBoneDensity() - 5);
        return (int) Math.round(raw);
    }

    /**
     * DisplayMassKg = MuscleKg + FatKg + FrameKg + BoneModKg. UI-facing real-world mass —
     * never used by gameplay formulas directly except as an input to
     * {@link #getDragCapacityKg()}, which mixes it with {@link #getLiftStrength()}.
     */
    public double getDisplayMassKg() {
        double muscleKg = composition().getMuscleMass() * coeff().getKMuscleKgMultiplier()
                + coeff().getKMuscleKgOffset();
        double fatKg = composition().getBodyFat() * coeff().getKFatKgMultiplier();
        double frameKg = coeff().getKFrameKgBase()
                + Math.floor((genetics().getHeight() * coeff().getKFrameKgHeightMultiplier())
                        / coeff().getKFrameKgDivisor());
        double boneModKg = composition().getBoneDensity() - 5;
        return muscleKg + fatKg + frameKg + boneModKg;
    }

    // -------------------------------------------------------------------------
    // Mean Strength (Delta V4) — hidden base engine, never exposed via any DTO or public getter.
    // -------------------------------------------------------------------------

    private AttributeBreakdown meanStrengthBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKMeanStrengthMuscleMass() * (composition().getMuscleMass() - 5),
                coeff().getKMeanStrengthNeuromuscular() * (neuralSystem().getNeuromuscularEfficiency() - 5),
                coeff().getKMeanStrengthFiberType() * (composition().getDominantFiberType() - 5)
        ));
    }

    private double meanStrength() {
        return meanStrengthBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // 4 specialized strengths (Delta V4) — each anchored on the hidden meanStrength(), not the
    // baseline directly. All floored, continuing the old Strength's floor convention.
    // -------------------------------------------------------------------------

    /**
     * PushStrength ("Upper Strike") = meanStrength() + kPushStrengthLimbRatio x (LimbRatio-3) +
     * kPushStrengthMuscleDistribution x (MuscleDistribution-5) + kPushStrengthTendons x
     * (TendonsAndLigaments-5) + kPushStrengthHeight x (Height-7). Floored.
     */
    public AttributeBreakdown getPushStrengthBreakdown() {
        return new AttributeBreakdown(meanStrength(), List.of(
                coeff().getKPushStrengthLimbRatio() * (genetics().getLimbRatio() - 3),
                coeff().getKPushStrengthMuscleDistribution() * (composition().getMuscleDistribution() - 5),
                coeff().getKPushStrengthTendons() * (composition().getTendonsAndLigaments() - 5),
                coeff().getKPushStrengthHeight() * (genetics().getHeight() - 7)
        ));
    }

    public double getPushStrength() {
        return floor(getPushStrengthBreakdown().total());
    }

    /**
     * LegDrive = meanStrength() + kLegDriveLimbRatio x (LimbRatio-3) +
     * kLegDriveMuscleDistribution x (5-MuscleDistribution) + kLegDriveTendons x
     * (TendonsAndLigaments-5) + kLegDriveHeight x (Height-7). MuscleDistribution term inverted
     * relative to {@link #getPushStrength()} — leg-bias helps, arm-bias hurts. Floored.
     */
    public AttributeBreakdown getLegDriveBreakdown() {
        return new AttributeBreakdown(meanStrength(), List.of(
                coeff().getKLegDriveLimbRatio() * (genetics().getLimbRatio() - 3),
                coeff().getKLegDriveMuscleDistribution() * (5 - composition().getMuscleDistribution()),
                coeff().getKLegDriveTendons() * (composition().getTendonsAndLigaments() - 5),
                coeff().getKLegDriveHeight() * (genetics().getHeight() - 7)
        ));
    }

    public double getLegDrive() {
        return floor(getLegDriveBreakdown().total());
    }

    /**
     * GripStrength = meanStrength() + kGripStrengthMuscleDistribution x
     * (MuscleDistribution-5) + kGripStrengthTendons x (TendonsAndLigaments-5). Floored.
     */
    public AttributeBreakdown getGripStrengthBreakdown() {
        return new AttributeBreakdown(meanStrength(), List.of(
                coeff().getKGripStrengthMuscleDistribution() * (composition().getMuscleDistribution() - 5),
                coeff().getKGripStrengthTendons() * (composition().getTendonsAndLigaments() - 5)
        ));
    }

    public double getGripStrength() {
        return floor(getGripStrengthBreakdown().total());
    }

    /**
     * LiftStrength ("Pull Strength") = meanStrength() - kLiftStrengthLimbRatio x (LimbRatio-3) +
     * kLiftStrengthTendons x (TendonsAndLigaments-5). Floored. Feeds the entire Load Capacity
     * group below (replaces the old global Strength).
     */
    public AttributeBreakdown getLiftStrengthBreakdown() {
        return new AttributeBreakdown(meanStrength(), List.of(
                -coeff().getKLiftStrengthLimbRatio() * (genetics().getLimbRatio() - 3),
                coeff().getKLiftStrengthTendons() * (composition().getTendonsAndLigaments() - 5)
        ));
    }

    public double getLiftStrength() {
        return floor(getLiftStrengthBreakdown().total());
    }

    /**
     * SwingPower = floor((PushStrength + GripStrength) / 2). Average of two already-resolved
     * attributes, not an additive-standard formula — no breakdown.
     */
    public double getSwingPower() {
        return floor(Math.floor((getPushStrength() + getGripStrength()) / 2));
    }

    /**
     * GrapplingSelfLifting = floor((GripStrength + LiftStrength) / 2). Same shape as
     * {@link #getSwingPower()} — no breakdown.
     */
    public double getGrapplingSelfLifting() {
        return floor(Math.floor((getGripStrength() + getLiftStrength()) / 2));
    }

    // -------------------------------------------------------------------------
    // Biomechanics-derived attributes
    // -------------------------------------------------------------------------

    /**
     * Speed = baseline + kSpeedNeuromuscular x (NeuromuscularEfficiency-5) + kSpeedMuscleMass x
     * (MuscleMass-5) + kSpeedFiberType x (FiberType-5) - floor((SymbolicTotalMass -
     * kSpeedMassNeutral) / kSpeedMassDivisor). The divisor-3 mass penalty (rpg-11 revision)
     * keeps the worst-case result positive (minimum ~27 at baseline 60) without needing a
     * floor.
     */
    public AttributeBreakdown getSpeedBreakdown() {
        double massPenalty = Math.floor(
                (getSymbolicTotalMass() - coeff().getKSpeedMassNeutral()) / coeff().getKSpeedMassDivisor());
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKSpeedNeuromuscular() * (neuralSystem().getNeuromuscularEfficiency() - 5),
                coeff().getKSpeedMuscleMass() * (composition().getMuscleMass() - 5),
                coeff().getKSpeedFiberType() * (composition().getDominantFiberType() - 5),
                -massPenalty
        ));
    }

    public double getSpeed() {
        return getSpeedBreakdown().total();
    }

    /**
     * MaxMovementSpeed = Speed + kMaxMovementSpeedLimbRatio x (LimbRatio-3) -
     * kMaxMovementSpeedMuscleDistribution x (MuscleDistribution-5) + kMaxMovementSpeedHeight x
     * (Height-7) (height term added Delta V4). Displacement/travel speed, anchored on Speed.
     * Floored.
     */
    public AttributeBreakdown getMaxMovementSpeedBreakdown() {
        return new AttributeBreakdown(getSpeed(), List.of(
                coeff().getKMaxMovementSpeedLimbRatio() * (genetics().getLimbRatio() - 3),
                -coeff().getKMaxMovementSpeedMuscleDistribution() * (composition().getMuscleDistribution() - 5),
                coeff().getKMaxMovementSpeedHeight() * (genetics().getHeight() - 7)
        ));
    }

    public double getMaxMovementSpeed() {
        return floor(getMaxMovementSpeedBreakdown().total());
    }

    /**
     * StaminaPool = baseline + kStaminaPoolPulmonary x (PulmonaryCapacity-5) +
     * kStaminaPoolCardiac x (CardiacOutput-5) + kStaminaPoolOxygen x
     * (OxygenCarryingCapacity-5) - kStaminaPoolFiberType x (FiberType-5) +
     * kStaminaPoolDigestiveAbsorption x (DigestiveAbsorption-5) (renamed from
     * NutrientAbsorption, Delta V4).
     */
    public AttributeBreakdown getStaminaPoolBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKStaminaPoolPulmonary() * (bodySystems().getPulmonarySystem().getPulmonaryCapacity() - 5),
                coeff().getKStaminaPoolCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5),
                coeff().getKStaminaPoolOxygen() * (bodySystems().getBloodSystem().getOxygenCarryingCapacity() - 5),
                -coeff().getKStaminaPoolFiberType() * (composition().getDominantFiberType() - 5),
                coeff().getKStaminaPoolDigestiveAbsorption()
                        * (bodySystems().getDigestiveSystem().getDigestiveAbsorption() - 5)
        ));
    }

    public double getStaminaPool() {
        return getStaminaPoolBreakdown().total();
    }

    /**
     * FatigueResistance = baseline + kFatigueResistanceCardiac x (CardiacOutput-5) +
     * kFatigueResistancePulmonary x (PulmonaryCapacity-5) + kFatigueResistanceOxygen x
     * (OxygenCarryingCapacity-5) - kFatigueResistanceNeuromuscular x
     * (NeuromuscularEfficiency-5) - floor((SymbolicTotalMass - kFatigueResistanceMassNeutral) /
     * kFatigueResistanceMassDivisor) - kFatigueResistanceMuscleMass x (MuscleMass-5) +
     * kFatigueResistanceHypothalamus x (Hypothalamus-5) + kFatigueResistanceThyroid x
     * (Thyroid-5). Floored.
     */
    public AttributeBreakdown getFatigueResistanceBreakdown() {
        double massPenalty = Math.floor((getSymbolicTotalMass() - coeff().getKFatigueResistanceMassNeutral())
                / coeff().getKFatigueResistanceMassDivisor());
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKFatigueResistanceCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5),
                coeff().getKFatigueResistancePulmonary()
                        * (bodySystems().getPulmonarySystem().getPulmonaryCapacity() - 5),
                coeff().getKFatigueResistanceOxygen()
                        * (bodySystems().getBloodSystem().getOxygenCarryingCapacity() - 5),
                -coeff().getKFatigueResistanceNeuromuscular() * (neuralSystem().getNeuromuscularEfficiency() - 5),
                -massPenalty,
                -coeff().getKFatigueResistanceMuscleMass() * (composition().getMuscleMass() - 5),
                coeff().getKFatigueResistanceHypothalamus() * (neuralSystem().getHypothalamus() - 5),
                coeff().getKFatigueResistanceThyroid() * (bodySystems().getHormonalGlandularSystem().getThyroid() - 5)
        ));
    }

    public double getFatigueResistance() {
        return floor(getFatigueResistanceBreakdown().total());
    }

    /**
     * StaminaRecovery = baseline + kStaminaRecoveryOxygen x (OxygenCarryingCapacity-5) +
     * kStaminaRecoveryPulmonary x (PulmonaryCapacity-5) + kStaminaRecoveryCardiac x
     * (CardiacOutput-5) - kStaminaRecoveryFiberType x (FiberType-5).
     */
    public AttributeBreakdown getStaminaRecoveryBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKStaminaRecoveryOxygen() * (bodySystems().getBloodSystem().getOxygenCarryingCapacity() - 5),
                coeff().getKStaminaRecoveryPulmonary()
                        * (bodySystems().getPulmonarySystem().getPulmonaryCapacity() - 5),
                coeff().getKStaminaRecoveryCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5),
                -coeff().getKStaminaRecoveryFiberType() * (composition().getDominantFiberType() - 5)
        ));
    }

    public double getStaminaRecovery() {
        return getStaminaRecoveryBreakdown().total();
    }

    /**
     * Durability = baseline + kDurabilityBoneDensity x (BoneDensity-5) + kDurabilityMesomorphy
     * x (Mesomorphy-5) + kDurabilityBodyFat x (BodyFat-3) - kDurabilityFlexibility x
     * (Flexibility-5) + kDurabilitySkin x (SkinThickness-3). Note BodyFat's own neutral is 3,
     * not 5, same as SkinThickness's.
     */
    public AttributeBreakdown getDurabilityBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKDurabilityBoneDensity() * (composition().getBoneDensity() - 5),
                coeff().getKDurabilityMesomorphy() * (genetics().getMesomorphy() - 5),
                coeff().getKDurabilityBodyFat() * (composition().getBodyFat() - 3),
                -coeff().getKDurabilityFlexibility() * (composition().getFlexibility() - 5),
                coeff().getKDurabilitySkin() * (bodyStructure().getSkinThickness() - 3)
        ));
    }

    public double getDurability() {
        return getDurabilityBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // NeuralSystem-derived attributes (spatial/accuracy — formerly SpatialIntelligence)
    // -------------------------------------------------------------------------

    /**
     * Sight = baseline + kSightEyesSensitivity x (EyesSensitivity-5) + kSightThalamus x
     * (Thalamus-5) + kSightNeuralDrive x (NeuralDrive-5) + kSightPmod x Pmod. Reads
     * {@code Thalamus} instead of {@code Hippocampus} since Delta V4 (perception input, not
     * memory).
     */
    public AttributeBreakdown getSightBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKSightEyesSensitivity() * (sensorialOrgans().getEyesSensitivity() - 5),
                coeff().getKSightThalamus() * (neuralSystem().getThalamus() - 5),
                coeff().getKSightNeuralDrive() * (neuralSystem().getNeuralDrive() - 5),
                coeff().getKSightPmod() * progesteroneModifier()
        ));
    }

    public double getSight() {
        return getSightBreakdown().total();
    }

    /**
     * Hearing = baseline + kHearingEarsSensitivity x (EarsSensitivity-5) + kHearingThalamus x
     * (Thalamus-5) + kHearingNeuralDrive x (NeuralDrive-5) + kHearingPmod x Pmod.
     */
    public AttributeBreakdown getHearingBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKHearingEarsSensitivity() * (sensorialOrgans().getEarsSensitivity() - 5),
                coeff().getKHearingThalamus() * (neuralSystem().getThalamus() - 5),
                coeff().getKHearingNeuralDrive() * (neuralSystem().getNeuralDrive() - 5),
                coeff().getKHearingPmod() * progesteroneModifier()
        ));
    }

    public double getHearing() {
        return getHearingBreakdown().total();
    }

    /**
     * Smell = baseline + kSmellNoseSensitivity x (NoseSensitivity-5) + kSmellThalamus x
     * (Thalamus-5) + kSmellNeuralDrive x (NeuralDrive-5) + kSmellPmod x Pmod.
     */
    public AttributeBreakdown getSmellBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKSmellNoseSensitivity() * (sensorialOrgans().getNoseSensitivity() - 5),
                coeff().getKSmellThalamus() * (neuralSystem().getThalamus() - 5),
                coeff().getKSmellNeuralDrive() * (neuralSystem().getNeuralDrive() - 5),
                coeff().getKSmellPmod() * progesteroneModifier()
        ));
    }

    public double getSmell() {
        return getSmellBreakdown().total();
    }

    /**
     * Evasion = Speed + kEvasionAgility x (Agility-5) + kEvasionNeuralDrive x (NeuralDrive-5) +
     * kEvasionFlexibility x (Flexibility-5). Anchored directly on final movement speed.
     * Floored.
     */
    public AttributeBreakdown getEvasionBreakdown() {
        return new AttributeBreakdown(getSpeed(), List.of(
                coeff().getKEvasionAgility() * (neuralSystem().getAgility() - 5),
                coeff().getKEvasionNeuralDrive() * (neuralSystem().getNeuralDrive() - 5),
                coeff().getKEvasionFlexibility() * (composition().getFlexibility() - 5)
        ));
    }

    public double getEvasion() {
        return floor(getEvasionBreakdown().total());
    }

    /**
     * Acrobatics = baseline + kAcrobaticsAgility x (Agility-5) + kAcrobaticsFlexibility x
     * (Flexibility-5).
     */
    public AttributeBreakdown getAcrobaticsBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKAcrobaticsAgility() * (neuralSystem().getAgility() - 5),
                coeff().getKAcrobaticsFlexibility() * (composition().getFlexibility() - 5)
        ));
    }

    public double getAcrobatics() {
        return getAcrobaticsBreakdown().total();
    }

    /**
     * MeleeAccuracy = baseline + kMeleeAccuracyPrecision x (Precision-5) +
     * kMeleeAccuracyAgility x (Agility-5).
     */
    public AttributeBreakdown getMeleeAccuracyBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKMeleeAccuracyPrecision() * (neuralSystem().getPrecision() - 5),
                coeff().getKMeleeAccuracyAgility() * (neuralSystem().getAgility() - 5)
        ));
    }

    public double getMeleeAccuracy() {
        return getMeleeAccuracyBreakdown().total();
    }

    /**
     * Aim = baseline + kAimPrecision x (Precision-5) + kAimThalamus x (Thalamus-5). Reweighted
     * and switched from {@code Hippocampus} to {@code Thalamus} in Delta V4; the EyesSensitivity
     * term proposed in an earlier draft was dropped per explicit user instruction.
     */
    public AttributeBreakdown getAimBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKAimPrecision() * (neuralSystem().getPrecision() - 5),
                coeff().getKAimThalamus() * (neuralSystem().getThalamus() - 5)
        ));
    }

    public double getAim() {
        return getAimBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // Cognitive / Mental (rpg-13, Memory/ShortMemory reweighted Delta V4) — NeuralSystem-derived
    // -------------------------------------------------------------------------

    /** MemoryPool = baseline + kMemoryPoolCerebral x (CerebralCapacity-5) + kMemoryPoolHippocampus x (Hippocampus-5). */
    public AttributeBreakdown getMemoryPoolBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKMemoryPoolCerebral() * (neuralSystem().getCerebralCapacity() - 5),
                coeff().getKMemoryPoolHippocampus() * (neuralSystem().getHippocampus() - 5)
        ));
    }

    public double getMemoryPool() {
        return getMemoryPoolBreakdown().total();
    }

    /**
     * Reasoning = baseline + kReasoningSynapsis x (SynapsisQuality-5) + kReasoningTruth x
     * (Truth-1) (Mind pillar, new). {@code Truth}'s neutral is 1, its own default — see
     * {@link Values}.
     */
    public AttributeBreakdown getReasoningBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKReasoningSynapsis() * (neuralSystem().getSynapsisQuality() - 5),
                coeff().getKReasoningTruth() * (values().getTruth() - 1)
        ));
    }

    public double getReasoning() {
        return getReasoningBreakdown().total();
    }

    /**
     * ShortMemory = baseline + kShortMemoryCerebral x (CerebralCapacity-5) +
     * kShortMemorySynapsis x (SynapsisQuality-5) + kShortMemoryHippocampus x (Hippocampus-5) +
     * kShortMemoryKnowledge x (Knowledge-1) (Mind pillar, new). Still reads {@code Hippocampus}
     * (memory), not {@code Thalamus} — unlike Sight/Hearing/Smell/Balance/Aim, which moved to
     * Thalamus in Delta V4. {@code Knowledge}'s neutral is 1 (its own default), not 5 — see
     * {@link Values}.
     */
    public AttributeBreakdown getShortMemoryBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKShortMemoryCerebral() * (neuralSystem().getCerebralCapacity() - 5),
                coeff().getKShortMemorySynapsis() * (neuralSystem().getSynapsisQuality() - 5),
                coeff().getKShortMemoryHippocampus() * (neuralSystem().getHippocampus() - 5),
                coeff().getKShortMemoryKnowledge() * (values().getKnowledge() - 1)
        ));
    }

    public double getShortMemory() {
        return getShortMemoryBreakdown().total();
    }

    /**
     * MentalHealthPool = baseline - kMentalHealthAmygdala x (AmygdalaAndCingulum-5) -
     * kMentalHealthTmod x Tmod + kMentalHealthPmod x Pmod. Reserve for future Mind-pillar
     * mechanics — deliberately simplified until that pillar exists.
     */
    public AttributeBreakdown getMentalHealthPoolBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                -coeff().getKMentalHealthAmygdala() * (neuralSystem().getAmygdalaAndCingulum() - 5),
                -coeff().getKMentalHealthTmod() * testosteroneModifier(),
                coeff().getKMentalHealthPmod() * progesteroneModifier()
        ));
    }

    public double getMentalHealthPool() {
        return getMentalHealthPoolBreakdown().total();
    }

    /**
     * Will = MentalHealthPool's own baseline and terms, plus kWillMorality x (Morality-1) (Mind
     * pillar, new) — the divergence from {@link #getMentalHealthPool()} anticipated when Will
     * was first written as a simplified alias. Reuses {@link #getMentalHealthPoolBreakdown()}'s
     * terms rather than recomputing Amygdala/Tmod/Pmod, so the two formulas cannot drift apart
     * by accident on those shared terms. {@code Morality}'s neutral is 1, its own default — see
     * {@link Values}.
     */
    public AttributeBreakdown getWillBreakdown() {
        AttributeBreakdown mentalHealthPool = getMentalHealthPoolBreakdown();
        List<Double> terms = new ArrayList<>(mentalHealthPool.terms());
        terms.add(coeff().getKWillMorality() * (values().getMorality() - 1));
        return new AttributeBreakdown(mentalHealthPool.baseline(), terms);
    }

    public double getWill() {
        return getWillBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // Sensory / Hormonal / Stress (rpg-13, Balance rebuilt Delta V4)
    // -------------------------------------------------------------------------

    /**
     * Balance = baseline + kBalanceThalamus x (Thalamus-5) + kBalanceNeuralDrive x
     * (NeuralDrive-5) + kBalanceLegDrive x (LegDrive-60). Rebuilt in Delta V4: the old
     * {@code Hippocampus} and {@code TendonsAndLigaments} terms are gone (tendons already
     * factor into {@link #getLegDrive()}, which Balance now reads as a term);
     * {@code NeuralDrive} was kept per explicit user instruction rather than being replaced by
     * Agility. This is the first formula in the codebase to use another derived attribute as an
     * additive *term* (deviation from its own baseline, 60) rather than as a base like
     * {@link #getEvasion()}/{@link #getMaxMovementSpeed()} do with Speed.
     */
    public AttributeBreakdown getBalanceBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKBalanceThalamus() * (neuralSystem().getThalamus() - 5),
                coeff().getKBalanceNeuralDrive() * (neuralSystem().getNeuralDrive() - 5),
                coeff().getKBalanceLegDrive() * (getLegDrive() - 60)
        ));
    }

    public double getBalance() {
        return getBalanceBreakdown().total();
    }

    /**
     * StressResistance = baseline - kStressResistanceAmygdala x (AmygdalaAndCingulum-5) -
     * kStressResistanceAdrenal x (AdrenalGlands-5).
     */
    public AttributeBreakdown getStressResistanceBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                -coeff().getKStressResistanceAmygdala() * (neuralSystem().getAmygdalaAndCingulum() - 5),
                -coeff().getKStressResistanceAdrenal() * (bodySystems().getHormonalGlandularSystem().getAdrenalGlands() - 5)
        ));
    }

    public double getStressResistance() {
        return getStressResistanceBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // Resistance / pain threshold (Delta V4 — new attributes)
    // -------------------------------------------------------------------------

    /** AngerResistance = baseline - kAngerResistanceAmygdala x (AmygdalaAndCingulum-5). */
    public AttributeBreakdown getAngerResistanceBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                -coeff().getKAngerResistanceAmygdala() * (neuralSystem().getAmygdalaAndCingulum() - 5)
        ));
    }

    public double getAngerResistance() {
        return getAngerResistanceBreakdown().total();
    }

    /**
     * FearResistance = baseline - kFearResistanceAmygdala x (AmygdalaAndCingulum-5). Same
     * formula shape as {@link #getAngerResistance()} — kept as two separate methods/coefficients
     * per the design document, in case they diverge later.
     */
    public AttributeBreakdown getFearResistanceBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                -coeff().getKFearResistanceAmygdala() * (neuralSystem().getAmygdalaAndCingulum() - 5)
        ));
    }

    public double getFearResistance() {
        return getFearResistanceBreakdown().total();
    }

    /**
     * PainThreshold = baseline + kPainThresholdBodyFat x (BodyFat-3) + kPainThresholdSkin x
     * (SkinThickness-3) - kPainThresholdAmygdala x (AmygdalaAndCingulum-5). The design document
     * wrote the BodyFat term as a deviation from 5; confirmed with the user that BodyFat's
     * neutral is 3 everywhere else in this codebase (e.g. {@link #getDurability()}), so this
     * formula uses -3 for consistency.
     */
    public AttributeBreakdown getPainThresholdBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKPainThresholdBodyFat() * (composition().getBodyFat() - 3),
                coeff().getKPainThresholdSkin() * (bodyStructure().getSkinThickness() - 3),
                -coeff().getKPainThresholdAmygdala() * (neuralSystem().getAmygdalaAndCingulum() - 5)
        ));
    }

    public double getPainThreshold() {
        return getPainThresholdBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // Biological defense (rpg-13, cellular health term added rpg-14)
    // -------------------------------------------------------------------------

    /**
     * PoisonResistance = baseline + kPoisonResistanceImmunity x (Immunity-5) -
     * kPoisonResistanceCardiac x (CardiacOutput-5) - kPoisonResistanceBloodThickness x
     * (BloodThickness-3) + kPoisonResistanceCellularHealth x (CellularHealth-5).
     */
    public AttributeBreakdown getPoisonResistanceBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKPoisonResistanceImmunity() * (neuralSystem().getImmunity() - 5),
                -coeff().getKPoisonResistanceCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5),
                -coeff().getKPoisonResistanceBloodThickness()
                        * (bodySystems().getBloodSystem().getBloodThickness() - 3),
                coeff().getKPoisonResistanceCellularHealth() * (bodyStructure().getCellularHealth() - 5)
        ));
    }

    public double getPoisonResistance() {
        return getPoisonResistanceBreakdown().total();
    }

    /**
     * DiseaseResistance = baseline + kDiseaseResistanceImmunity x (Immunity-5) +
     * kDiseaseResistanceAmygdala x (AmygdalaAndCingulum-5) + kDiseaseResistanceCellularHealth x
     * (CellularHealth-5).
     */
    public AttributeBreakdown getDiseaseResistanceBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKDiseaseResistanceImmunity() * (neuralSystem().getImmunity() - 5),
                coeff().getKDiseaseResistanceAmygdala() * (neuralSystem().getAmygdalaAndCingulum() - 5),
                coeff().getKDiseaseResistanceCellularHealth() * (bodyStructure().getCellularHealth() - 5)
        ));
    }

    public double getDiseaseResistance() {
        return getDiseaseResistanceBreakdown().total();
    }

    /**
     * BleedingResistance = baseline + kBleedingResistanceBloodThickness x (BloodThickness-3) -
     * kBleedingResistanceCardiac x (CardiacOutput-5).
     */
    public AttributeBreakdown getBleedingResistanceBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKBleedingResistanceBloodThickness()
                        * (bodySystems().getBloodSystem().getBloodThickness() - 3),
                -coeff().getKBleedingResistanceCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5)
        ));
    }

    public double getBleedingResistance() {
        return getBleedingResistanceBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // Metabolic / survival (rpg-13)
    // -------------------------------------------------------------------------

    /**
     * ThermalResistance = baseline + kThermalResistanceSkin x (SkinThickness-3) +
     * kThermalResistanceBodyFat x (BodyFat-3) + kThermalResistanceHypothalamus x
     * (Hypothalamus-5). Natural human ceiling is 83 (SkinThickness UI-locked to 4) — the true
     * ceiling (98, SkinThickness=7) is reserved for future non-human races.
     */
    public AttributeBreakdown getThermalResistanceBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKThermalResistanceSkin() * (bodyStructure().getSkinThickness() - 3),
                coeff().getKThermalResistanceBodyFat() * (composition().getBodyFat() - 3),
                coeff().getKThermalResistanceHypothalamus() * (neuralSystem().getHypothalamus() - 5)
        ));
    }

    public double getThermalResistance() {
        return getThermalResistanceBreakdown().total();
    }

    /** BreathOutput = baseline + kBreathOutputPulmonary x (PulmonaryCapacity-5). */
    public AttributeBreakdown getBreathOutputBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKBreathOutputPulmonary() * (bodySystems().getPulmonarySystem().getPulmonaryCapacity() - 5)
        ));
    }

    public double getBreathOutput() {
        return getBreathOutputBreakdown().total();
    }

    /**
     * DehydrationResistance = baseline + kDehydrationResistanceHypothalamus x (Hypothalamus-5)
     * + kDehydrationResistanceKetosis x (KetosisQuality-5).
     */
    public AttributeBreakdown getDehydrationResistanceBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKDehydrationResistanceHypothalamus() * (neuralSystem().getHypothalamus() - 5),
                coeff().getKDehydrationResistanceKetosis()
                        * (bodySystems().getDigestiveSystem().getKetosisEfficiency() - 5)
        ));
    }

    public double getDehydrationResistance() {
        return getDehydrationResistanceBreakdown().total();
    }

    /**
     * StarvationResistance = baseline + kStarvationResistanceHypothalamus x (Hypothalamus-5) +
     * kStarvationResistanceDigestiveAbsorption x (DigestiveAbsorption-5) (renamed from
     * NutrientAbsorption, Delta V4) + kStarvationResistanceKetosis x (KetosisQuality-5).
     */
    public AttributeBreakdown getStarvationResistanceBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKStarvationResistanceHypothalamus() * (neuralSystem().getHypothalamus() - 5),
                coeff().getKStarvationResistanceDigestiveAbsorption()
                        * (bodySystems().getDigestiveSystem().getDigestiveAbsorption() - 5),
                coeff().getKStarvationResistanceKetosis()
                        * (bodySystems().getDigestiveSystem().getKetosisEfficiency() - 5)
        ));
    }

    public double getStarvationResistance() {
        return getStarvationResistanceBreakdown().total();
    }

    /**
     * FoodPoisoningAlcoholResistance = baseline + kFoodPoisoningImpurity x (ImpurityCleaning-5)
     * + kFoodPoisoningImmunity x (Immunity-5) + kFoodPoisoningCellularHealth x
     * (CellularHealth-5) - kFoodPoisoningDigestiveAbsorption x (DigestiveAbsorption-5) (new
     * term, Delta V4 — easier nutrient absorption carries a light extra exposure to
     * food-borne/alcohol effects).
     */
    public AttributeBreakdown getFoodPoisoningAlcoholResistanceBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKFoodPoisoningImpurity() * (bodySystems().getDigestiveSystem().getImpurityCleaning() - 5),
                coeff().getKFoodPoisoningImmunity() * (neuralSystem().getImmunity() - 5),
                coeff().getKFoodPoisoningCellularHealth() * (bodyStructure().getCellularHealth() - 5),
                -coeff().getKFoodPoisoningDigestiveAbsorption()
                        * (bodySystems().getDigestiveSystem().getDigestiveAbsorption() - 5)
        ));
    }

    public double getFoodPoisoningAlcoholResistance() {
        return getFoodPoisoningAlcoholResistanceBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // Load capacity (rpg-11, recalibrated rpg-12, now driven by LiftStrength — Delta V4).
    // All four figures are whole kg, matching the design document's int arithmetic. Non-linear
    // transforms of an already-resolved attribute — no breakdown.
    // -------------------------------------------------------------------------

    /**
     * MaxCapacityKg = floor(LiftStrength^2 / kMaxCapacityDivisor) + LiftStrength, computed on
     * LiftStrength truncated to an int (Delta V4 — was the old global Strength).
     * kMaxCapacityDivisor (150, rpg-12) is calibrated to work directly off a baseline-60
     * attribute — no separate offset needed.
     */
    public int getMaxCapacityKg() {
        int liftStrength = (int) getLiftStrength();
        return (int) Math.floor(Math.pow(liftStrength, 2) / coeff().getKMaxCapacityDivisor()) + liftStrength;
    }

    /** LightLoadKg = floor(MaxCapacityKg / kLightLoadDivisor) — exactly one third, no penalty. */
    public int getLightLoadKg() {
        return (int) Math.floor(getMaxCapacityKg() / coeff().getKLightLoadDivisor());
    }

    /**
     * HeavyLoadKg = floor(MaxCapacityKg x kHeavyLoadMultiplier / kHeavyLoadDivisor) — exactly
     * two thirds, the practical carry ceiling before movement is seriously hampered.
     */
    public int getHeavyLoadKg() {
        return (int) Math.floor(getMaxCapacityKg() * coeff().getKHeavyLoadMultiplier() / coeff().getKHeavyLoadDivisor());
    }

    /**
     * DragCapacityKg = kDragCapacityMultiplier x MaxCapacityKg + floor(DisplayMassKg x
     * kDragCapacityMassFraction). The only load figure that also depends on the character's
     * own real-world mass, not just LiftStrength.
     */
    public int getDragCapacityKg() {
        return (int) (coeff().getKDragCapacityMultiplier() * getMaxCapacityKg()
                + Math.floor(getDisplayMassKg() * coeff().getKDragCapacityMassFraction()));
    }

    // -------------------------------------------------------------------------
    // Body-growth rates (rpg-14) — zero-baseline, unlike every other derived attribute above.
    // These express a rate of change (can be negative), not an absolute stat value, so they
    // deliberately do NOT add BodyCoefficients.getBaseline(). A documented exception to the
    // additive standard, alongside Speed's mass penalty and Evasion/MaxMovementSpeed/Balance's
    // anchoring on another derived attribute — see .claude/skills/additive-attribute-formulas.md.
    // -------------------------------------------------------------------------

    /**
     * FatGainRate = kFatGainRateEndomorphy x (Endomorphy-5) - kFatGainRateEctomorphy x
     * (Ectomorphy-5) + kFatGainRateDigestiveAbsorption x (DigestiveAbsorption-5) (renamed from
     * NutrientAbsorption, Delta V4) - kFatGainRateKetosis x (KetosisQuality-5) -
     * kFatGainRateCellularHealth x (CellularHealth-5). Zero-baseline: positive means gaining
     * fat faster, negative means losing it faster, zero means stable at every input's neutral
     * value.
     */
    public AttributeBreakdown getFatGainRateBreakdown() {
        return new AttributeBreakdown(0, List.of(
                coeff().getKFatGainRateEndomorphy() * (genetics().getEndomorphy() - 5),
                -coeff().getKFatGainRateEctomorphy() * (genetics().getEctomorphy() - 5),
                coeff().getKFatGainRateDigestiveAbsorption()
                        * (bodySystems().getDigestiveSystem().getDigestiveAbsorption() - 5),
                -coeff().getKFatGainRateKetosis() * (bodySystems().getDigestiveSystem().getKetosisEfficiency() - 5),
                -coeff().getKFatGainRateCellularHealth() * (bodyStructure().getCellularHealth() - 5)
        ));
    }

    public double getFatGainRate() {
        return getFatGainRateBreakdown().total();
    }

    /**
     * MuscleGainRate = kMuscleGainRateMesomorphy x (Mesomorphy-5) - kMuscleGainRateEctomorphy x
     * (Ectomorphy-5) + kMuscleGainRateDigestiveAbsorption x (DigestiveAbsorption-5) (renamed
     * from NutrientAbsorption, Delta V4) + kMuscleGainRateTmod x Tmod. Zero-baseline, same
     * semantics as {@link #getFatGainRate()}.
     */
    public AttributeBreakdown getMuscleGainRateBreakdown() {
        return new AttributeBreakdown(0, List.of(
                coeff().getKMuscleGainRateMesomorphy() * (genetics().getMesomorphy() - 5),
                -coeff().getKMuscleGainRateEctomorphy() * (genetics().getEctomorphy() - 5),
                coeff().getKMuscleGainRateDigestiveAbsorption()
                        * (bodySystems().getDigestiveSystem().getDigestiveAbsorption() - 5),
                coeff().getKMuscleGainRateTmod() * testosteroneModifier()
        ));
    }

    public double getMuscleGainRate() {
        return getMuscleGainRateBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // Social attributes (rpg-14) — baseline 60, morphology/hormone-driven.
    // -------------------------------------------------------------------------

    /**
     * Intimidation = baseline - kIntimidationShapeAesthetics x (ShapeAesthetics-5) +
     * kIntimidationTmod x Tmod + kIntimidationMass x (SymbolicTotalMass-kIntimidationMassNeutral).
     * Unattractive, testosterone-driven, physically imposing characters intimidate more.
     */
    public AttributeBreakdown getIntimidationBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                -coeff().getKIntimidationShapeAesthetics() * (bodyStructure().getShapeAesthetics() - 5),
                coeff().getKIntimidationTmod() * testosteroneModifier(),
                coeff().getKIntimidationMass() * (getSymbolicTotalMass() - coeff().getKIntimidationMassNeutral())
        ));
    }

    public double getIntimidation() {
        return getIntimidationBreakdown().total();
    }

    /**
     * Diplomacy = baseline + kDiplomacyShapeAesthetics x (ShapeAesthetics-5) + kDiplomacyPmod x
     * Pmod.
     */
    public AttributeBreakdown getDiplomacyBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKDiplomacyShapeAesthetics() * (bodyStructure().getShapeAesthetics() - 5),
                coeff().getKDiplomacyPmod() * progesteroneModifier()
        ));
    }

    public double getDiplomacy() {
        return getDiplomacyBreakdown().total();
    }

    /**
     * Enfactuation = baseline + kEnfactuationShapeAesthetics x (ShapeAesthetics-5) +
     * kEnfactuationPmod x Pmod + kEnfactuationLoyalty x (Loyalty-1) (Mind pillar, new — this is
     * the divergence from {@link #getDiplomacy()} anticipated when both formulas were first
     * written: a character who values loyalty forms attachments more easily). {@code Loyalty}'s
     * neutral is 1, its own default — see {@link Values}.
     */
    public AttributeBreakdown getEnfactuationBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKEnfactuationShapeAesthetics() * (bodyStructure().getShapeAesthetics() - 5),
                coeff().getKEnfactuationPmod() * progesteroneModifier(),
                coeff().getKEnfactuationLoyalty() * (values().getLoyalty() - 1)
        ));
    }

    public double getEnfactuation() {
        return getEnfactuationBreakdown().total();
    }

    /**
     * Command = baseline + kCommandShapeAesthetics x |ShapeAesthetics-5|. V-shaped: both
     * extremes (very repulsive or very attractive) raise Command equally — commanding presence
     * comes from being memorable, not from being liked.
     */
    public AttributeBreakdown getCommandBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKCommandShapeAesthetics() * Math.abs(bodyStructure().getShapeAesthetics() - 5)
        ));
    }

    public double getCommand() {
        return getCommandBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // Arcane organs — magical races only. Each of the three organs (SubtleEpiphysealGland,
    // AstralVentriculum, NoeticPlexus) is absent (0) on the human default template, and each
    // formula reads only its own organ around a neutral point of 6 (not the usual 5) with a
    // wider weight (8) — at the absent value (0), every one of these three attributes resolves
    // to 60 - 48 = 12, reflecting a character with no capacity for magic whatsoever.
    // -------------------------------------------------------------------------

    /** ManaPool = baseline + kManaPoolEpiphyseal x (SubtleEpiphysealGland-6). */
    public AttributeBreakdown getManaPoolBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKManaPoolEpiphyseal() * (bodySystems().getHormonalGlandularSystem().getSubtleEpiphysealGland() - 6)
        ));
    }

    public double getManaPool() {
        return getManaPoolBreakdown().total();
    }

    /** ArcaneOutput = baseline + kArcaneOutputVentriculum x (AstralVentriculum-6). */
    public AttributeBreakdown getArcaneOutputBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKArcaneOutputVentriculum() * (bodySystems().getCardiacSystem().getAstralVentriculum() - 6)
        ));
    }

    public double getArcaneOutput() {
        return getArcaneOutputBreakdown().total();
    }

    /** Mediunity (renamed from SixthSense) = baseline + kMediunityNoeticPlexus x (NoeticPlexus-6). */
    public AttributeBreakdown getMediunityBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKMediunityNoeticPlexus() * (neuralSystem().getNoeticPlexus() - 6)
        ));
    }

    public double getMediunity() {
        return getMediunityBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // Concern attributes (Mind pillar, new) — direct mirrors of each Values field, not the
    // usual baseline+deviation shape. A documented exception to the additive standard: baseline
    // 0, single term equal to the raw Value (not a deviation from its neutral). Each represents
    // a character's vulnerability to stress in a situation touching that value (e.g. a high
    // AcademicConcern character is rattled by a destroyed library).
    // -------------------------------------------------------------------------

    public AttributeBreakdown getSelfConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getEgo())); }
    public double getSelfConcern() { return getSelfConcernBreakdown().total(); }

    public AttributeBreakdown getFriendshipConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getLoyalty())); }
    public double getFriendshipConcern() { return getFriendshipConcernBreakdown().total(); }

    public AttributeBreakdown getOrderConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getOrganization())); }
    public double getOrderConcern() { return getOrderConcernBreakdown().total(); }

    public AttributeBreakdown getFreedomConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getFreedom())); }
    public double getFreedomConcern() { return getFreedomConcernBreakdown().total(); }

    public AttributeBreakdown getPatriotismConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getSociety())); }
    public double getPatriotismConcern() { return getPatriotismConcernBreakdown().total(); }

    public AttributeBreakdown getSpiritualConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getDivinity())); }
    public double getSpiritualConcern() { return getSpiritualConcernBreakdown().total(); }

    public AttributeBreakdown getPhilosophyConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getTruth())); }
    public double getPhilosophyConcern() { return getPhilosophyConcernBreakdown().total(); }

    public AttributeBreakdown getAcademicConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getKnowledge())); }
    public double getAcademicConcern() { return getAcademicConcernBreakdown().total(); }

    public AttributeBreakdown getEnvironmentalismConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getNature())); }
    public double getEnvironmentalismConcern() { return getEnvironmentalismConcernBreakdown().total(); }

    public AttributeBreakdown getMoralityConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getMorality())); }
    public double getMoralityConcern() { return getMoralityConcernBreakdown().total(); }

    public AttributeBreakdown getTraditionalismConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getTradition())); }
    public double getTraditionalismConcern() { return getTraditionalismConcernBreakdown().total(); }

    public AttributeBreakdown getJusticeConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getJustice())); }
    public double getJusticeConcern() { return getJusticeConcernBreakdown().total(); }

    public AttributeBreakdown getProgressConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getProgress())); }
    public double getProgressConcern() { return getProgressConcernBreakdown().total(); }

    public AttributeBreakdown getPeaceConcernBreakdown() { return new AttributeBreakdown(0, List.of((double) values().getPeace())); }
    public double getPeaceConcern() { return getPeaceConcernBreakdown().total(); }

    // -------------------------------------------------------------------------
    // Mind-driven attributes (new) — mix of Erudition traits, Values, and existing Body
    // PhysicalTraits inputs. All baseline 60, additive-standard shape, except where noted.
    // -------------------------------------------------------------------------

    /** SurvivalSkills = baseline + kSurvivalSkillsEcology x hasEcology. */
    public AttributeBreakdown getSurvivalSkillsBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKSurvivalSkillsEcology() * flag(hasTrait(Trait.ECOLOGY))
        ));
    }

    public double getSurvivalSkills() {
        return getSurvivalSkillsBreakdown().total();
    }

    /** AnimalCaring = baseline + kAnimalCaringEcology x hasEcology + kAnimalCaringBiology x hasBiology. */
    public AttributeBreakdown getAnimalCaringBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKAnimalCaringEcology() * flag(hasTrait(Trait.ECOLOGY)),
                coeff().getKAnimalCaringBiology() * flag(hasTrait(Trait.BIOLOGY))
        ));
    }

    public double getAnimalCaring() {
        return getAnimalCaringBreakdown().total();
    }

    /** Manipulation = baseline. No modifier yet — the ability to provoke emotion in others. */
    public AttributeBreakdown getManipulationBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of());
    }

    public double getManipulation() {
        return getManipulationBreakdown().total();
    }

    /** BehaviorReading = baseline. No modifier yet. */
    public AttributeBreakdown getBehaviorReadingBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of());
    }

    public double getBehaviorReading() {
        return getBehaviorReadingBreakdown().total();
    }

    /**
     * Discretion = baseline - kDiscretionShapeAesthetics x |ShapeAesthetics-5|. Inverted-V:
     * ShapeAesthetics always penalizes Discretion regardless of direction — only a character
     * with a neutral ShapeAesthetics is discreet. Same |deviation| shape as {@link #getCommand()},
     * sign flipped.
     */
    public AttributeBreakdown getDiscretionBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                -coeff().getKDiscretionShapeAesthetics() * Math.abs(bodyStructure().getShapeAesthetics() - 5)
        ));
    }

    public double getDiscretion() {
        return getDiscretionBreakdown().total();
    }

    /** Bluffing = baseline - kBluffingTruth x (Truth-1) - kBluffingMorality x (Morality-1). */
    public AttributeBreakdown getBluffingBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                -coeff().getKBluffingTruth() * (values().getTruth() - 1),
                -coeff().getKBluffingMorality() * (values().getMorality() - 1)
        ));
    }

    public double getBluffing() {
        return getBluffingBreakdown().total();
    }

    /** Faith = baseline + kFaithDivinity x (Divinity-1). */
    public AttributeBreakdown getFaithBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKFaithDivinity() * (values().getDivinity() - 1)
        ));
    }

    public double getFaith() {
        return getFaithBreakdown().total();
    }

    /** IllusionResistanceSanity = baseline + kIllusionResistanceSanityTruth x (Truth-1). */
    public AttributeBreakdown getIllusionResistanceSanityBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKIllusionResistanceSanityTruth() * (values().getTruth() - 1)
        ));
    }

    public double getIllusionResistanceSanity() {
        return getIllusionResistanceSanityBreakdown().total();
    }

    /** Creativity = baseline + kCreativityProgress x (Progress-1). */
    public AttributeBreakdown getCreativityBreakdown() {
        return new AttributeBreakdown(coeff().getBaseline(), List.of(
                coeff().getKCreativityProgress() * (values().getProgress() - 1)
        ));
    }

    public double getCreativity() {
        return getCreativityBreakdown().total();
    }

    // -------------------------------------------------------------------------
    // Lore link
    // -------------------------------------------------------------------------

    public void linkToLore(String loreReference) {
        this.loreReference = loreReference;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getName() { return name; }
    public Body getBody() { return body; }
    public Mind getMind() { return mind; }
    public String getLoreReference() { return loreReference; }

    // -------------------------------------------------------------------------
    // Private accessors — shortcuts to reduce deep-chain noise in formulas
    // -------------------------------------------------------------------------

    private Genetics genetics() { return body.getBiomechanics().getGenetics(); }
    private BodyComposition composition() { return body.getBiomechanics().getBodyComposition(); }
    private BodySystems bodySystems() { return body.getBodySystems(); }
    private NeuralSystem neuralSystem() { return body.getBodySystems().getNeuralSystem(); }
    private PhysicalTraits physicalTraits() { return body.getPhysicalTraits(); }
    private SensorialOrgans sensorialOrgans() { return body.getPhysicalTraits().getSensorialOrgans(); }
    private BodyStructure bodyStructure() { return body.getPhysicalTraits().getBodyStructure(); }
    private BodyCoefficients coeff() { return body.getCoefficients(); }
    private Values values() { return mind.getValues(); }
    private Erudition erudition() { return mind.getErudition(); }

    private boolean hasTrait(Trait trait) { return erudition().hasTrait(trait); }

    private double flag(boolean present) { return present ? 1 : 0; }

    /**
     * Applies the shared safety floor used by the Strength-family (Push/Leg/Grip/Lift Strength,
     * SwingPower, GrapplingSelfLifting), FatigueResistance, Evasion, and MaxMovementSpeed.
     */
    private double floor(double value) {
        return Math.max(coeff().getAttributeFloor(), value);
    }

    /**
     * Testosterone modifier (rpg-14): active only when {@code PredominantMorphicHormone} is
     * below its neutral point (5) — {@code 5 - input}, ranging 1-4. Zero at or above neutral.
     */
    private int testosteroneModifier() {
        int input = bodySystems().getHormonalGlandularSystem().getPredominantMorphicHormone();
        return input < 5 ? 5 - input : 0;
    }

    /**
     * Progesterone modifier (rpg-14): active only when {@code PredominantMorphicHormone} is
     * above its neutral point (5) — {@code input - 5}, ranging 1-4. Zero at or below neutral.
     */
    private int progesteroneModifier() {
        int input = bodySystems().getHormonalGlandularSystem().getPredominantMorphicHormone();
        return input > 5 ? input - 5 : 0;
    }
}
