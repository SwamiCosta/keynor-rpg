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
                + (composition().getBoneDensity() - 5);
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
        double boneModKg = composition().getBoneDensity() - 5;
        return muscleKg + fatKg + frameKg + boneModKg;
    }

    // -------------------------------------------------------------------------
    // Biomechanics-derived attributes
    // -------------------------------------------------------------------------

    /**
     * Strength = baseline + kStrengthMuscleMass x (MuscleMass-5) + kStrengthNeuromuscular x
     * (NeuromuscularEfficiency-5) + kStrengthFiberType x (FiberType-5) + kStrengthLimbRatio x
     * (LimbRatio-3) + kStrengthMuscleDistribution x (MuscleDistribution-5) + kStrengthTendons x
     * (TendonsAndLigaments-5). Floored at {@link BodyCoefficients#getAttributeFloor()} —
     * MuscleMass's 1-15 range is asymmetric around its neutral (5), so extreme low-mass builds
     * can otherwise go negative.
     */
    public double getStrength() {
        double raw = coeff().getBaseline()
                + coeff().getKStrengthMuscleMass() * (composition().getMuscleMass() - 5)
                + coeff().getKStrengthNeuromuscular() * (neuralSystem().getNeuromuscularEfficiency() - 5)
                + coeff().getKStrengthFiberType() * (composition().getDominantFiberType() - 5)
                + coeff().getKStrengthLimbRatio() * (genetics().getLimbRatio() - 3)
                + coeff().getKStrengthMuscleDistribution() * (composition().getMuscleDistribution() - 5)
                + coeff().getKStrengthTendons() * (composition().getTendonsAndLigaments() - 5);
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
     * (Flexibility-5) + kDurabilitySkin x (SkinThickness-3). Note BodyFat's own neutral is 3,
     * not 5, same as SkinThickness's. Dense bones, a mesomorphic build, extra fat cushioning,
     * and thick skin all raise durability; high flexibility lowers it.
     */
    public double getDurability() {
        return coeff().getBaseline()
                + coeff().getKDurabilityBoneDensity() * (composition().getBoneDensity() - 5)
                + coeff().getKDurabilityMesomorphy() * (genetics().getMesomorphy() - 5)
                + coeff().getKDurabilityBodyFat() * (composition().getBodyFat() - 3)
                - coeff().getKDurabilityFlexibility() * (composition().getFlexibility() - 5)
                + coeff().getKDurabilitySkin() * (bodyStructure().getSkinThickness() - 3);
    }

    // -------------------------------------------------------------------------
    // NeuralSystem-derived attributes (spatial/accuracy — formerly SpatialIntelligence)
    // -------------------------------------------------------------------------

    /**
     * Sight = baseline + kSightEyesSensitivity x (EyesSensitivity-5) + kSightHippocampus x
     * (Hippocampus-5) + kSightNeuralDrive x (NeuralDrive-5) + kSightPmod x Pmod. Diverged from
     * {@link #getHearing()}/{@link #getSmell()} in rpg-14 — each sense now reads its own
     * {@link SensorialOrgans} input instead of sharing one shared formula.
     */
    public double getSight() {
        return coeff().getBaseline()
                + coeff().getKSightEyesSensitivity() * (sensorialOrgans().getEyesSensitivity() - 5)
                + coeff().getKSightHippocampus() * (neuralSystem().getHippocampus() - 5)
                + coeff().getKSightNeuralDrive() * (neuralSystem().getNeuralDrive() - 5)
                + coeff().getKSightPmod() * progesteroneModifier();
    }

    /**
     * Hearing = baseline + kHearingEarsSensitivity x (EarsSensitivity-5) + kHearingHippocampus
     * x (Hippocampus-5) + kHearingNeuralDrive x (NeuralDrive-5) + kHearingPmod x Pmod.
     */
    public double getHearing() {
        return coeff().getBaseline()
                + coeff().getKHearingEarsSensitivity() * (sensorialOrgans().getEarsSensitivity() - 5)
                + coeff().getKHearingHippocampus() * (neuralSystem().getHippocampus() - 5)
                + coeff().getKHearingNeuralDrive() * (neuralSystem().getNeuralDrive() - 5)
                + coeff().getKHearingPmod() * progesteroneModifier();
    }

    /**
     * Smell = baseline + kSmellNoseSensitivity x (NoseSensitivity-5) + kSmellHippocampus x
     * (Hippocampus-5) + kSmellNeuralDrive x (NeuralDrive-5) + kSmellPmod x Pmod.
     */
    public double getSmell() {
        return coeff().getBaseline()
                + coeff().getKSmellNoseSensitivity() * (sensorialOrgans().getNoseSensitivity() - 5)
                + coeff().getKSmellHippocampus() * (neuralSystem().getHippocampus() - 5)
                + coeff().getKSmellNeuralDrive() * (neuralSystem().getNeuralDrive() - 5)
                + coeff().getKSmellPmod() * progesteroneModifier();
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
     * MentalHealthPool = baseline - kMentalHealthAmygdala x (AmygdalaAndCingulum-5) -
     * kMentalHealthTmod x Tmod + kMentalHealthPmod x Pmod. {@code kMentalHealthAmygdala} was
     * reweighted 10->5 in rpg-14 alongside the two new hormone-modifier terms. Reserve for
     * future Mind-pillar mechanics — deliberately simplified until that pillar exists.
     */
    public double getMentalHealthPool() {
        return coeff().getBaseline()
                - coeff().getKMentalHealthAmygdala() * (neuralSystem().getAmygdalaAndCingulum() - 5)
                - coeff().getKMentalHealthTmod() * testosteroneModifier()
                + coeff().getKMentalHealthPmod() * progesteroneModifier();
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

    /**
     * Balance = baseline + kBalanceHippocampus x (Hippocampus-5) + kBalanceNeuralDrive x
     * (NeuralDrive-5) + kBalanceTendons x (TendonsAndLigaments-5). {@code kBalanceHippocampus}
     * was reweighted 3->1 in rpg-14 when the new tendons term was introduced.
     */
    public double getBalance() {
        return coeff().getBaseline()
                + coeff().getKBalanceHippocampus() * (neuralSystem().getHippocampus() - 5)
                + coeff().getKBalanceNeuralDrive() * (neuralSystem().getNeuralDrive() - 5)
                + coeff().getKBalanceTendons() * (composition().getTendonsAndLigaments() - 5);
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
     * (BloodThickness-3) + kPoisonResistanceCellularHealth x (CellularHealth-5) (added rpg-14).
     */
    public double getPoisonResistance() {
        return coeff().getBaseline()
                + coeff().getKPoisonResistanceImmunity() * (neuralSystem().getImmunity() - 5)
                - coeff().getKPoisonResistanceCardiac() * (bodySystems().getCardiacSystem().getCardiacOutput() - 5)
                - coeff().getKPoisonResistanceBloodThickness()
                        * (bodySystems().getBloodSystem().getBloodThickness() - 3)
                + coeff().getKPoisonResistanceCellularHealth() * (bodyStructure().getCellularHealth() - 5);
    }

    /**
     * DiseaseResistance = baseline + kDiseaseResistanceImmunity x (Immunity-5) +
     * kDiseaseResistanceAmygdala x (AmygdalaAndCingulum-5) + kDiseaseResistanceCellularHealth x
     * (CellularHealth-5) (added rpg-14).
     */
    public double getDiseaseResistance() {
        return coeff().getBaseline()
                + coeff().getKDiseaseResistanceImmunity() * (neuralSystem().getImmunity() - 5)
                + coeff().getKDiseaseResistanceAmygdala() * (neuralSystem().getAmygdalaAndCingulum() - 5)
                + coeff().getKDiseaseResistanceCellularHealth() * (bodyStructure().getCellularHealth() - 5);
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
                + coeff().getKThermalResistanceSkin() * (bodyStructure().getSkinThickness() - 3)
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
     * + kFoodPoisoningImmunity x (Immunity-5) + kFoodPoisoningCellularHealth x
     * (CellularHealth-5) (added rpg-14).
     */
    public double getFoodPoisoningAlcoholResistance() {
        return coeff().getBaseline()
                + coeff().getKFoodPoisoningImpurity() * (bodySystems().getDigestiveSystem().getImpurityCleaning() - 5)
                + coeff().getKFoodPoisoningImmunity() * (neuralSystem().getImmunity() - 5)
                + coeff().getKFoodPoisoningCellularHealth() * (bodyStructure().getCellularHealth() - 5);
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
    // Body-growth rates (rpg-14) — zero-baseline, unlike every other derived attribute above.
    // These express a rate of change (can be negative), not an absolute stat value, so they
    // deliberately do NOT add BodyCoefficients.getBaseline(). A third documented exception to
    // the additive standard, alongside Speed's mass penalty and Evasion/MaxMovementSpeed's
    // Speed-anchoring — see .claude/skills/additive-attribute-formulas.md.
    // -------------------------------------------------------------------------

    /**
     * FatGainRate = kFatGainRateEndomorphy x (Endomorphy-5) - kFatGainRateEctomorphy x
     * (Ectomorphy-5) + kFatGainRateNutrientAbsorption x (NutrientAbsorption-5) -
     * kFatGainRateKetosis x (KetosisQuality-5) - kFatGainRateCellularHealth x
     * (CellularHealth-5). Zero-baseline: positive means gaining fat faster, negative means
     * losing it faster, zero means stable at every input's neutral value.
     */
    public double getFatGainRate() {
        return coeff().getKFatGainRateEndomorphy() * (genetics().getEndomorphy() - 5)
                - coeff().getKFatGainRateEctomorphy() * (genetics().getEctomorphy() - 5)
                + coeff().getKFatGainRateNutrientAbsorption()
                        * (bodySystems().getDigestiveSystem().getNutrientAbsorption() - 5)
                - coeff().getKFatGainRateKetosis() * (bodySystems().getDigestiveSystem().getKetosisQuality() - 5)
                - coeff().getKFatGainRateCellularHealth() * (bodyStructure().getCellularHealth() - 5);
    }

    /**
     * MuscleGainRate = kMuscleGainRateMesomorphy x (Mesomorphy-5) - kMuscleGainRateEctomorphy x
     * (Ectomorphy-5) + kMuscleGainRateNutrientAbsorption x (NutrientAbsorption-5) +
     * kMuscleGainRateTmod x Tmod. Zero-baseline, same semantics as {@link #getFatGainRate()}.
     */
    public double getMuscleGainRate() {
        return coeff().getKMuscleGainRateMesomorphy() * (genetics().getMesomorphy() - 5)
                - coeff().getKMuscleGainRateEctomorphy() * (genetics().getEctomorphy() - 5)
                + coeff().getKMuscleGainRateNutrientAbsorption()
                        * (bodySystems().getDigestiveSystem().getNutrientAbsorption() - 5)
                + coeff().getKMuscleGainRateTmod() * testosteroneModifier();
    }

    // -------------------------------------------------------------------------
    // Social attributes (rpg-14) — baseline 60, morphology/hormone-driven.
    // -------------------------------------------------------------------------

    /**
     * Intimidation = baseline - kIntimidationShapeAesthetics x (ShapeAesthetics-5) +
     * kIntimidationTmod x Tmod + kIntimidationMass x (SymbolicTotalMass-kIntimidationMassNeutral).
     * Unattractive, testosterone-driven, physically imposing characters intimidate more.
     */
    public double getIntimidation() {
        return coeff().getBaseline()
                - coeff().getKIntimidationShapeAesthetics() * (bodyStructure().getShapeAesthetics() - 5)
                + coeff().getKIntimidationTmod() * testosteroneModifier()
                + coeff().getKIntimidationMass() * (getSymbolicTotalMass() - coeff().getKIntimidationMassNeutral());
    }

    /**
     * Diplomacy = baseline + kDiplomacyShapeAesthetics x (ShapeAesthetics-5) + kDiplomacyPmod x
     * Pmod.
     */
    public double getDiplomacy() {
        return coeff().getBaseline()
                + coeff().getKDiplomacyShapeAesthetics() * (bodyStructure().getShapeAesthetics() - 5)
                + coeff().getKDiplomacyPmod() * progesteroneModifier();
    }

    /**
     * Enfactuation = baseline + kEnfactuationShapeAesthetics x (ShapeAesthetics-5) +
     * kEnfactuationPmod x Pmod. Currently identical in shape to {@link #getDiplomacy()} — both
     * read the same morphology/hormone inputs today; expected to diverge once the Mind pillar
     * adds cognitive/social inputs that only one of the two should use.
     */
    public double getEnfactuation() {
        return coeff().getBaseline()
                + coeff().getKEnfactuationShapeAesthetics() * (bodyStructure().getShapeAesthetics() - 5)
                + coeff().getKEnfactuationPmod() * progesteroneModifier();
    }

    /**
     * Command = baseline + kCommandShapeAesthetics x |ShapeAesthetics-5|. V-shaped: both
     * extremes (very repulsive or very attractive) raise Command equally — commanding presence
     * comes from being memorable, not from being liked.
     */
    public double getCommand() {
        return coeff().getBaseline()
                + coeff().getKCommandShapeAesthetics() * Math.abs(bodyStructure().getShapeAesthetics() - 5);
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
    private PhysicalTraits physicalTraits() { return body.getPhysicalTraits(); }
    private SensorialOrgans sensorialOrgans() { return body.getPhysicalTraits().getSensorialOrgans(); }
    private BodyStructure bodyStructure() { return body.getPhysicalTraits().getBodyStructure(); }
    private BodyCoefficients coeff() { return body.getCoefficients(); }

    /** Applies the shared safety floor used by Strength, FatigueResistance, Evasion, and MaxMovementSpeed. */
    private double floor(double value) {
        return Math.max(coeff().getAttributeFloor(), value);
    }

    /**
     * Testosterone modifier (rpg-14): active only when {@code PredominantMorphicHormone} is
     * below its neutral point (5) — {@code 5 - input}, ranging 1-4. Zero at or above neutral.
     */
    private int testosteroneModifier() {
        int input = bodySystems().getHormonalSystem().getPredominantMorphicHormone();
        return input < 5 ? 5 - input : 0;
    }

    /**
     * Progesterone modifier (rpg-14): active only when {@code PredominantMorphicHormone} is
     * above its neutral point (5) — {@code input - 5}, ranging 1-4. Zero at or below neutral.
     */
    private int progesteroneModifier() {
        int input = bodySystems().getHormonalSystem().getPredominantMorphicHormone();
        return input > 5 ? input - 5 : 0;
    }
}
