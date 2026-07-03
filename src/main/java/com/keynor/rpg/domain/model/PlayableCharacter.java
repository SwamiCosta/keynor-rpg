package com.keynor.rpg.domain.model;

/**
 * Aggregate root for a playable character. Holds the {@link Body} pillar (wound tree +
 * data groups) and exposes all derived physical attribute formulas. Formulas combine inputs
 * from {@link Biomechanics} (genetics + body composition) and {@link BodySystems}
 * (cardiovascular, neural, hormonal, and digestive systems) — none of these groups owns the
 * formulas themselves. {@link NeuralSystem} absorbed the former {@code SpatialIntelligence}
 * group in rpg-13 (perception/agility/precision now live there as
 * hippocampus/agility/precision).
 *
 * <p><b>Additive standard (rpg-11, extended rpg-13):</b> every derived attribute is
 * {@code baseline + sum(weight x (input - neutral))} — the previous multiplicative model
 * (square-cube law, power-to-weight ratios, logarithms) is fully replaced. {@code baseline}
 * is 60 (see {@link BodyCoefficients#getBaseline()}). Most inputs are 1-9 with neutral 5;
 * {@code limbRatio} is 1-5 with neutral 3; {@code bodyFat}'s own neutral is 3 (not 5) inside
 * {@link #getDurability()}; {@code bloodThickness} is 1-5 neutral 3; {@code skinThickness} is
 * 1-7 neutral 3. {@code height}/{@code muscleMass}/{@code bodyFat} additionally feed
 * {@link #getSymbolicTotalMass()}/{@link #getDisplayMassKg()} directly (not as deviations).
 * See {@code .claude/skills/additive-attribute-formulas.md} for the full design rationale.
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
                + coeff().getKStrengthNeuromuscular() * (neuralSystem().getNeuromuscularEfficiency() - 5)
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
                + coeff().getKSpeedNeuromuscular() * (neuralSystem().getNeuromuscularEfficiency() - 5)
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
     * (OxygenCarryingCapacity-5) - kStaminaPoolFiberType x (FiberType-5) +
     * kStaminaPoolNutrientAbsorption x (NutrientAbsorption-5). Pulmonary capacity is the
     * leading term; fast-twitch fiber bias lowers the pool; efficient nutrient absorption
     * (added rpg-13) raises it.
     */
    public double getStaminaPool() {
        return coeff().getBaseline()
                + coeff().getKStaminaPoolPulmonary() * (bodySystems().getPulmonarySystem().getPulmonaryCapacity() - 5)
                + coeff().getKStaminaPoolCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5)
                + coeff().getKStaminaPoolOxygen() * (bodySystems().getBloodSystem().getOxygenCarryingCapacity() - 5)
                - coeff().getKStaminaPoolFiberType() * (composition().getDominantFiberType() - 5)
                + coeff().getKStaminaPoolNutrientAbsorption()
                        * (bodySystems().getDigestiveSystem().getNutrientAbsorption() - 5);
    }

    /**
     * FatigueResistance = baseline + kFatigueResistanceCardiac x (CardiacOutput-5) +
     * kFatigueResistancePulmonary x (PulmonaryCapacity-5) + kFatigueResistanceOxygen x
     * (OxygenCarryingCapacity-5) - kFatigueResistanceNeuromuscular x
     * (NeuromuscularEfficiency-5) - floor((SymbolicTotalMass - kFatigueResistanceMassNeutral) /
     * kFatigueResistanceMassDivisor) - kFatigueResistanceMuscleMass x (MuscleMass-5) +
     * kFatigueResistanceHypothalamus x (Hypothalamus-5) + kFatigueResistanceThyroid x
     * (Thyroid-5). Cardiac output leads; high neuromuscular efficiency, heavy mass, and high
     * muscle mass all cause wear that lowers resistance; homeostatic (Hypothalamus) and
     * metabolic-rate (Thyroid) control raise it (added rpg-13). Floored — replaces the old
     * (removed) {@code FatigueRate}, with inverted semantics: higher is now better.
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
                - coeff().getKFatigueResistanceNeuromuscular() * (neuralSystem().getNeuromuscularEfficiency() - 5)
                - massPenalty
                - coeff().getKFatigueResistanceMuscleMass() * (composition().getMuscleMass() - 5)
                + coeff().getKFatigueResistanceHypothalamus() * (neuralSystem().getHypothalamus() - 5)
                + coeff().getKFatigueResistanceThyroid() * (bodySystems().getHormonalSystem().getThyroid() - 5);
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
    // NeuralSystem-derived attributes (spatial/accuracy — formerly SpatialIntelligence)
    // -------------------------------------------------------------------------

    /**
     * Sight = baseline + kSensePerception x (Perception-5) + kSenseNeuralDrive x
     * (NeuralDrive-5). Shares the same formula as {@link #getHearing()} and
     * {@link #getSmell()} — each can be trained independently in the future.
     */
    public double getSight() {
        return coeff().getBaseline()
                + coeff().getKSensePerception() * (neuralSystem().getHippocampus() - 5)
                + coeff().getKSenseNeuralDrive() * (neuralSystem().getNeuralDrive() - 5);
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
                + coeff().getKEvasionAgility() * (neuralSystem().getAgility() - 5)
                + coeff().getKEvasionNeuralDrive() * (neuralSystem().getNeuralDrive() - 5)
                + coeff().getKEvasionFlexibility() * (composition().getFlexibility() - 5);
        return floor(raw);
    }

    /**
     * Acrobatics = baseline + kAcrobaticsAgility x (Agility-5) + kAcrobaticsFlexibility x
     * (Flexibility-5).
     */
    public double getAcrobatics() {
        return coeff().getBaseline()
                + coeff().getKAcrobaticsAgility() * (neuralSystem().getAgility() - 5)
                + coeff().getKAcrobaticsFlexibility() * (composition().getFlexibility() - 5);
    }

    /**
     * MeleeAccuracy = baseline + kMeleeAccuracyPrecision x (Precision-5) +
     * kMeleeAccuracyAgility x (Agility-5).
     */
    public double getMeleeAccuracy() {
        return coeff().getBaseline()
                + coeff().getKMeleeAccuracyPrecision() * (neuralSystem().getPrecision() - 5)
                + coeff().getKMeleeAccuracyAgility() * (neuralSystem().getAgility() - 5);
    }

    /**
     * Aim = baseline + kAimPrecision x (Precision-5) + kAimPerception x (Perception-5).
     */
    public double getAim() {
        return coeff().getBaseline()
                + coeff().getKAimPrecision() * (neuralSystem().getPrecision() - 5)
                + coeff().getKAimPerception() * (neuralSystem().getHippocampus() - 5);
    }

    // -------------------------------------------------------------------------
    // Cognitive / Mental (rpg-13) — NeuralSystem-derived
    // -------------------------------------------------------------------------

    /** MemoryPool = baseline + kMemoryPoolCerebral x (CerebralCapacity-5) + kMemoryPoolHippocampus x (Hippocampus-5). */
    public double getMemoryPool() {
        return coeff().getBaseline()
                + coeff().getKMemoryPoolCerebral() * (neuralSystem().getCerebralCapacity() - 5)
                + coeff().getKMemoryPoolHippocampus() * (neuralSystem().getHippocampus() - 5);
    }

    /** Reasoning = baseline + kReasoningSynapsis x (SynapsisQuality-5). */
    public double getReasoning() {
        return coeff().getBaseline()
                + coeff().getKReasoningSynapsis() * (neuralSystem().getSynapsisQuality() - 5);
    }

    /**
     * ShortMemory = baseline + kShortMemoryCerebral x (CerebralCapacity-5) +
     * kShortMemorySynapsis x (SynapsisQuality-5) + kShortMemoryHippocampus x (Hippocampus-5).
     */
    public double getShortMemory() {
        return coeff().getBaseline()
                + coeff().getKShortMemoryCerebral() * (neuralSystem().getCerebralCapacity() - 5)
                + coeff().getKShortMemorySynapsis() * (neuralSystem().getSynapsisQuality() - 5)
                + coeff().getKShortMemoryHippocampus() * (neuralSystem().getHippocampus() - 5);
    }

    /**
     * MentalHealthPool = baseline - kMentalHealthAmygdala x (AmygdalaAndCingulum-5). Reserve
     * for future Mind-pillar mechanics — deliberately simplified until that pillar exists.
     */
    public double getMentalHealthPool() {
        return coeff().getBaseline()
                - coeff().getKMentalHealthAmygdala() * (neuralSystem().getAmygdalaAndCingulum() - 5);
    }

    /**
     * Will — same formula as {@link #getMentalHealthPool()} for now; expected to diverge once
     * the Mind pillar is implemented.
     */
    public double getWill() {
        return getMentalHealthPool();
    }

    // -------------------------------------------------------------------------
    // Sensory / Hormonal / Stress (rpg-13)
    // -------------------------------------------------------------------------

    /** Balance = baseline + kBalanceHippocampus x (Hippocampus-5) + kBalanceNeuralDrive x (NeuralDrive-5). */
    public double getBalance() {
        return coeff().getBaseline()
                + coeff().getKBalanceHippocampus() * (neuralSystem().getHippocampus() - 5)
                + coeff().getKBalanceNeuralDrive() * (neuralSystem().getNeuralDrive() - 5);
    }

    /**
     * StressResistance = baseline - kStressResistanceAmygdala x (AmygdalaAndCingulum-5) -
     * kStressResistanceAdrenal x (AdrenalGlands-5).
     */
    public double getStressResistance() {
        return coeff().getBaseline()
                - coeff().getKStressResistanceAmygdala() * (neuralSystem().getAmygdalaAndCingulum() - 5)
                - coeff().getKStressResistanceAdrenal() * (bodySystems().getHormonalSystem().getAdrenalGlands() - 5);
    }

    // -------------------------------------------------------------------------
    // Biological defense (rpg-13)
    // -------------------------------------------------------------------------

    /**
     * PoisonResistance = baseline + kPoisonResistanceImmunity x (Immunity-5) -
     * kPoisonResistanceCardiac x (CardiacOutput-5) - kPoisonResistanceBloodThickness x
     * (BloodThickness-3).
     */
    public double getPoisonResistance() {
        return coeff().getBaseline()
                + coeff().getKPoisonResistanceImmunity() * (neuralSystem().getImmunity() - 5)
                - coeff().getKPoisonResistanceCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5)
                - coeff().getKPoisonResistanceBloodThickness()
                        * (bodySystems().getBloodSystem().getBloodThickness() - 3);
    }

    /**
     * DiseaseResistance = baseline + kDiseaseResistanceImmunity x (Immunity-5) +
     * kDiseaseResistanceAmygdala x (AmygdalaAndCingulum-5).
     */
    public double getDiseaseResistance() {
        return coeff().getBaseline()
                + coeff().getKDiseaseResistanceImmunity() * (neuralSystem().getImmunity() - 5)
                + coeff().getKDiseaseResistanceAmygdala() * (neuralSystem().getAmygdalaAndCingulum() - 5);
    }

    /**
     * BleedingResistance = baseline + kBleedingResistanceBloodThickness x (BloodThickness-3) -
     * kBleedingResistanceCardiac x (CardiacOutput-5).
     */
    public double getBleedingResistance() {
        return coeff().getBaseline()
                + coeff().getKBleedingResistanceBloodThickness()
                        * (bodySystems().getBloodSystem().getBloodThickness() - 3)
                - coeff().getKBleedingResistanceCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5);
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
    public double getThermalResistance() {
        return coeff().getBaseline()
                + coeff().getKThermalResistanceSkin() * (genetics().getSkinThickness() - 3)
                + coeff().getKThermalResistanceBodyFat() * (composition().getBodyFat() - 3)
                + coeff().getKThermalResistanceHypothalamus() * (neuralSystem().getHypothalamus() - 5);
    }

    /** BreathOutput = baseline + kBreathOutputPulmonary x (PulmonaryCapacity-5). */
    public double getBreathOutput() {
        return coeff().getBaseline()
                + coeff().getKBreathOutputPulmonary() * (bodySystems().getPulmonarySystem().getPulmonaryCapacity() - 5);
    }

    /**
     * DehydrationResistance = baseline + kDehydrationResistanceHypothalamus x (Hypothalamus-5)
     * + kDehydrationResistanceKetosis x (KetosisQuality-5).
     */
    public double getDehydrationResistance() {
        return coeff().getBaseline()
                + coeff().getKDehydrationResistanceHypothalamus() * (neuralSystem().getHypothalamus() - 5)
                + coeff().getKDehydrationResistanceKetosis()
                        * (bodySystems().getDigestiveSystem().getKetosisQuality() - 5);
    }

    /**
     * StarvationResistance = baseline + kStarvationResistanceHypothalamus x (Hypothalamus-5) +
     * kStarvationResistanceNutrient x (NutrientAbsorption-5) + kStarvationResistanceKetosis x
     * (KetosisQuality-5).
     */
    public double getStarvationResistance() {
        return coeff().getBaseline()
                + coeff().getKStarvationResistanceHypothalamus() * (neuralSystem().getHypothalamus() - 5)
                + coeff().getKStarvationResistanceNutrient()
                        * (bodySystems().getDigestiveSystem().getNutrientAbsorption() - 5)
                + coeff().getKStarvationResistanceKetosis()
                        * (bodySystems().getDigestiveSystem().getKetosisQuality() - 5);
    }

    /**
     * FoodPoisoningAlcoholResistance = baseline + kFoodPoisoningImpurity x (ImpurityCleaning-5)
     * + kFoodPoisoningImmunity x (Immunity-5).
     */
    public double getFoodPoisoningAlcoholResistance() {
        return coeff().getBaseline()
                + coeff().getKFoodPoisoningImpurity() * (bodySystems().getDigestiveSystem().getImpurityCleaning() - 5)
                + coeff().getKFoodPoisoningImmunity() * (neuralSystem().getImmunity() - 5);
    }

    // -------------------------------------------------------------------------
    // Load capacity (rpg-11, recalibrated rpg-12) — derived from Strength and DisplayMassKg.
    // All four figures are whole kg, matching the design document's int arithmetic.
    // -------------------------------------------------------------------------

    /**
     * MaxCapacityKg = floor(Strength^2 / kMaxCapacityDivisor) + Strength, computed on
     * Strength truncated to an int. kMaxCapacityDivisor (150, rpg-12) is calibrated to work
     * directly off the baseline-60 Strength — no separate offset needed, superseding the
     * short-lived rpg-11 kLoadCapacityStrengthOffset correction. Non-linear so heroic
     * Strength values yield disproportionately large capacity.
     */
    public int getMaxCapacityKg() {
        int strength = (int) getStrength();
        return (int) Math.floor(Math.pow(strength, 2) / coeff().getKMaxCapacityDivisor()) + strength;
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
     * own real-world mass, not just Strength.
     */
    public int getDragCapacityKg() {
        return (int) (coeff().getKDragCapacityMultiplier() * getMaxCapacityKg()
                + Math.floor(getDisplayMassKg() * coeff().getKDragCapacityMassFraction()));
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
    private NeuralSystem neuralSystem() { return body.getBodySystems().getNeuralSystem(); }
    private BodyCoefficients coeff() { return body.getCoefficients(); }

    /** Applies the shared safety floor used by Strength, FatigueResistance, Evasion, and MaxMovementSpeed. */
    private double floor(double value) {
        return Math.max(coeff().getAttributeFloor(), value);
    }
}
