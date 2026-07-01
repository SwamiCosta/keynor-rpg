package com.keynor.rpg.domain.model;

/**
 * Aggregate root for a playable character. Holds the {@link Body} pillar (wound tree +
 * data groups) and exposes all derived physical attribute formulas. Formulas combine
 * inputs from {@link Biomechanics} (genetics + body composition), {@link BodySystems}
 * (cardiovascular and neural systems), and {@link SpatialIntelligence} (spatial awareness
 * group) — none of these groups owns the formulas themselves.
 *
 * <p><b>Additive standard (rpg-11):</b> every derived attribute is
 * {@code baseline + sum(weight x (input - neutral))} — the previous multiplicative model
 * (square-cube law, power-to-weight ratios, logarithms) is fully replaced. {@code baseline}
 * is 60 (see {@link BodyCoefficients#getBaseline()}). Most inputs are 1-9 with neutral 5;
 * {@code limbRatio} is 1-5 with neutral 3; {@code bodyFat}'s own neutral is 3 (not 5) inside
 * {@link #getDurability()}. {@code height}/{@code muscleMass}/{@code bodyFat} additionally
 * feed {@link #getSymbolicTotalMass()}/{@link #getDisplayMassKg()} directly (not as
 * deviations). See {@code .claude/skills/additive-attribute-formulas.md} for the full design
 * rationale.
 *
 * <p>All formula coefficients are tunable via {@link Body#getCoefficients()} without
 * modifying any formula code. Default coefficients are not balanced game data — tune
 * through play.
 */
public class PlayableCharacter {

    private final String name;
    private final Body body;
    private String loreReference;

    public PlayableCharacter(String name, Body body) {
        this.name = name;
        this.body = body;
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
                + (genetics().getBoneDensity() - 5);
        return (int) Math.round(raw);
    }

    /**
     * DisplayMassKg = MuscleKg + FatKg + FrameKg + BoneModKg. UI-facing real-world mass —
     * never used by gameplay formulas directly except as an input to
     * {@link #getDragCapacityKg()}, which mixes it with {@link #getStrength()}.
     */
    public double getDisplayMassKg() {
        double muscleKg = composition().getMuscleMass() * coeff().getKMuscleKgMultiplier()
                + coeff().getKMuscleKgOffset();
        double fatKg = composition().getBodyFat() * coeff().getKFatKgMultiplier();
        double frameKg = coeff().getKFrameKgBase()
                + Math.floor((genetics().getHeight() * coeff().getKFrameKgHeightMultiplier())
                        / coeff().getKFrameKgDivisor());
        double boneModKg = genetics().getBoneDensity() - 5;
        return muscleKg + fatKg + frameKg + boneModKg;
    }

    // -------------------------------------------------------------------------
    // Biomechanics-derived attributes
    // -------------------------------------------------------------------------

    /**
     * Strength = baseline + kStrengthMuscleMass x (MuscleMass-5) + kStrengthNeuromuscular x
     * (NeuromuscularEfficiency-5) + kStrengthFiberType x (FiberType-5) + kStrengthLimbRatio x
     * (LimbRatio-3) + kStrengthMuscleDistribution x (MuscleDistribution-5). Floored at
     * {@link BodyCoefficients#getAttributeFloor()} — MuscleMass's 1-15 range is asymmetric
     * around its neutral (5), so extreme low-mass builds can otherwise go negative.
     */
    public double getStrength() {
        double raw = coeff().getBaseline()
                + coeff().getKStrengthMuscleMass() * (composition().getMuscleMass() - 5)
                + coeff().getKStrengthNeuromuscular() * (nervousSystem().getNeuromuscularEfficiency() - 5)
                + coeff().getKStrengthFiberType() * (composition().getDominantFiberType() - 5)
                + coeff().getKStrengthLimbRatio() * (genetics().getLimbRatio() - 3)
                + coeff().getKStrengthMuscleDistribution() * (composition().getMuscleDistribution() - 5);
        return floor(raw);
    }

    /**
     * Speed = baseline + kSpeedNeuromuscular x (NeuromuscularEfficiency-5) + kSpeedMuscleMass x
     * (MuscleMass-5) + kSpeedFiberType x (FiberType-5) - floor((SymbolicTotalMass -
     * kSpeedMassNeutral) / kSpeedMassDivisor). The divisor-3 mass penalty (rpg-11 revision)
     * keeps the worst-case result positive (minimum ~27 at baseline 60) without needing a
     * floor, unlike {@link #getStrength()}.
     */
    public double getSpeed() {
        double massPenalty = Math.floor(
                (getSymbolicTotalMass() - coeff().getKSpeedMassNeutral()) / coeff().getKSpeedMassDivisor());
        return coeff().getBaseline()
                + coeff().getKSpeedNeuromuscular() * (nervousSystem().getNeuromuscularEfficiency() - 5)
                + coeff().getKSpeedMuscleMass() * (composition().getMuscleMass() - 5)
                + coeff().getKSpeedFiberType() * (composition().getDominantFiberType() - 5)
                - massPenalty;
    }

    /**
     * MaxMovementSpeed = Speed + kMaxMovementSpeedLimbRatio x (LimbRatio-3) -
     * kMaxMovementSpeedMuscleDistribution x (MuscleDistribution-5). Displacement/travel speed,
     * extended from Speed with a stride-length term (longer limbs help, shorter limbs hurt)
     * and a muscle-distribution term (leg-bias helps, arm-bias hurts). Floored.
     */
    public double getMaxMovementSpeed() {
        double raw = getSpeed()
                + coeff().getKMaxMovementSpeedLimbRatio() * (genetics().getLimbRatio() - 3)
                - coeff().getKMaxMovementSpeedMuscleDistribution() * (composition().getMuscleDistribution() - 5);
        return floor(raw);
    }

    /**
     * StaminaPool = baseline + kStaminaPoolPulmonary x (PulmonaryCapacity-5) +
     * kStaminaPoolCardiac x (CardiacOutput-5) + kStaminaPoolOxygen x
     * (OxygenCarryingCapacity-5) - kStaminaPoolFiberType x (FiberType-5). Pulmonary capacity
     * is the leading term; fast-twitch fiber bias lowers the pool.
     */
    public double getStaminaPool() {
        return coeff().getBaseline()
                + coeff().getKStaminaPoolPulmonary() * (bodySystems().getPulmonarySystem().getPulmonaryCapacity() - 5)
                + coeff().getKStaminaPoolCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5)
                + coeff().getKStaminaPoolOxygen() * (bodySystems().getBloodSystem().getOxygenCarryingCapacity() - 5)
                - coeff().getKStaminaPoolFiberType() * (composition().getDominantFiberType() - 5);
    }

    /**
     * FatigueResistance = baseline + kFatigueResistanceCardiac x (CardiacOutput-5) +
     * kFatigueResistancePulmonary x (PulmonaryCapacity-5) + kFatigueResistanceOxygen x
     * (OxygenCarryingCapacity-5) - kFatigueResistanceNeuromuscular x
     * (NeuromuscularEfficiency-5) - floor((SymbolicTotalMass - kFatigueResistanceMassNeutral) /
     * kFatigueResistanceMassDivisor) - kFatigueResistanceMuscleMass x (MuscleMass-5). Cardiac
     * output leads; high neuromuscular efficiency, heavy mass, and high muscle mass all cause
     * wear that lowers resistance. Floored — replaces the old (removed) {@code FatigueRate},
     * with inverted semantics: higher is now better.
     */
    public double getFatigueResistance() {
        double massPenalty = Math.floor((getSymbolicTotalMass() - coeff().getKFatigueResistanceMassNeutral())
                / coeff().getKFatigueResistanceMassDivisor());
        double raw = coeff().getBaseline()
                + coeff().getKFatigueResistanceCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5)
                + coeff().getKFatigueResistancePulmonary()
                        * (bodySystems().getPulmonarySystem().getPulmonaryCapacity() - 5)
                + coeff().getKFatigueResistanceOxygen()
                        * (bodySystems().getBloodSystem().getOxygenCarryingCapacity() - 5)
                - coeff().getKFatigueResistanceNeuromuscular() * (nervousSystem().getNeuromuscularEfficiency() - 5)
                - massPenalty
                - coeff().getKFatigueResistanceMuscleMass() * (composition().getMuscleMass() - 5);
        return floor(raw);
    }

    /**
     * StaminaRecovery = baseline + kStaminaRecoveryOxygen x (OxygenCarryingCapacity-5) +
     * kStaminaRecoveryPulmonary x (PulmonaryCapacity-5) + kStaminaRecoveryCardiac x
     * (CardiacOutput-5) - kStaminaRecoveryFiberType x (FiberType-5). New attribute (rpg-11):
     * blood oxygenation leads recovery speed; slow-twitch fiber bias gives a bonus.
     */
    public double getStaminaRecovery() {
        return coeff().getBaseline()
                + coeff().getKStaminaRecoveryOxygen() * (bodySystems().getBloodSystem().getOxygenCarryingCapacity() - 5)
                + coeff().getKStaminaRecoveryPulmonary()
                        * (bodySystems().getPulmonarySystem().getPulmonaryCapacity() - 5)
                + coeff().getKStaminaRecoveryCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5)
                - coeff().getKStaminaRecoveryFiberType() * (composition().getDominantFiberType() - 5);
    }

    /**
     * Durability = baseline + kDurabilityBoneDensity x (BoneDensity-5) + kDurabilityMesomorphy
     * x (Mesomorphy-5) + kDurabilityBodyFat x (BodyFat-3) - kDurabilityFlexibility x
     * (Flexibility-5). Note BodyFat's own neutral is 3, not 5. Dense bones, a mesomorphic
     * build, and extra fat cushioning raise durability; high flexibility lowers it.
     */
    public double getDurability() {
        return coeff().getBaseline()
                + coeff().getKDurabilityBoneDensity() * (genetics().getBoneDensity() - 5)
                + coeff().getKDurabilityMesomorphy() * (genetics().getMesomorphy() - 5)
                + coeff().getKDurabilityBodyFat() * (composition().getBodyFat() - 3)
                - coeff().getKDurabilityFlexibility() * (composition().getFlexibility() - 5);
    }

    // -------------------------------------------------------------------------
    // SpatialIntelligence-derived attributes
    // -------------------------------------------------------------------------

    /**
     * Sight = baseline + kSensePerception x (Perception-5) + kSenseNeuralDrive x
     * (NeuralDrive-5). Shares the same formula as {@link #getHearing()} and
     * {@link #getSmell()} — each can be trained independently in the future.
     */
    public double getSight() {
        return coeff().getBaseline()
                + coeff().getKSensePerception() * (spatialIntelligence().getPerception() - 5)
                + coeff().getKSenseNeuralDrive() * (nervousSystem().getNeuralDrive() - 5);
    }

    /** Hearing — same base formula as {@link #getSight()}. */
    public double getHearing() {
        return getSight();
    }

    /** Smell — same base formula as {@link #getSight()}. */
    public double getSmell() {
        return getSight();
    }

    /**
     * Evasion = Speed + kEvasionAgility x (Agility-5) + kEvasionNeuralDrive x (NeuralDrive-5) +
     * kEvasionFlexibility x (Flexibility-5). Anchored directly on final movement speed.
     * Floored.
     */
    public double getEvasion() {
        double raw = getSpeed()
                + coeff().getKEvasionAgility() * (spatialIntelligence().getAgility() - 5)
                + coeff().getKEvasionNeuralDrive() * (nervousSystem().getNeuralDrive() - 5)
                + coeff().getKEvasionFlexibility() * (composition().getFlexibility() - 5);
        return floor(raw);
    }

    /**
     * Acrobatics = baseline + kAcrobaticsAgility x (Agility-5) + kAcrobaticsFlexibility x
     * (Flexibility-5).
     */
    public double getAcrobatics() {
        return coeff().getBaseline()
                + coeff().getKAcrobaticsAgility() * (spatialIntelligence().getAgility() - 5)
                + coeff().getKAcrobaticsFlexibility() * (composition().getFlexibility() - 5);
    }

    /**
     * MeleeAccuracy = baseline + kMeleeAccuracyPrecision x (Precision-5) +
     * kMeleeAccuracyAgility x (Agility-5).
     */
    public double getMeleeAccuracy() {
        return coeff().getBaseline()
                + coeff().getKMeleeAccuracyPrecision() * (spatialIntelligence().getPrecision() - 5)
                + coeff().getKMeleeAccuracyAgility() * (spatialIntelligence().getAgility() - 5);
    }

    /**
     * Aim = baseline + kAimPrecision x (Precision-5) + kAimPerception x (Perception-5).
     */
    public double getAim() {
        return coeff().getBaseline()
                + coeff().getKAimPrecision() * (spatialIntelligence().getPrecision() - 5)
                + coeff().getKAimPerception() * (spatialIntelligence().getPerception() - 5);
    }

    // -------------------------------------------------------------------------
    // Load capacity (rpg-11) — derived from Strength and DisplayMassKg
    // -------------------------------------------------------------------------

    /**
     * MaxCapacityKg = floor(Strength^2 / kMaxCapacityDivisor) + Strength. Non-linear so
     * heroic Strength values yield disproportionately large capacity.
     */
    public double getMaxCapacityKg() {
        double strength = getStrength();
        return Math.floor(Math.pow(strength, 2) / coeff().getKMaxCapacityDivisor()) + strength;
    }

    /** LightLoadKg = MaxCapacityKg x kLightLoadFraction. */
    public double getLightLoadKg() {
        return getMaxCapacityKg() * coeff().getKLightLoadFraction();
    }

    /** HeavyLoadKg = MaxCapacityKg x kHeavyLoadFraction — practical carry ceiling. */
    public double getHeavyLoadKg() {
        return getMaxCapacityKg() * coeff().getKHeavyLoadFraction();
    }

    /**
     * DragCapacityKg = kDragCapacityMultiplier x MaxCapacityKg + floor(DisplayMassKg x
     * kDragCapacityMassFraction). The only load figure that also depends on the character's
     * own real-world mass, not just Strength.
     */
    public double getDragCapacityKg() {
        return coeff().getKDragCapacityMultiplier() * getMaxCapacityKg()
                + Math.floor(getDisplayMassKg() * coeff().getKDragCapacityMassFraction());
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
    public String getLoreReference() { return loreReference; }

    // -------------------------------------------------------------------------
    // Private accessors — shortcuts to reduce deep-chain noise in formulas
    // -------------------------------------------------------------------------

    private Genetics genetics() { return body.getBiomechanics().getGenetics(); }
    private BodyComposition composition() { return body.getBiomechanics().getBodyComposition(); }
    private BodySystems bodySystems() { return body.getBodySystems(); }
    private NervousSystem nervousSystem() { return body.getBodySystems().getNervousSystem(); }
    private SpatialIntelligence spatialIntelligence() { return body.getSpatialIntelligence(); }
    private BodyCoefficients coeff() { return body.getCoefficients(); }

    /** Applies the shared safety floor used by Strength, FatigueResistance, Evasion, and MaxMovementSpeed. */
    private double floor(double value) {
        return Math.max(coeff().getAttributeFloor(), value);
    }
}
