package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

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
    void getBoneMass_onHumanDefaults_appliesHeightSquaredWithNeutralDensityDeviation() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = 2.7 * Math.pow(1.70, 2) * (1 + 0.06 * (5 - 5));

        assertThat(character.getBoneMass()).isCloseTo(expected, within(TOLERANCE));
        assertThat(character.getBoneMass()).isCloseTo(7.80, within(0.01));
    }

    @Test
    void getBoneMass_doesNotCollapseToZeroAtMinimumBoneDensity() {
        Genetics brittleBones = new Genetics(5, 5, 5, 170, 1.0, 0);
        Biomechanics biomechanics = new Biomechanics(brittleBones, BodyComposition.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(biomechanics, BodySystems.defaults(), SpatialIntelligence.defaults()));

        assertThat(character.getBoneMass()).isGreaterThan(0);
    }

    @Test
    void getBoneMass_increasesWithBoneDensityAboveTheMidRangeDefault() {
        Genetics denseBones = new Genetics(5, 5, 5, 170, 1.0, 10);
        Biomechanics biomechanics = new Biomechanics(denseBones, BodyComposition.defaults());
        PlayableCharacter denser = new PlayableCharacter("test",
                Body.previewTemplate(biomechanics, BodySystems.defaults(), SpatialIntelligence.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(denser.getBoneMass()).isGreaterThan(defaults.getBoneMass());
    }

    @Test
    void getOrganWaterMass_onHumanDefaults_scalesOnlyWithHeightSquared() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = 6.3 * Math.pow(1.70, 2);

        assertThat(character.getOrganWaterMass()).isCloseTo(expected, within(TOLERANCE));
        assertThat(character.getOrganWaterMass()).isCloseTo(18.21, within(0.01));
    }

    @Test
    void getOrganWaterMass_isUnaffectedByBoneDensity() {
        Genetics denseBones = new Genetics(5, 5, 5, 170, 1.0, 10);
        Biomechanics biomechanics = new Biomechanics(denseBones, BodyComposition.defaults());
        PlayableCharacter denser = new PlayableCharacter("test",
                Body.previewTemplate(biomechanics, BodySystems.defaults(), SpatialIntelligence.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(denser.getOrganWaterMass()).isCloseTo(defaults.getOrganWaterMass(), within(TOLERANCE));
    }

    @Test
    void getTotalMass_onHumanDefaults_matchesThePreviousHardcoded70kgDefaultAlmostExactly() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = 14 + 30 + character.getBoneMass() + character.getOrganWaterMass();

        assertThat(character.getTotalMass()).isCloseTo(expected, within(TOLERANCE));
        assertThat(character.getTotalMass()).isCloseTo(70.01, within(0.01));
    }

    @Test
    void getTotalMass_sumsBodyFatMuscleMassBoneMassAndOrganWaterMass() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setBodyFat(10);
        body.getBiomechanics().getBodyComposition().setMuscleMass(40);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double expected = 10 + 40 + character.getBoneMass() + character.getOrganWaterMass();

        assertThat(character.getTotalMass()).isCloseTo(expected, within(TOLERANCE));
    }

    // -------------------------------------------------------------------------
    // Cardiovascular capacity
    // -------------------------------------------------------------------------

    @Test
    void getCardiovascularCapacity_isAverageOfBloodCardiacAndPulmonaryQuality() {
        BodySystems bodySystems = new BodySystems(new BloodSystem(6), new CardiacSystem(9),
                new PulmonarySystem(3), NervousSystem.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), bodySystems, SpatialIntelligence.defaults()));

        assertThat(character.getCardiovascularCapacity()).isCloseTo(6.0, within(TOLERANCE));
    }

    // -------------------------------------------------------------------------
    // Strength
    // -------------------------------------------------------------------------

    @Test
    void getStrength_onHumanDefaults_appliesMuscleMassPowerLawAndNeuromuscularEfficiency() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = Math.pow(30, 2.0 / 3.0) * (1 + 0.3 * 0.0) * 0.5 * 1.0;

        assertThat(character.getStrength()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getStrength_appliesLeverageCoefficientCToLimbRatioAndScalesWithK1() {
        Genetics genetics = new Genetics(5, 5, 5, 170, 1.2, 5);
        BodyComposition composition = new BodyComposition(14, 40, 0.5, 5.0, 5.0);
        NervousSystem nervousSystem = new NervousSystem(5, 0.8);
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), nervousSystem);
        Body body = Body.previewTemplate(new Biomechanics(genetics, composition), bodySystems,
                SpatialIntelligence.defaults());
        body.getCoefficients().setK1(1.5);
        body.getCoefficients().setC(2);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double leverageF = 1 + 2 * (1.2 - 1);
        double muscleDistF = 1 + 0.02 * (5 - 5);
        double expected = 1.5 * Math.pow(40, 2.0 / 3.0) * (1 + 0.3 * 0.5) * 0.8 * leverageF * muscleDistF;

        assertThat(character.getStrength()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getStrength_appliesMuscleDistributionDeviationAsArmBiasBonus() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(8);
        body.getCoefficients().setKMuscleDistributionStrength(0.05);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double muscleDistributionF = 1 + 0.05 * (8 - 5);
        double expected = Math.pow(30, 2.0 / 3.0) * (1 + 0.3 * 0.0) * 0.5 * muscleDistributionF;

        assertThat(character.getStrength()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getStrength_legBiasedMuscleDistribution_isLowerThanBalanced() {
        Body legBiasedBody = Body.humanTemplate();
        legBiasedBody.getBiomechanics().getBodyComposition().setMuscleDistribution(2);
        PlayableCharacter legBiased = new PlayableCharacter("test", legBiasedBody);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(legBiased.getStrength()).isLessThan(defaults.getStrength());
    }

    // -------------------------------------------------------------------------
    // Speed and MaxMovementSpeed
    // -------------------------------------------------------------------------

    @Test
    void getSpeed_isIndependentOfStrengthAndUsesMuscleMassFiberTypeAndNeuromuscularEfficiency() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        // Human defaults: muscleMass=30, fiberType=0, neuromuscularEfficiency=0.5
        double expected = Math.pow(30, 2.0 / 3.0) * (1 + 0.4 * 0.0) * 0.5 / character.getTotalMass();

        assertThat(character.getSpeed()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getSpeed_isNotAffectedByLimbRatio() {
        Genetics longLimbs = new Genetics(5, 5, 5, 170, 1.5, 5);
        PlayableCharacter longLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(longLimbs, BodyComposition.defaults()),
                        BodySystems.defaults(), SpatialIntelligence.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(longLimbed.getSpeed()).isCloseTo(defaults.getSpeed(), within(TOLERANCE));
    }

    @Test
    void getMaxMovementSpeed_onBalancedMuscleDistribution_equalsSpeed() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMaxMovementSpeed()).isCloseTo(character.getSpeed(), within(TOLERANCE));
    }

    @Test
    void getMaxMovementSpeed_legBiasedMuscleDistribution_isHigherThanSpeed() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(0);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getMaxMovementSpeed()).isGreaterThan(character.getSpeed());
    }

    @Test
    void getMaxMovementSpeed_armBiasedMuscleDistribution_isLowerThanSpeed() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(10);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getMaxMovementSpeed()).isLessThan(character.getSpeed());
    }

    @Test
    void getMaxMovementSpeed_appliesExplicitCoefficientToDeviation() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(1);
        body.getCoefficients().setKMuscleDistributionSpeed(0.1);
        PlayableCharacter character = new PlayableCharacter("test", body);

        // LimbRatio at default 1.0 — limbF is neutral (1.0)
        double deviation = 1 - 5;
        double expected = character.getSpeed() * (1 - 0.1 * deviation);

        assertThat(character.getMaxMovementSpeed()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getMaxMovementSpeed_longerLimbRatio_increasesMaxMovementSpeed() {
        Genetics longLimbs = new Genetics(5, 5, 5, 170, 1.3, 5);
        PlayableCharacter longLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(longLimbs, BodyComposition.defaults()),
                        BodySystems.defaults(), SpatialIntelligence.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(longLimbed.getMaxMovementSpeed()).isGreaterThan(defaults.getMaxMovementSpeed());
    }

    @Test
    void getMaxMovementSpeed_shorterLimbRatio_reducesMaxMovementSpeed() {
        Genetics shortLimbs = new Genetics(5, 5, 5, 170, 0.8, 5);
        PlayableCharacter shortLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(shortLimbs, BodyComposition.defaults()),
                        BodySystems.defaults(), SpatialIntelligence.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(shortLimbed.getMaxMovementSpeed()).isLessThan(defaults.getMaxMovementSpeed());
    }

    @Test
    void getMaxMovementSpeed_appliesKLimbRatioSpeedCoefficientExplicitly() {
        Genetics genetics = new Genetics(5, 5, 5, 170, 1.2, 5);
        Body body = Body.previewTemplate(new Biomechanics(genetics, BodyComposition.defaults()),
                BodySystems.defaults(), SpatialIntelligence.defaults());
        body.getCoefficients().setKLimbRatioSpeed(0.5);
        PlayableCharacter character = new PlayableCharacter("test", body);

        // MuscleDistribution is at default 5 — muscleDistF is neutral (1.0)
        double limbF = 1 + 0.5 * (1.2 - 1.0);
        double expected = character.getSpeed() * limbF;

        assertThat(character.getMaxMovementSpeed()).isCloseTo(expected, within(TOLERANCE));
    }

    // -------------------------------------------------------------------------
    // Stamina, fatigue and energy cost
    // -------------------------------------------------------------------------

    @Test
    void getStaminaPool_isReducedByFastTwitchFiberTypeBias() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setDominantFiberType(1.0);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double expected = character.getCardiovascularCapacity() * (1 - 0.3 * 1.0);

        assertThat(character.getStaminaPool()).isCloseTo(expected, within(TOLERANCE));
        assertThat(character.getStaminaPool()).isLessThan(character.getCardiovascularCapacity());
    }

    @Test
    void getFatigueRate_combinesKleiberMassBaseMuscleMassIntensityAndCardioRecovery() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = Math.pow(character.getTotalMass(), 0.75) + 30 * 2.0
                - character.getCardiovascularCapacity();

        assertThat(character.getFatigueRate(2.0)).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getEnergyCost_combinesBmrBaseAndActivityCostMinusEfficiency() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double bmrBase = Math.pow(character.getTotalMass(), 0.75);
        double activityCost = character.getTotalMass() * 1.5;
        double efficiency = character.getCardiovascularCapacity() * (1 - 0.3 * 0.0);
        double expected = bmrBase + activityCost - efficiency;

        assertThat(character.getEnergyCost(1.5)).isCloseTo(expected, within(TOLERANCE));
    }

    // -------------------------------------------------------------------------
    // Durability
    // -------------------------------------------------------------------------

    @Test
    void getDurability_combinesBoneDensityMesomorphyMassInertiaAndFatCushion() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = (5 + 0.5 * 5) + Math.log(character.getTotalMass()) + Math.sqrt(14);

        assertThat(character.getDurability()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getDurability_higherFlexibilityReducesDurabilityByFlexibilityDeviationTerm() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setFlexibility(7.0);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        double flexDeviation = (7.0 - 5) * 1.0; // kFlexibilityDurability = 1.0
        assertThat(character.getDurability()).isCloseTo(defaults.getDurability() - flexDeviation, within(TOLERANCE));
        assertThat(character.getDurability()).isLessThan(defaults.getDurability());
    }

    @Test
    void getDurability_flexibilityAtDefault5_doesNotAffectDurability() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getDurability()).isCloseTo(defaults.getDurability(), within(TOLERANCE));
    }

    // -------------------------------------------------------------------------
    // Sight / Hearing / Smell
    // -------------------------------------------------------------------------

    @Test
    void getSight_onHumanDefaults_combinesPerceptionAndNeuralDriveAsAverage() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = (5 + 5) / 2.0; // kSense=1, perception=5, neuralDrive=5

        assertThat(character.getSight()).isCloseTo(expected, within(TOLERANCE));
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
    void getSight_scalesWithKSenseCoefficient() {
        Body body = Body.humanTemplate();
        body.getCoefficients().setKSense(2.0);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSight()).isCloseTo(2.0 * defaults.getSight(), within(TOLERANCE));
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
    void getEvasion_onHumanDefaults_includesAgilitySpeedNeuralAndFlexModifiers() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = 1.0 * 5 * character.getSpeed()
                * (1 + 0.1 * 5)
                * (1 + 0.1 * 5);

        assertThat(character.getEvasion()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getEvasion_higherAgilityIncreasesEvasion() {
        Body body = Body.humanTemplate();
        body.getSpatialIntelligence().setAgility(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getEvasion()).isGreaterThan(defaults.getEvasion());
    }

    @Test
    void getEvasion_higherFlexibility_increasesEvasionViakEvasionFlexModifier() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setFlexibility(9.0);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getEvasion()).isGreaterThan(defaults.getEvasion());
    }

    // -------------------------------------------------------------------------
    // Acrobatics
    // -------------------------------------------------------------------------

    @Test
    void getAcrobatics_onHumanDefaults_isAverageOfAgilityAndFlexibility() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = (5 + 5.0) / 2.0; // kAcrobatics=1, agility=5, flexibility=5

        assertThat(character.getAcrobatics()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getAcrobatics_higherAgilityOrFlexibility_increasesAcrobatics() {
        Body body = Body.humanTemplate();
        body.getSpatialIntelligence().setAgility(9);
        body.getBiomechanics().getBodyComposition().setFlexibility(9.0);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getAcrobatics()).isGreaterThan(defaults.getAcrobatics());
    }

    // -------------------------------------------------------------------------
    // Melee Accuracy
    // -------------------------------------------------------------------------

    @Test
    void getMeleeAccuracy_onHumanDefaults_isAverageOfPrecisionAndAgility() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = (5 + 5) / 2.0; // kMelee=1, precision=5, agility=5

        assertThat(character.getMeleeAccuracy()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getMeleeAccuracy_higherPrecision_increasesMeleeAccuracy() {
        Body body = Body.humanTemplate();
        body.getSpatialIntelligence().setPrecision(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMeleeAccuracy()).isGreaterThan(defaults.getMeleeAccuracy());
    }

    // -------------------------------------------------------------------------
    // Aim
    // -------------------------------------------------------------------------

    @Test
    void getAim_onHumanDefaults_isAverageOfPrecisionAndPerception() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        double expected = (5 + 5) / 2.0; // kAim=1, precision=5, perception=5

        assertThat(character.getAim()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getAim_higherPerception_increasesAim() {
        Body body = Body.humanTemplate();
        body.getSpatialIntelligence().setPerception(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getAim()).isGreaterThan(defaults.getAim());
    }

    @Test
    void getAim_higherPrecision_increasesAim() {
        Body body = Body.humanTemplate();
        body.getSpatialIntelligence().setPrecision(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getAim()).isGreaterThan(defaults.getAim());
    }

}
