package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class BiomechanicsTest {

    private static final double TOLERANCE = 1e-9;

    @Test
    void humanDefaults_wiresUpGeneticAndTrainableLayersWithFullPointBudgets() {
        Biomechanics biomechanics = Biomechanics.humanDefaults();

        assertThat(biomechanics.getGenetics()).isNotNull();
        assertThat(biomechanics.getBloodSystem()).isNotNull();
        assertThat(biomechanics.getBodyComposition()).isNotNull();
        assertThat(biomechanics.getNervousSystem()).isNotNull();
        assertThat(biomechanics.getCardiacSystem()).isNotNull();
        assertThat(biomechanics.getPulmonarySystem()).isNotNull();
        assertThat(biomechanics.getBalance()).isNotNull();

        assertThat(biomechanics.getGeneticPoints().remainingPoints()).isEqualTo(20);
        assertThat(biomechanics.getTrainingPoints().remainingPoints()).isEqualTo(20);
    }

    @Test
    void getCardiovascularCapacity_isAverageOfBloodCardiacAndPulmonaryQuality() {
        Biomechanics biomechanics = new Biomechanics(Genetics.defaults(), new BloodSystem(6),
                BodyComposition.defaults(), NervousSystem.defaults(), new CardiacSystem(9),
                new PulmonarySystem(3), new AttributePointBudget(20), new AttributePointBudget(20),
                BiomechanicsBalance.defaults());

        assertThat(biomechanics.getCardiovascularCapacity()).isCloseTo(6.0, within(TOLERANCE));
    }

    @Test
    void getStrength_onHumanDefaults_appliesMuscleMassPowerLawAndNeuromuscularEfficiency() {
        Biomechanics biomechanics = Biomechanics.humanDefaults();

        double expected = Math.pow(30, 2.0 / 3.0) * (1 + 0.3 * 0.0) * 0.5 * 1.0;

        assertThat(biomechanics.getStrength()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getStrength_appliesLeverageCoefficientCToLimbRatioAndScalesWithK1() {
        Genetics genetics = new Genetics(5, 5, 5, 170, 1.2, 5);
        BodyComposition composition = new BodyComposition(14, 40, 0.5, 0.8);
        BiomechanicsBalance balance = BiomechanicsBalance.defaults();
        balance.setK1(1.5);
        balance.setC(2);
        Biomechanics biomechanics = new Biomechanics(genetics, BloodSystem.defaults(), composition,
                NervousSystem.defaults(), CardiacSystem.defaults(), PulmonarySystem.defaults(),
                new AttributePointBudget(20), new AttributePointBudget(20), balance);

        double leverageF = 1 + 2 * (1.2 - 1);
        double expected = 1.5 * Math.pow(40, 2.0 / 3.0) * (1 + 0.3 * 0.5) * 0.8 * leverageF;

        assertThat(biomechanics.getStrength()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getSpeed_combinesStrengthFiberTypeBonusOverMassWithStrideF() {
        Biomechanics biomechanics = Biomechanics.humanDefaults();

        double strideF = 170 * 1.0;
        double expected = (biomechanics.getStrength() * (1 + 0.4 * 0.0) / biomechanics.getTotalMass()) * strideF;

        assertThat(biomechanics.getSpeed()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getStaminaPool_isReducedByFastTwitchFiberTypeBias() {
        BodyComposition fastTwitch = new BodyComposition(14, 30, 1.0, 0.5);
        Biomechanics biomechanics = new Biomechanics(Genetics.defaults(), BloodSystem.defaults(), fastTwitch,
                NervousSystem.defaults(), CardiacSystem.defaults(), PulmonarySystem.defaults(),
                new AttributePointBudget(20), new AttributePointBudget(20), BiomechanicsBalance.defaults());

        double expected = biomechanics.getCardiovascularCapacity() * (1 - 0.3 * 1.0);

        assertThat(biomechanics.getStaminaPool()).isCloseTo(expected, within(TOLERANCE));
        assertThat(biomechanics.getStaminaPool()).isLessThan(biomechanics.getCardiovascularCapacity());
    }

    @Test
    void getFatigueRate_combinesKleiberMassBaseMuscleMassIntensityAndCardioRecovery() {
        Biomechanics biomechanics = Biomechanics.humanDefaults();

        double expected = Math.pow(biomechanics.getTotalMass(), 0.75) + 30 * 2.0
                - biomechanics.getCardiovascularCapacity();

        assertThat(biomechanics.getFatigueRate(2.0)).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getEnergyCost_combinesBmrBaseAndActivityCostMinusEfficiency() {
        Biomechanics biomechanics = Biomechanics.humanDefaults();

        double bmrBase = Math.pow(biomechanics.getTotalMass(), 0.75);
        double activityCost = biomechanics.getTotalMass() * 1.5;
        double efficiency = biomechanics.getCardiovascularCapacity() * (1 - 0.3 * 0.0);
        double expected = bmrBase + activityCost - efficiency;

        assertThat(biomechanics.getEnergyCost(1.5)).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getDurability_combinesBoneDensityMesomorphyMassInertiaAndFatCushion() {
        Biomechanics biomechanics = Biomechanics.humanDefaults();

        double expected = (5 + 0.5 * 5) + Math.log(biomechanics.getTotalMass()) + Math.sqrt(14);

        assertThat(biomechanics.getDurability()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getBoneMass_onHumanDefaults_appliesHeightSquaredWithNeutralDensityDeviation() {
        Biomechanics biomechanics = Biomechanics.humanDefaults();

        double expected = 2.7 * Math.pow(1.70, 2) * (1 + 0.06 * (5 - 5));

        assertThat(biomechanics.getBoneMass()).isCloseTo(expected, within(TOLERANCE));
        assertThat(biomechanics.getBoneMass()).isCloseTo(7.80, within(0.01));
    }

    @Test
    void getBoneMass_doesNotCollapseToZeroAtMinimumBoneDensity() {
        Genetics brittleBones = new Genetics(5, 5, 5, 170, 1.0, 0);
        Biomechanics biomechanics = new Biomechanics(brittleBones, BloodSystem.defaults(),
                BodyComposition.defaults(), NervousSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), new AttributePointBudget(20), new AttributePointBudget(20),
                BiomechanicsBalance.defaults());

        assertThat(biomechanics.getBoneMass()).isGreaterThan(0);
    }

    @Test
    void getBoneMass_increasesWithBoneDensityAboveTheMidRangeDefault() {
        Genetics denseBones = new Genetics(5, 5, 5, 170, 1.0, 10);
        Biomechanics biomechanics = new Biomechanics(denseBones, BloodSystem.defaults(),
                BodyComposition.defaults(), NervousSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), new AttributePointBudget(20), new AttributePointBudget(20),
                BiomechanicsBalance.defaults());

        assertThat(biomechanics.getBoneMass()).isGreaterThan(Biomechanics.humanDefaults().getBoneMass());
    }

    @Test
    void getOrganWaterMass_onHumanDefaults_scalesOnlyWithHeightSquared() {
        Biomechanics biomechanics = Biomechanics.humanDefaults();

        double expected = 6.3 * Math.pow(1.70, 2);

        assertThat(biomechanics.getOrganWaterMass()).isCloseTo(expected, within(TOLERANCE));
        assertThat(biomechanics.getOrganWaterMass()).isCloseTo(18.21, within(0.01));
    }

    @Test
    void getOrganWaterMass_isUnaffectedByBoneDensity() {
        Genetics denseBones = new Genetics(5, 5, 5, 170, 1.0, 10);
        Biomechanics biomechanics = new Biomechanics(denseBones, BloodSystem.defaults(),
                BodyComposition.defaults(), NervousSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), new AttributePointBudget(20), new AttributePointBudget(20),
                BiomechanicsBalance.defaults());

        assertThat(biomechanics.getOrganWaterMass())
                .isCloseTo(Biomechanics.humanDefaults().getOrganWaterMass(), within(TOLERANCE));
    }

    @Test
    void getTotalMass_onHumanDefaults_matchesThePreviousHardcoded70kgDefaultAlmostExactly() {
        Biomechanics biomechanics = Biomechanics.humanDefaults();

        double expected = 14 + 30 + biomechanics.getBoneMass() + biomechanics.getOrganWaterMass();

        assertThat(biomechanics.getTotalMass()).isCloseTo(expected, within(TOLERANCE));
        assertThat(biomechanics.getTotalMass()).isCloseTo(70.01, within(0.01));
    }

    @Test
    void getTotalMass_sumsBodyFatMuscleMassBoneMassAndOrganWaterMass() {
        BodyComposition composition = new BodyComposition(10, 40, 0.0, 0.5);
        Biomechanics biomechanics = new Biomechanics(Genetics.defaults(), BloodSystem.defaults(), composition,
                NervousSystem.defaults(), CardiacSystem.defaults(), PulmonarySystem.defaults(),
                new AttributePointBudget(20), new AttributePointBudget(20), BiomechanicsBalance.defaults());

        double expected = 10 + 40 + biomechanics.getBoneMass() + biomechanics.getOrganWaterMass();

        assertThat(biomechanics.getTotalMass()).isCloseTo(expected, within(TOLERANCE));
    }
}
