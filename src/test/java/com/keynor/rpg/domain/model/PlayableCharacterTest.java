package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Covers the additive-standard formulas introduced in rpg-11 (baseline 60 + weighted
 * deviations from each input's neutral point). Human defaults land every deviation at
 * zero, so every baseline-anchored attribute equals exactly 60 on {@link Body#humanTemplate()}.
 */
class PlayableCharacterTest {

    private static final double TOLERANCE = 1e-9;

    // -------------------------------------------------------------------------
    // Identity and lore reference
    // -------------------------------------------------------------------------

    @Test
    void newCharacter_hasNoLoreReferenceUntilLinked() {
        PlayableCharacter character = new PlayableCharacter("Keynor", Body.humanTemplate());

        assertThat(character.getName()).isEqualTo("Keynor");
        assertThat(character.getBody()).isNotNull();
        assertThat(character.getBody().getBiomechanics()).isNotNull();
        assertThat(character.getLoreReference()).isNull();
    }

    @Test
    void linkToLore_setsLoreReference() {
        PlayableCharacter character = new PlayableCharacter("Keynor", Body.humanTemplate());

        character.linkToLore("character-keynor");

        assertThat(character.getLoreReference()).isEqualTo("character-keynor");
    }

    // -------------------------------------------------------------------------
    // Derived mass
    // -------------------------------------------------------------------------

    @Test
    void getSymbolicTotalMass_onHumanDefaults_equalsTwentyFive() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSymbolicTotalMass()).isEqualTo(25);
    }

    @Test
    void getSymbolicTotalMass_increasesWithHeightMuscleMassBodyFatAndBoneDensity() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getGenetics(); // sanity: genetics is immutable, composition/bone density mutate below
        body.getBiomechanics().getBodyComposition().setMuscleMass(10);
        body.getBiomechanics().getBodyComposition().setBodyFat(6);
        PlayableCharacter character = new PlayableCharacter("test", body);

        // 10 (base) + 7 (height) + 10 (muscleMass) + 6 (bodyFat) + (5 - 5) (boneDensity deviation)
        assertThat(character.getSymbolicTotalMass()).isEqualTo(33);
    }

    @Test
    void getDisplayMassKg_onHumanDefaults_equalsSeventyOne() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getDisplayMassKg()).isCloseTo(71.0, within(TOLERANCE));
    }

    // -------------------------------------------------------------------------
    // Strength
    // -------------------------------------------------------------------------

    @Test
    void getStrength_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getStrength()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getStrength_higherMuscleMassNeuromuscularFiberTypeLimbRatioAndMuscleDistribution_allIncreaseStrength() {
        Genetics genetics = new Genetics(5, 5, 5, 7, 5, 5);
        BodyComposition composition = new BodyComposition(3, 9, 9, 9, 9);
        NervousSystem nervousSystem = new NervousSystem(5, 9);
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), nervousSystem);
        Body body = Body.previewTemplate(new Biomechanics(genetics, composition), bodySystems,
                SpatialIntelligence.defaults());
        PlayableCharacter character = new PlayableCharacter("test", body);

        double expected = 60
                + 4 * (9 - 5)
                + 2 * (9 - 5)
                + 1 * (9 - 5)
                + 2 * (5 - 3)
                + 1 * (9 - 5);

        assertThat(character.getStrength()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getStrength_isFlooredWhenAnExtremeCoefficientDrivesItBelowTheFloor() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleMass(1);
        body.getCoefficients().setKStrengthMuscleMass(1000);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getStrength()).isEqualTo(body.getCoefficients().getAttributeFloor());
    }

    // -------------------------------------------------------------------------
    // Speed and MaxMovementSpeed
    // -------------------------------------------------------------------------

    @Test
    void getSpeed_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSpeed()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getSpeed_isNotAffectedByLimbRatio() {
        Genetics longLimbs = new Genetics(5, 5, 5, 7, 5, 5);
        PlayableCharacter longLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(longLimbs, BodyComposition.defaults()),
                        BodySystems.defaults(), SpatialIntelligence.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(longLimbed.getSpeed()).isCloseTo(defaults.getSpeed(), within(TOLERANCE));
    }

    @Test
    void getSpeed_worstCaseSliderCombination_staysPositiveWithoutAFloor() {
        Genetics worstCase = new Genetics(5, 5, 5, 15, 3, 9);
        BodyComposition composition = new BodyComposition(10, 1, 1, 5, 5);
        NervousSystem nervousSystem = new NervousSystem(5, 1);
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), nervousSystem);
        Body body = Body.previewTemplate(new Biomechanics(worstCase, composition), bodySystems,
                SpatialIntelligence.defaults());
        PlayableCharacter character = new PlayableCharacter("test", body);

        // SymbolicTotalMass = 10+15+1+10+(9-5) = 40; massPenalty = floor((40-25)/3) = 5
        // Speed = 60 + 4*(1-5) + 1*(1-5) + 2*(1-5) - 5 = 27
        assertThat(character.getSpeed()).isCloseTo(27.0, within(TOLERANCE));
        assertThat(character.getSpeed()).isGreaterThan(0);
    }

    @Test
    void getMaxMovementSpeed_onBalancedMuscleDistributionAndNeutralLimbRatio_equalsSpeed() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMaxMovementSpeed()).isCloseTo(character.getSpeed(), within(TOLERANCE));
    }

    @Test
    void getMaxMovementSpeed_legBiasedMuscleDistribution_isHigherThanSpeed() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(1);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getMaxMovementSpeed()).isGreaterThan(character.getSpeed());
    }

    @Test
    void getMaxMovementSpeed_armBiasedMuscleDistribution_isLowerThanSpeed() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(9);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getMaxMovementSpeed()).isLessThan(character.getSpeed());
    }

    @Test
    void getMaxMovementSpeed_longerLimbRatio_increasesMaxMovementSpeed() {
        Genetics longLimbs = new Genetics(5, 5, 5, 7, 5, 5);
        PlayableCharacter longLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(longLimbs, BodyComposition.defaults()),
                        BodySystems.defaults(), SpatialIntelligence.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(longLimbed.getMaxMovementSpeed()).isGreaterThan(defaults.getMaxMovementSpeed());
    }

    @Test
    void getMaxMovementSpeed_shorterLimbRatio_reducesMaxMovementSpeed() {
        Genetics shortLimbs = new Genetics(5, 5, 5, 7, 1, 5);
        PlayableCharacter shortLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(shortLimbs, BodyComposition.defaults()),
                        BodySystems.defaults(), SpatialIntelligence.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(shortLimbed.getMaxMovementSpeed()).isLessThan(defaults.getMaxMovementSpeed());
    }

    @Test
    void getMaxMovementSpeed_isFlooredWhenAnExtremeCoefficientDrivesItBelowTheFloor() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(9);
        body.getCoefficients().setKMaxMovementSpeedMuscleDistribution(1000);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getMaxMovementSpeed()).isEqualTo(body.getCoefficients().getAttributeFloor());
    }

    // -------------------------------------------------------------------------
    // StaminaPool, FatigueResistance, StaminaRecovery
    // -------------------------------------------------------------------------

    @Test
    void getStaminaPool_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getStaminaPool()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getStaminaPool_isReducedByFastTwitchFiberTypeBias() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setDominantFiberType(9);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double expected = 60 - 2 * (9 - 5);
        assertThat(character.getStaminaPool()).isCloseTo(expected, within(TOLERANCE));
        assertThat(character.getStaminaPool()).isLessThan(60.0);
    }

    @Test
    void getFatigueResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getFatigueResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getFatigueResistance_worstCaseSliderCombination_isStillPositiveButFlooredIfPushedFurther() {
        Genetics worstCase = new Genetics(5, 5, 5, 15, 3, 9);
        BodyComposition composition = new BodyComposition(10, 15, 5, 5, 5);
        NervousSystem nervousSystem = new NervousSystem(5, 9);
        BodySystems bodySystems = new BodySystems(new BloodSystem(1), new CardiacSystem(1),
                new PulmonarySystem(1), nervousSystem);
        Body body = Body.previewTemplate(new Biomechanics(worstCase, composition), bodySystems,
                SpatialIntelligence.defaults());
        PlayableCharacter character = new PlayableCharacter("test", body);

        // SymbolicTotalMass = 10+15+15+10+(9-5) = 54; massPenalty = floor((54-25)/2) = 14
        // raw = 60 + 3*(1-5) + 1*(1-5) + 1*(1-5) - 2*(9-5) - 14 - 1*(15-5) = 60-12-4-4-8-14-10 = 8
        assertThat(character.getFatigueResistance()).isCloseTo(8.0, within(TOLERANCE));
    }

    @Test
    void getFatigueResistance_isFlooredWhenAnExtremeCoefficientDrivesItBelowTheFloor() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleMass(15);
        body.getCoefficients().setKFatigueResistanceMuscleMass(1000);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getFatigueResistance()).isEqualTo(body.getCoefficients().getAttributeFloor());
    }

    @Test
    void getStaminaRecovery_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getStaminaRecovery()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getStaminaRecovery_isReducedByFastTwitchFiberTypeBias() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setDominantFiberType(9);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double expected = 60 - 1 * (9 - 5);
        assertThat(character.getStaminaRecovery()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getStaminaRecovery_higherOxygenCarryingCapacity_increasesRecovery() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getBloodSystem(); // BloodSystem is immutable — build a new one instead
        BodySystems bodySystems = new BodySystems(new BloodSystem(9), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), NervousSystem.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), bodySystems, SpatialIntelligence.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getStaminaRecovery()).isGreaterThan(defaults.getStaminaRecovery());
    }

    // -------------------------------------------------------------------------
    // Durability
    // -------------------------------------------------------------------------

    @Test
    void getDurability_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getDurability()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getDurability_usesBodyFatsOwnNeutralOfThreeNotFive() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setBodyFat(6);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double expected = 60 + 1 * (6 - 3);
        assertThat(character.getDurability()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getDurability_higherFlexibilityReducesDurability() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setFlexibility(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getDurability()).isLessThan(defaults.getDurability());
    }

    // -------------------------------------------------------------------------
    // Sight / Hearing / Smell
    // -------------------------------------------------------------------------

    @Test
    void getSight_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSight()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getHearing_returnsTheSameValueAsSight() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getHearing()).isCloseTo(character.getSight(), within(TOLERANCE));
    }

    @Test
    void getSmell_returnsTheSameValueAsSight() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSmell()).isCloseTo(character.getSight(), within(TOLERANCE));
    }

    @Test
    void getSight_higherPerception_increasesAllSenses() {
        Body body = Body.humanTemplate();
        body.getSpatialIntelligence().setPerception(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSight()).isGreaterThan(defaults.getSight());
    }

    @Test
    void getSight_higherNeuralDrive_increasesAllSenses() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNervousSystem().setNeuralDrive(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSight()).isGreaterThan(defaults.getSight());
    }

    // -------------------------------------------------------------------------
    // Evasion
    // -------------------------------------------------------------------------

    @Test
    void getEvasion_onHumanDefaults_equalsSpeed() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getEvasion()).isCloseTo(character.getSpeed(), within(TOLERANCE));
    }

    @Test
    void getEvasion_higherAgilityNeuralDriveOrFlexibility_increasesEvasion() {
        Body body = Body.humanTemplate();
        body.getSpatialIntelligence().setAgility(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getEvasion()).isGreaterThan(defaults.getEvasion());
    }

    @Test
    void getEvasion_isFlooredWhenAnExtremeCoefficientDrivesItBelowTheFloor() {
        Body body = Body.humanTemplate();
        body.getSpatialIntelligence().setAgility(1);
        body.getCoefficients().setKEvasionAgility(1000);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getEvasion()).isEqualTo(body.getCoefficients().getAttributeFloor());
    }

    // -------------------------------------------------------------------------
    // Acrobatics / MeleeAccuracy / Aim
    // -------------------------------------------------------------------------

    @Test
    void getAcrobatics_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getAcrobatics()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getAcrobatics_higherAgilityOrFlexibility_increasesAcrobatics() {
        Body body = Body.humanTemplate();
        body.getSpatialIntelligence().setAgility(9);
        body.getBiomechanics().getBodyComposition().setFlexibility(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getAcrobatics()).isGreaterThan(defaults.getAcrobatics());
    }

    @Test
    void getMeleeAccuracy_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMeleeAccuracy()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getMeleeAccuracy_higherPrecision_increasesMeleeAccuracy() {
        Body body = Body.humanTemplate();
        body.getSpatialIntelligence().setPrecision(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMeleeAccuracy()).isGreaterThan(defaults.getMeleeAccuracy());
    }

    @Test
    void getAim_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getAim()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getAim_higherPerceptionOrPrecision_increasesAim() {
        Body body = Body.humanTemplate();
        body.getSpatialIntelligence().setPerception(9);
        body.getSpatialIntelligence().setPrecision(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getAim()).isGreaterThan(defaults.getAim());
    }

    // -------------------------------------------------------------------------
    // Load capacity
    // -------------------------------------------------------------------------

    @Test
    void getMaxCapacityKg_onHumanDefaults_derivesFromStrengthSquaredPlusStrength() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = Math.floor(Math.pow(60, 2) / 25.0) + 60;
        assertThat(character.getMaxCapacityKg()).isCloseTo(expected, within(TOLERANCE));
        assertThat(character.getMaxCapacityKg()).isCloseTo(204.0, within(TOLERANCE));
    }

    @Test
    void getLightAndHeavyLoadKg_areFractionsOfMaxCapacityKg() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getLightLoadKg()).isCloseTo(character.getMaxCapacityKg() * 0.3, within(TOLERANCE));
        assertThat(character.getHeavyLoadKg()).isCloseTo(character.getMaxCapacityKg() * 0.7, within(TOLERANCE));
    }

    @Test
    void getDragCapacityKg_combinesMaxCapacityKgAndDisplayMassKg() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = 2 * character.getMaxCapacityKg() + Math.floor(character.getDisplayMassKg() * 0.5);
        assertThat(character.getDragCapacityKg()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getMaxCapacityKg_higherStrength_increasesEveryLoadFigure() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleMass(15);
        PlayableCharacter stronger = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(stronger.getMaxCapacityKg()).isGreaterThan(defaults.getMaxCapacityKg());
        assertThat(stronger.getLightLoadKg()).isGreaterThan(defaults.getLightLoadKg());
        assertThat(stronger.getHeavyLoadKg()).isGreaterThan(defaults.getHeavyLoadKg());
        assertThat(stronger.getDragCapacityKg()).isGreaterThan(defaults.getDragCapacityKg());
    }
}
