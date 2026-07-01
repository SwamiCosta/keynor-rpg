package com.keynor.rpg.domain.model;

/**
 * Aggregate root for a playable character. Holds the {@link Body} pillar (wound tree +
 * data groups) and exposes all derived physical attribute formulas. Formulas combine
 * inputs from {@link Biomechanics} (genetics + body composition), {@link BodySystems}
 * (cardiovascular and neural systems), and {@link SpatialIntelligence} (spatial awareness
 * group) — none of these groups owns the formulas themselves.
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
    // Derived mass — building blocks for most formulas
    // -------------------------------------------------------------------------

    /**
     * BoneMass = kBoneMass x (Height/100)^2 x (1 + kBoneDensity x (BoneDensity - 5)).
     * The density term is a deviation from the mid-range default (5), not an absolute
     * multiplier, so {@code boneDensity = 0} does not collapse bone mass to zero.
     */
    public double getBoneMass() {
        double heightMeters = genetics().getHeight() / 100.0;
        return coeff().getKBoneMass() * Math.pow(heightMeters, 2)
                * (1 + coeff().getKBoneDensity() * (genetics().getBoneDensity() - 5));
    }

    /**
     * OrganWaterMass = kOrganWaterMass x (Height/100)^2. Covers organs, blood, skin and
     * water — mass that {@code bodyFat + muscleMass + boneMass} structurally excludes.
     */
    public double getOrganWaterMass() {
        double heightMeters = genetics().getHeight() / 100.0;
        return coeff().getKOrganWaterMass() * Math.pow(heightMeters, 2);
    }

    /**
     * TotalMass = BodyFat + MuscleMass + BoneMass + OrganWaterMass — fully derived from
     * the trainable layer's player-set fields plus the two height/density-driven components.
     */
    public double getTotalMass() {
        return composition().getBodyFat() + composition().getMuscleMass()
                + getBoneMass() + getOrganWaterMass();
    }

    // -------------------------------------------------------------------------
    // Biomechanics-derived attributes
    // -------------------------------------------------------------------------

    /**
     * Resultant value per design doc: average quality of blood, cardiac and pulmonary systems.
     */
    public double getCardiovascularCapacity() {
        return (bodySystems().getBloodSystem().getOxygenCarryingCapacity()
                + bodySystems().getCardiacSystem().getCardiacOutput()
                + bodySystems().getPulmonarySystem().getPulmonaryCapacity()) / 3.0;
    }

    /**
     * Strength = k1 x MuscleMass^(2/3) x (1 + 0.3 x FiberType) x NeuromuscularEfficiency
     * x LeverageF x MuscleDistributionF. The 2/3 exponent is the square-cube law.
     * LeverageF = 1 + c x (LimbRatio - 1). MuscleDistributionF applies a slight arm-bias
     * bonus (leg-bias penalty) — see {@link #muscleDistributionDeviation()}.
     */
    public double getStrength() {
        double leverageF = 1 + coeff().getC() * (genetics().getLimbRatio() - 1);
        double muscleDistF = 1 + coeff().getKMuscleDistributionStrength() * muscleDistributionDeviation();
        return coeff().getK1() * Math.pow(composition().getMuscleMass(), 2.0 / 3.0)
                * (1 + 0.3 * composition().getDominantFiberType())
                * nervousSystem().getNeuromuscularEfficiency()
                * leverageF
                * muscleDistF;
    }

    /**
     * Speed = k2 x MuscleMass^(2/3) x (1 + 0.4 x FiberType) x NeuromuscularEfficiency / TotalMass.
     * Pure power-to-weight formula, independent of Strength. Same muscle-quality inputs as
     * {@link #getStrength()} but without the leverage term (LimbRatio) — LimbRatio affects only
     * {@link #getMaxMovementSpeed()} via its stride modifier. Feeds Evasion and MaxMovementSpeed.
     */
    public double getSpeed() {
        return coeff().getK2()
                * Math.pow(composition().getMuscleMass(), 2.0 / 3.0)
                * (1 + 0.4 * composition().getDominantFiberType())
                * nervousSystem().getNeuromuscularEfficiency()
                / getTotalMass();
    }

    /**
     * MaxMovementSpeed = Speed x (1 + kLimbRatioSpeed x (LimbRatio - 1))
     * x (1 - kMuscleDistributionSpeed x MuscleDistributionDeviation).
     * Displacement/travel speed, extended from Speed with a stride-length modifier (longer limbs
     * increase it, shorter limbs reduce it) and the muscle-distribution modifier (leg-bias
     * increases it, arm-bias reduces it). LimbRatio is expressed as a deviation from 1.0 (neutral).
     */
    public double getMaxMovementSpeed() {
        double limbF = 1 + coeff().getKLimbRatioSpeed() * (genetics().getLimbRatio() - 1);
        return getSpeed() * limbF * (1 - coeff().getKMuscleDistributionSpeed() * muscleDistributionDeviation());
    }

    /**
     * StaminaPool = k3 x CardiovascularCapacity x (1 - 0.3 x FiberType): slow-twitch fiber
     * bias raises the pool, fast-twitch bias lowers it.
     */
    public double getStaminaPool() {
        return coeff().getK3() * getCardiovascularCapacity()
                * (1 - 0.3 * composition().getDominantFiberType());
    }

    /**
     * FatigueRate = k4 x TotalMass^0.75 + k5 x MuscleMass x intensity - k6 x CardiovascularCapacity.
     * TotalMass^0.75 is Kleiber's law used as a game-design heuristic.
     */
    public double getFatigueRate(double intensity) {
        return coeff().getK4() * Math.pow(getTotalMass(), 0.75)
                + coeff().getK5() * composition().getMuscleMass() * intensity
                - coeff().getK6() * getCardiovascularCapacity();
    }

    /**
     * EnergyCost(intensity) = BMR_base + ActivityCost - Efficiency. ActivityCost (linear in
     * mass and intensity) and Efficiency (cardiovascular capacity discounted by fast-twitch
     * fiber bias) are this implementation's own concretization of terms left unspecified in
     * the design document.
     */
    public double getEnergyCost(double intensity) {
        double bmrBase = coeff().getKBmr() * Math.pow(getTotalMass(), 0.75);
        double activityCost = coeff().getKActivityCost() * getTotalMass() * intensity;
        double efficiency = coeff().getKEfficiency() * getCardiovascularCapacity()
                * (1 - 0.3 * composition().getDominantFiberType());
        return bmrBase + activityCost - efficiency;
    }

    /**
     * Durability = k7 x (BoneDensity + 0.5 x Mesomorphy) + k8 x ln(TotalMass)
     * + k9 x sqrt(BodyFat) - kFlexibilityDurability x (Flexibility - 5).
     * The flexibility term is a deviation from the balanced midpoint (5): higher flexibility
     * reduces durability, lower flexibility increases it.
     */
    public double getDurability() {
        return coeff().getK7() * (genetics().getBoneDensity() + 0.5 * genetics().getMesomorphy())
                + coeff().getK8() * Math.log(getTotalMass())
                + coeff().getK9() * Math.sqrt(composition().getBodyFat())
                - coeff().getKFlexibilityDurability() * (composition().getFlexibility() - 5);
    }

    // -------------------------------------------------------------------------
    // SpatialIntelligence-derived attributes
    // -------------------------------------------------------------------------

    /**
     * Sight = kSense x (Perception + NeuralDrive) / 2. Shares the same formula as
     * {@link #getHearing()} and {@link #getSmell()} — each can be trained independently
     * in the future, but currently all return the same value.
     */
    public double getSight() {
        return coeff().getKSense() * (spatialIntelligence().getPerception()
                + nervousSystem().getNeuralDrive()) / 2.0;
    }

    /**
     * Hearing — same base formula as {@link #getSight()}.
     */
    public double getHearing() {
        return getSight();
    }

    /**
     * Smell — same base formula as {@link #getSight()}.
     */
    public double getSmell() {
        return getSight();
    }

    /**
     * Evasion = kEvasion x Agility x Speed x (1 + kEvasionNeural x NeuralDrive)
     * x (1 + kEvasionFlex x Flexibility). Ability to dodge attacks, projectiles, and
     * explosions — depends on agility, generic movement speed, neural drive, and flexibility.
     */
    public double getEvasion() {
        return coeff().getKEvasion()
                * spatialIntelligence().getAgility()
                * getSpeed()
                * (1 + coeff().getKEvasionNeural() * nervousSystem().getNeuralDrive())
                * (1 + coeff().getKEvasionFlex() * composition().getFlexibility());
    }

    /**
     * Acrobatics = kAcrobatics x (Agility + Flexibility) / 2. Ability to execute precise
     * movements — feints, leaps, twists — for various effects.
     */
    public double getAcrobatics() {
        return coeff().getKAcrobatics()
                * (spatialIntelligence().getAgility() + composition().getFlexibility()) / 2.0;
    }

    /**
     * MeleeAccuracy = kMelee x (Precision + Agility) / 2. How well the character hits
     * targets with melee strikes.
     */
    public double getMeleeAccuracy() {
        return coeff().getKMelee()
                * (spatialIntelligence().getPrecision() + spatialIntelligence().getAgility()) / 2.0;
    }

    /**
     * Aim = kAim x (Precision + Perception) / 2. How well the character hits targets
     * at a distance.
     */
    public double getAim() {
        return coeff().getKAim()
                * (spatialIntelligence().getPrecision() + spatialIntelligence().getPerception()) / 2.0;
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

    private double muscleDistributionDeviation() {
        return composition().getMuscleDistribution() - 5;
    }
}
