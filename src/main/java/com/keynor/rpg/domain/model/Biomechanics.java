package com.keynor.rpg.domain.model;

/**
 * Aggregates the genetic and trainable layers of a {@link Body}'s physical attributes,
 * plus the output formulas derived from them. {@code geneticPoints} and {@code trainingPoints}
 * are illustrative placeholder budgets wiring up the points-economy concept — not balanced
 * game data. Cardiovascular capacity has no stored field: it is always the live average of
 * {@link BloodSystem}, {@link CardiacSystem} and {@link PulmonarySystem}.
 */
public class Biomechanics {

    private final Genetics genetics;
    private final BloodSystem bloodSystem;
    private final BodyComposition bodyComposition;
    private final NervousSystem nervousSystem;
    private final CardiacSystem cardiacSystem;
    private final PulmonarySystem pulmonarySystem;
    private final AttributePointBudget geneticPoints;
    private final AttributePointBudget trainingPoints;
    private final BiomechanicsBalance balance;

    public Biomechanics(Genetics genetics, BloodSystem bloodSystem, BodyComposition bodyComposition,
                         NervousSystem nervousSystem, CardiacSystem cardiacSystem, PulmonarySystem pulmonarySystem,
                         AttributePointBudget geneticPoints, AttributePointBudget trainingPoints,
                         BiomechanicsBalance balance) {
        this.genetics = genetics;
        this.bloodSystem = bloodSystem;
        this.bodyComposition = bodyComposition;
        this.nervousSystem = nervousSystem;
        this.cardiacSystem = cardiacSystem;
        this.pulmonarySystem = pulmonarySystem;
        this.geneticPoints = geneticPoints;
        this.trainingPoints = trainingPoints;
        this.balance = balance;
    }

    public static Biomechanics humanDefaults() {
        return new Biomechanics(Genetics.defaults(), BloodSystem.defaults(), BodyComposition.defaults(),
                NervousSystem.defaults(), CardiacSystem.defaults(), PulmonarySystem.defaults(),
                new AttributePointBudget(20), new AttributePointBudget(20), BiomechanicsBalance.defaults());
    }

    /**
     * Resultant value, per design doc: average quality of blood, cardiac and pulmonary systems.
     */
    public double getCardiovascularCapacity() {
        return (bloodSystem.getOxygenCarryingCapacity() + cardiacSystem.getCardiacOutput()
                + pulmonarySystem.getPulmonaryCapacity()) / 3.0;
    }

    /**
     * BoneMass = kBoneMass x (Height/100)^2 x (1 + kBoneDensity x (BoneDensity - 5)). The
     * height term follows the same square-cube intuition as {@link #getStrength()}'s mass
     * terms; the density term is a deviation from the mid-range default (5), not an absolute
     * multiplier, so {@code boneDensity = 0} does not collapse bone mass to zero.
     */
    public double getBoneMass() {
        double heightMeters = genetics.getHeight() / 100.0;
        return balance.getKBoneMass() * Math.pow(heightMeters, 2)
                * (1 + balance.getKBoneDensity() * (genetics.getBoneDensity() - 5));
    }

    /**
     * OrganWaterMass = kOrganWaterMass x (Height/100)^2. Covers organs, blood, skin and water —
     * mass that {@code bodyFat + muscleMass + boneMass} structurally excludes. Scales only with
     * height, not density: there is no genetic trait modeling organ size.
     */
    public double getOrganWaterMass() {
        double heightMeters = genetics.getHeight() / 100.0;
        return balance.getKOrganWaterMass() * Math.pow(heightMeters, 2);
    }

    /**
     * TotalMass = BodyFat + MuscleMass + BoneMass + OrganWaterMass — fully derived from the
     * trainable layer's player-set fields plus the two height/density-driven components above.
     */
    public double getTotalMass() {
        return bodyComposition.getBodyFat() + bodyComposition.getMuscleMass()
                + getBoneMass() + getOrganWaterMass();
    }

    /**
     * Strength = k1 x MuscleMass^(2/3) x (1 + 0.3 x FiberType) x NeuromuscularEfficiency x LeverageF
     * x MuscleDistributionF, LeverageF = 1 + c x (LimbRatio - 1). The 2/3 exponent is the
     * square-cube law: cross-sectional area (force) scales slower than volume (mass).
     * MuscleDistributionF is a slight bonus for arm-biased characters (and penalty for
     * leg-biased ones) — see {@link #getMuscleDistributionDeviation()}.
     */
    public double getStrength() {
        double leverageF = 1 + balance.getC() * (genetics.getLimbRatio() - 1);
        double muscleDistributionF = 1 + balance.getKMuscleDistributionStrength() * getMuscleDistributionDeviation();
        return balance.getK1() * Math.pow(bodyComposition.getMuscleMass(), 2.0 / 3.0)
                * (1 + 0.3 * bodyComposition.getDominantFiberType())
                * bodyComposition.getNeuromuscularEfficiency()
                * leverageF
                * muscleDistributionF;
    }

    /**
     * Speed = k2 x [Strength x (1 + 0.4 x FiberType) / TotalMass] x StrideF, StrideF = Height x LimbRatio.
     * Generic movement-capable speed, used by any movement-involving action (including attacks) —
     * unaffected by {@code muscleDistribution}; see {@link #getMaxMovementSpeed()} for that.
     */
    public double getSpeed() {
        double strideF = genetics.getHeight() * genetics.getLimbRatio();
        return balance.getK2() * (getStrength() * (1 + 0.4 * bodyComposition.getDominantFiberType())
                / getTotalMass()) * strideF;
    }

    /**
     * MaxMovementSpeed = Speed x (1 - kMuscleDistributionSpeed x MuscleDistributionDeviation): the
     * character's max displacement/travel speed, derived from the generic {@link #getSpeed()} plus
     * a leg-bias bonus (arm-bias penalty) — the opposite direction of {@link #getStrength()}'s
     * muscle-distribution modifier, and a larger magnitude per the design's instruction.
     */
    public double getMaxMovementSpeed() {
        return getSpeed() * (1 - balance.getKMuscleDistributionSpeed() * getMuscleDistributionDeviation());
    }

    /**
     * Deviation from the balanced midpoint (5) of {@code muscleDistribution}, in -5..+5. Positive
     * means arm-biased, negative means leg-biased — the single source both muscle-distribution
     * modifiers above scale from, each applying it with the sign matching its own direction.
     */
    private double getMuscleDistributionDeviation() {
        return genetics.getMuscleDistribution() - 5;
    }

    /**
     * StaminaPool = k3 x CardiovascularCapacity x (1 - 0.3 x FiberType): slow-twitch fiber bias
     * raises the pool, fast-twitch bias lowers it.
     */
    public double getStaminaPool() {
        return balance.getK3() * getCardiovascularCapacity() * (1 - 0.3 * bodyComposition.getDominantFiberType());
    }

    /**
     * FatigueRate = k4 x TotalMass^0.75 + k5 x MuscleMass x intensity - k6 x CardiovascularCapacity.
     * TotalMass^0.75 is Kleiber's law, used here as a game-design heuristic rather than a
     * validated intra-species metabolic model.
     */
    public double getFatigueRate(double intensity) {
        return balance.getK4() * Math.pow(getTotalMass(), 0.75)
                + balance.getK5() * bodyComposition.getMuscleMass() * intensity
                - balance.getK6() * getCardiovascularCapacity();
    }

    /**
     * EnergyCost(intensity) = BMR_base + ActivityCost - Efficiency. The design doc names these
     * three terms without giving concrete formulas beyond "BMR_base proportional to TotalMass^0.75" —
     * ActivityCost (linear in mass and intensity) and Efficiency (cardiovascular capacity discounted
     * by fast-twitch fiber bias, mirroring {@link #getStaminaPool()}'s shape) are this implementation's
     * own concretization, each behind its own free coefficient.
     */
    public double getEnergyCost(double intensity) {
        double bmrBase = balance.getKBmr() * Math.pow(getTotalMass(), 0.75);
        double activityCost = balance.getKActivityCost() * getTotalMass() * intensity;
        double efficiency = balance.getKEfficiency() * getCardiovascularCapacity()
                * (1 - 0.3 * bodyComposition.getDominantFiberType());
        return bmrBase + activityCost - efficiency;
    }

    /**
     * Durability = k7 x (BoneDensity + 0.5 x Mesomorphy) + k8 x ln(TotalMass) + k9 x sqrt(FatMass).
     */
    public double getDurability() {
        return balance.getK7() * (genetics.getBoneDensity() + 0.5 * genetics.getMesomorphy())
                + balance.getK8() * Math.log(getTotalMass())
                + balance.getK9() * Math.sqrt(bodyComposition.getBodyFat());
    }

    public Genetics getGenetics() {
        return genetics;
    }

    public BloodSystem getBloodSystem() {
        return bloodSystem;
    }

    public BodyComposition getBodyComposition() {
        return bodyComposition;
    }

    public NervousSystem getNervousSystem() {
        return nervousSystem;
    }

    public CardiacSystem getCardiacSystem() {
        return cardiacSystem;
    }

    public PulmonarySystem getPulmonarySystem() {
        return pulmonarySystem;
    }

    public AttributePointBudget getGeneticPoints() {
        return geneticPoints;
    }

    public AttributePointBudget getTrainingPoints() {
        return trainingPoints;
    }

    public BiomechanicsBalance getBalance() {
        return balance;
    }
}
