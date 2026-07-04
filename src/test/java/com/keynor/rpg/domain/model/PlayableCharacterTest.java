package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Covers the additive-standard formulas introduced in rpg-11 (baseline 60 + weighted
 * deviations from each input's neutral point), extended in rpg-13 (Neural/Hormonal/Digestive
 * systems, 15 new attributes), rpg-14 (PhysicalTraits, hormone modifiers, 6 new attributes),
 * and Delta V4 (Hippocampus/Thalamus split, Strength deprecated in favor of 4 specialized
 * strengths, 3 new resistance/threshold attributes, Balance/Aim rebuilt). Human defaults land
 * every deviation at zero, so every baseline-anchored attribute equals exactly 60 on
 * {@link Body#humanTemplate()}.
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
    // Specialized strengths (Delta V4 — replace the old global Strength). All anchored on the
    // hidden meanStrength() engine, which equals baseline (60) at human defaults since
    // MuscleMass/NeuromuscularEfficiency/FiberType are all at their neutral (5).
    // -------------------------------------------------------------------------

    @Test
    void getPushStrength_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getPushStrength()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getLegDrive_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getLegDrive()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getGripStrength_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getGripStrength()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getLiftStrength_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getLiftStrength()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getPushStrength_higherMuscleMassNeuromuscularOrFiberType_increasesEveryStrength() {
        Genetics genetics = new Genetics(5, 5, 5, 7, 3);
        BodyComposition composition = new BodyComposition(3, 9, 9, 9, 5, 5, 5);
        NeuralSystem neuralSystem = new NeuralSystem(5, 9, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0);
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), neuralSystem, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        Body body = Body.previewTemplate(new Biomechanics(genetics, composition), bodySystems, PhysicalTraits.defaults());
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getPushStrength()).isGreaterThan(defaults.getPushStrength());
        assertThat(character.getLegDrive()).isGreaterThan(defaults.getLegDrive());
        assertThat(character.getGripStrength()).isGreaterThan(defaults.getGripStrength());
        assertThat(character.getLiftStrength()).isGreaterThan(defaults.getLiftStrength());
    }

    @Test
    void getPushStrength_higherLimbRatioMuscleDistributionTendonsAndHeight_increasesPushStrength() {
        Genetics genetics = new Genetics(5, 5, 5, 15, 5);
        BodyComposition composition = new BodyComposition(3, 5, 5, 9, 5, 5, 9);
        Body body = Body.previewTemplate(new Biomechanics(genetics, composition), BodySystems.defaults(),
                PhysicalTraits.defaults());
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getPushStrength()).isGreaterThan(defaults.getPushStrength());
    }

    @Test
    void getLegDrive_legBiasedMuscleDistribution_isHigherThanPushStrength() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(1);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getLegDrive()).isGreaterThan(character.getPushStrength());
    }

    @Test
    void getLegDrive_armBiasedMuscleDistribution_isLowerThanPushStrength() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(9);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getLegDrive()).isLessThan(character.getPushStrength());
    }

    @Test
    void getLiftStrength_higherLimbRatio_decreasesLiftStrength() {
        Genetics longLimbs = new Genetics(5, 5, 5, 7, 5);
        PlayableCharacter longLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(longLimbs, BodyComposition.defaults()), BodySystems.defaults(),
                        PhysicalTraits.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(longLimbed.getLiftStrength()).isLessThan(defaults.getLiftStrength());
    }

    @Test
    void getPushStrength_isFlooredWhenAnExtremeCoefficientDrivesItBelowTheFloor() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleMass(1);
        body.getCoefficients().setKMeanStrengthMuscleMass(1000);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getPushStrength()).isEqualTo(body.getCoefficients().getAttributeFloor());
        assertThat(character.getLegDrive()).isEqualTo(body.getCoefficients().getAttributeFloor());
        assertThat(character.getGripStrength()).isEqualTo(body.getCoefficients().getAttributeFloor());
        assertThat(character.getLiftStrength()).isEqualTo(body.getCoefficients().getAttributeFloor());
    }

    @Test
    void getSwingPower_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        // floor((PushStrength + GripStrength) / 2) = floor((60+60)/2) = 60
        assertThat(character.getSwingPower()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getGrapplingSelfLifting_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getGrapplingSelfLifting()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getSwingPower_isTheAverageOfPushAndGripStrength() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setTendonsAndLigaments(9);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double expected = Math.floor((character.getPushStrength() + character.getGripStrength()) / 2);
        assertThat(character.getSwingPower()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getGrapplingSelfLifting_isTheAverageOfGripAndLiftStrength() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setTendonsAndLigaments(9);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double expected = Math.floor((character.getGripStrength() + character.getLiftStrength()) / 2);
        assertThat(character.getGrapplingSelfLifting()).isCloseTo(expected, within(TOLERANCE));
    }

    // -------------------------------------------------------------------------
    // Attribute breakdowns (Delta V4) — term-by-term resolved values backing frontend tooltips
    // -------------------------------------------------------------------------

    @Test
    void getPushStrengthBreakdown_totalsMatchTheGetterValue() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setTendonsAndLigaments(9);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getPushStrengthBreakdown().total())
                .isCloseTo(character.getPushStrength(), within(TOLERANCE));
    }

    @Test
    void getBalanceBreakdown_totalsMatchTheGetterValue() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getBalanceBreakdown().total()).isCloseTo(character.getBalance(), within(TOLERANCE));
    }

    @Test
    void getFatGainRateBreakdown_hasZeroBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getFatGainRateBreakdown().baseline()).isCloseTo(0.0, within(TOLERANCE));
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
        Genetics longLimbs = new Genetics(5, 5, 5, 7, 5);
        PlayableCharacter longLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(longLimbs, BodyComposition.defaults()), BodySystems.defaults(),
                        PhysicalTraits.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(longLimbed.getSpeed()).isCloseTo(defaults.getSpeed(), within(TOLERANCE));
    }

    @Test
    void getSpeed_worstCaseSliderCombination_staysPositiveWithoutAFloor() {
        Genetics worstCase = new Genetics(5, 5, 5, 15, 3);
        BodyComposition composition = new BodyComposition(10, 1, 1, 5, 5, 9, 5);
        NeuralSystem neuralSystem = new NeuralSystem(5, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0);
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), neuralSystem, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        Body body = Body.previewTemplate(new Biomechanics(worstCase, composition), bodySystems, PhysicalTraits.defaults());
        PlayableCharacter character = new PlayableCharacter("test", body);

        // SymbolicTotalMass = 10+15+1+10+(9-5) = 40; massPenalty = floor((40-25)/3) = 5
        // Speed = 60 + 4*(1-5) + 1*(1-5) + 2*(1-5) - 5 = 27
        assertThat(character.getSpeed()).isCloseTo(27.0, within(TOLERANCE));
        assertThat(character.getSpeed()).isGreaterThan(0);
    }

    @Test
    void getMaxMovementSpeed_onBalancedMuscleDistributionAndNeutralLimbRatioAndHeight_equalsSpeed() {
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
        Genetics longLimbs = new Genetics(5, 5, 5, 7, 5);
        PlayableCharacter longLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(longLimbs, BodyComposition.defaults()), BodySystems.defaults(),
                        PhysicalTraits.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(longLimbed.getMaxMovementSpeed()).isGreaterThan(defaults.getMaxMovementSpeed());
    }

    @Test
    void getMaxMovementSpeed_shorterLimbRatio_reducesMaxMovementSpeed() {
        Genetics shortLimbs = new Genetics(5, 5, 5, 7, 1);
        PlayableCharacter shortLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(shortLimbs, BodyComposition.defaults()), BodySystems.defaults(),
                        PhysicalTraits.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(shortLimbed.getMaxMovementSpeed()).isLessThan(defaults.getMaxMovementSpeed());
    }

    @Test
    void getMaxMovementSpeed_tallerHeight_increasesMaxMovementSpeed() {
        Genetics tall = new Genetics(5, 5, 5, 15, 3);
        PlayableCharacter taller = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(tall, BodyComposition.defaults()), BodySystems.defaults(),
                        PhysicalTraits.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(taller.getMaxMovementSpeed()).isGreaterThan(defaults.getMaxMovementSpeed());
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
    void getStaminaPool_higherDigestiveAbsorption_increasesPool() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getDigestiveSystem().setDigestiveAbsorption(9);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double expected = 60 + 2 * (9 - 5);
        assertThat(character.getStaminaPool()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getFatigueResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getFatigueResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getFatigueResistance_worstCaseSliderCombination_isStillPositiveButFlooredIfPushedFurther() {
        Genetics worstCase = new Genetics(5, 5, 5, 15, 3);
        BodyComposition composition = new BodyComposition(10, 15, 5, 5, 5, 9, 5);
        NeuralSystem neuralSystem = new NeuralSystem(5, 9, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0);
        BodySystems bodySystems = new BodySystems(new BloodSystem(1, 3), new CardiacSystem(1, 0),
                new PulmonarySystem(1), neuralSystem, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        Body body = Body.previewTemplate(new Biomechanics(worstCase, composition), bodySystems, PhysicalTraits.defaults());
        PlayableCharacter character = new PlayableCharacter("test", body);

        // SymbolicTotalMass = 10+15+15+10+(9-5) = 54; massPenalty = floor((54-25)/2) = 14
        // raw = 60 + 3*(1-5) + 1*(1-5) + 1*(1-5) - 2*(9-5) - 14 - 1*(15-5) + 1*(5-5) + 2*(5-5)
        //     = 60-12-4-4-8-14-10+0+0 = 8
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
    void getFatigueResistance_higherHypothalamusOrThyroid_increasesResistance() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setHypothalamus(9);
        body.getBodySystems().getHormonalGlandularSystem().setThyroid(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getFatigueResistance()).isGreaterThan(defaults.getFatigueResistance());
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
        BodySystems bodySystems = new BodySystems(new BloodSystem(9, 3), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), NeuralSystem.defaults(), HormonalGlandularSystem.defaults(),
                DigestiveSystem.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), bodySystems, PhysicalTraits.defaults()));
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

    @Test
    void getDurability_higherSkinThickness_increasesDurability() {
        // skinThickness is immutable, so a thicker-skinned character needs its own preview body.
        BodyStructure thickSkin = new BodyStructure(4, 5, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), thickSkin);
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getDurability()).isGreaterThan(defaults.getDurability());
    }

    // -------------------------------------------------------------------------
    // Sight / Hearing / Smell (Delta V4: read Thalamus instead of Hippocampus)
    // -------------------------------------------------------------------------

    @Test
    void getSight_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSight()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getHearing_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getHearing()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getSmell_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSmell()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getSight_higherThalamusOrNeuralDrive_increasesAllThreeSenses() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setThalamus(9);
        body.getBodySystems().getNeuralSystem().setNeuralDrive(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSight()).isGreaterThan(defaults.getSight());
        assertThat(character.getHearing()).isGreaterThan(defaults.getHearing());
        assertThat(character.getSmell()).isGreaterThan(defaults.getSmell());
    }

    @Test
    void getSight_isNotAffectedByHippocampus() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setHippocampus(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSight()).isCloseTo(defaults.getSight(), within(TOLERANCE));
        assertThat(character.getHearing()).isCloseTo(defaults.getHearing(), within(TOLERANCE));
        assertThat(character.getSmell()).isCloseTo(defaults.getSmell(), within(TOLERANCE));
    }

    @Test
    void getSight_higherEyesSensitivity_increasesSightOnlyNotHearingOrSmell() {
        SensorialOrgans sensorialOrgans = new SensorialOrgans(9, 5, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(sensorialOrgans, BodyStructure.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSight()).isGreaterThan(defaults.getSight());
        assertThat(character.getHearing()).isCloseTo(defaults.getHearing(), within(TOLERANCE));
        assertThat(character.getSmell()).isCloseTo(defaults.getSmell(), within(TOLERANCE));
    }

    @Test
    void getHearing_higherEarsSensitivity_increasesHearingOnlyNotSightOrSmell() {
        SensorialOrgans sensorialOrgans = new SensorialOrgans(5, 9, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(sensorialOrgans, BodyStructure.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getHearing()).isGreaterThan(defaults.getHearing());
        assertThat(character.getSight()).isCloseTo(defaults.getSight(), within(TOLERANCE));
        assertThat(character.getSmell()).isCloseTo(defaults.getSmell(), within(TOLERANCE));
    }

    @Test
    void getSmell_higherNoseSensitivity_increasesSmellOnlyNotSightOrHearing() {
        SensorialOrgans sensorialOrgans = new SensorialOrgans(5, 5, 9);
        PhysicalTraits physicalTraits = new PhysicalTraits(sensorialOrgans, BodyStructure.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSmell()).isGreaterThan(defaults.getSmell());
        assertThat(character.getSight()).isCloseTo(defaults.getSight(), within(TOLERANCE));
        assertThat(character.getHearing()).isCloseTo(defaults.getHearing(), within(TOLERANCE));
    }

    @Test
    void getSight_higherPredominantMorphicHormoneAboveNeutral_increasesAllThreeSensesViaProgesteroneModifier() {
        HormonalGlandularSystem highHormone = new HormonalGlandularSystem(5, 5, 9, 0); // Pmod = 4
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), NeuralSystem.defaults(), highHormone, DigestiveSystem.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), bodySystems, PhysicalTraits.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSight()).isGreaterThan(defaults.getSight());
        assertThat(character.getHearing()).isGreaterThan(defaults.getHearing());
        assertThat(character.getSmell()).isGreaterThan(defaults.getSmell());
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
        body.getBodySystems().getNeuralSystem().setAgility(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getEvasion()).isGreaterThan(defaults.getEvasion());
    }

    @Test
    void getEvasion_isFlooredWhenAnExtremeCoefficientDrivesItBelowTheFloor() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setAgility(1);
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
        body.getBodySystems().getNeuralSystem().setAgility(9);
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
        body.getBodySystems().getNeuralSystem().setPrecision(9);
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
    void getAim_higherThalamusOrPrecision_increasesAim() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setThalamus(9);
        body.getBodySystems().getNeuralSystem().setPrecision(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getAim()).isGreaterThan(defaults.getAim());
    }

    @Test
    void getAim_isNotAffectedByHippocampusOrEyesSensitivity() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setHippocampus(9);
        PlayableCharacter viaHippocampus = new PlayableCharacter("test", body);

        SensorialOrgans keenEyes = new SensorialOrgans(9, 5, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(keenEyes, BodyStructure.defaults());
        PlayableCharacter viaEyes = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits));

        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(viaHippocampus.getAim()).isCloseTo(defaults.getAim(), within(TOLERANCE));
        assertThat(viaEyes.getAim()).isCloseTo(defaults.getAim(), within(TOLERANCE));
    }

    // -------------------------------------------------------------------------
    // Cognitive / Mental (rpg-13) — MemoryPool/ShortMemory still read Hippocampus (unchanged
    // by the Delta V4 Thalamus split), reweighted in Delta V4
    // -------------------------------------------------------------------------

    @Test
    void getMemoryPool_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMemoryPool()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getMemoryPool_atExtremes_staysWithinTwentyToOneHundred() {
        Body max = Body.humanTemplate();
        max.getBodySystems().getNeuralSystem().setCerebralCapacity(9);
        max.getBodySystems().getNeuralSystem().setHippocampus(9);
        Body min = Body.humanTemplate();
        min.getBodySystems().getNeuralSystem().setCerebralCapacity(1);
        min.getBodySystems().getNeuralSystem().setHippocampus(1);

        assertThat(new PlayableCharacter("test", max).getMemoryPool()).isCloseTo(100.0, within(TOLERANCE));
        assertThat(new PlayableCharacter("test", min).getMemoryPool()).isCloseTo(20.0, within(TOLERANCE));
    }

    @Test
    void getReasoning_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getReasoning()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getReasoning_atExtremes_staysWithinTwentyToOneHundred() {
        Body max = Body.humanTemplate();
        max.getBodySystems().getNeuralSystem().setSynapsisQuality(9);
        Body min = Body.humanTemplate();
        min.getBodySystems().getNeuralSystem().setSynapsisQuality(1);

        assertThat(new PlayableCharacter("test", max).getReasoning()).isCloseTo(100.0, within(TOLERANCE));
        assertThat(new PlayableCharacter("test", min).getReasoning()).isCloseTo(20.0, within(TOLERANCE));
    }

    @Test
    void getShortMemory_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getShortMemory()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getShortMemory_atExtremes_staysWithinTwentyToOneHundred() {
        Body max = Body.humanTemplate();
        max.getBodySystems().getNeuralSystem().setCerebralCapacity(9);
        max.getBodySystems().getNeuralSystem().setSynapsisQuality(9);
        max.getBodySystems().getNeuralSystem().setHippocampus(9);
        Body min = Body.humanTemplate();
        min.getBodySystems().getNeuralSystem().setCerebralCapacity(1);
        min.getBodySystems().getNeuralSystem().setSynapsisQuality(1);
        min.getBodySystems().getNeuralSystem().setHippocampus(1);

        assertThat(new PlayableCharacter("test", max).getShortMemory()).isCloseTo(100.0, within(TOLERANCE));
        assertThat(new PlayableCharacter("test", min).getShortMemory()).isCloseTo(20.0, within(TOLERANCE));
    }

    @Test
    void getMentalHealthPool_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMentalHealthPool()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getMentalHealthPool_higherAmygdala_reducesPool() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(9);
        PlayableCharacter character = new PlayableCharacter("test", body);

        // 60 - 5*(9-5) = 40 (kMentalHealthAmygdala reweighted 10->5 in rpg-14)
        assertThat(character.getMentalHealthPool()).isCloseTo(40.0, within(TOLERANCE));
    }

    @Test
    void getMentalHealthPool_atExtremes_staysWithinTwentyToOneHundred() {
        Body max = Body.humanTemplate();
        max.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(1); // +20
        max.getBodySystems().getHormonalGlandularSystem().setPredominantMorphicHormone(9); // Pmod=4, +20
        Body min = Body.humanTemplate();
        min.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(9); // -20
        min.getBodySystems().getHormonalGlandularSystem().setPredominantMorphicHormone(1); // Tmod=4, -20

        assertThat(new PlayableCharacter("test", max).getMentalHealthPool()).isCloseTo(100.0, within(TOLERANCE));
        assertThat(new PlayableCharacter("test", min).getMentalHealthPool()).isCloseTo(20.0, within(TOLERANCE));
    }

    @Test
    void getWill_alwaysEqualsMentalHealthPool() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(2);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getWill()).isCloseTo(character.getMentalHealthPool(), within(TOLERANCE));
    }

    // -------------------------------------------------------------------------
    // Balance (Delta V4: Thalamus + NeuralDrive (kept) + LegDrive term; TendonsAndLigaments and
    // Hippocampus dropped — tendons already factor into LegDrive)
    // -------------------------------------------------------------------------

    @Test
    void getBalance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getBalance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getBalance_higherThalamusOrNeuralDrive_increasesBalance() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setThalamus(9);
        body.getBodySystems().getNeuralSystem().setNeuralDrive(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getBalance()).isGreaterThan(defaults.getBalance());
    }

    @Test
    void getBalance_isNotAffectedByHippocampus() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setHippocampus(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getBalance()).isCloseTo(defaults.getBalance(), within(TOLERANCE));
    }

    @Test
    void getBalance_higherLegDrive_increasesBalanceViaTheLegDriveTerm() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(1); // leg-biased -> higher LegDrive
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getLegDrive()).isGreaterThan(defaults.getLegDrive());
        assertThat(character.getBalance()).isGreaterThan(defaults.getBalance());
    }

    @Test
    void getStressResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getStressResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getStressResistance_atExtremes_staysWithinTwentyToOneHundred() {
        Body max = Body.humanTemplate();
        max.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(1);
        max.getBodySystems().getHormonalGlandularSystem().setAdrenalGlands(1);
        Body min = Body.humanTemplate();
        min.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(9);
        min.getBodySystems().getHormonalGlandularSystem().setAdrenalGlands(9);

        assertThat(new PlayableCharacter("test", max).getStressResistance()).isCloseTo(100.0, within(TOLERANCE));
        assertThat(new PlayableCharacter("test", min).getStressResistance()).isCloseTo(20.0, within(TOLERANCE));
    }

    // -------------------------------------------------------------------------
    // Anger Resistance / Fear Resistance / Pain Threshold (Delta V4 — new attributes)
    // -------------------------------------------------------------------------

    @Test
    void getAngerResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getAngerResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getFearResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getFearResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getAngerResistanceAndFearResistance_areIdenticalFormulas_bothReduceWithHigherAmygdala() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(9);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double expected = 60 - 10 * (9 - 5);
        assertThat(character.getAngerResistance()).isCloseTo(expected, within(TOLERANCE));
        assertThat(character.getFearResistance()).isCloseTo(expected, within(TOLERANCE));
        assertThat(character.getAngerResistance()).isCloseTo(character.getFearResistance(), within(TOLERANCE));
    }

    @Test
    void getPainThreshold_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getPainThreshold()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getPainThreshold_usesBodyFatsOwnNeutralOfThreeNotFive() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setBodyFat(6);
        PlayableCharacter character = new PlayableCharacter("test", body);

        double expected = 60 + 3 * (6 - 3);
        assertThat(character.getPainThreshold()).isCloseTo(expected, within(TOLERANCE));
    }

    @Test
    void getPainThreshold_higherSkinThickness_increasesThreshold() {
        BodyStructure thickSkin = new BodyStructure(4, 5, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), thickSkin);
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getPainThreshold()).isGreaterThan(defaults.getPainThreshold());
    }

    @Test
    void getPainThreshold_higherAmygdala_reducesThreshold() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getPainThreshold()).isLessThan(defaults.getPainThreshold());
    }

    // -------------------------------------------------------------------------
    // Biological defense (rpg-13)
    // -------------------------------------------------------------------------

    @Test
    void getPoisonResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getPoisonResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getPoisonResistance_atExtremes_reflectsTheAddedCellularHealthTerm() {
        // Cellular health (added rpg-14) widens the old exact [20,100] bounds by +-8.
        NeuralSystem maxNeural = new NeuralSystem(5, 5, 5, 5, 5, 5, 5, 5, 9, 5, 5, 0); // immunity=9
        BodyStructure maxStructure = new BodyStructure(3, 5, 9); // cellularHealth=9
        BodySystems maxSystems = new BodySystems(new BloodSystem(5, 1), new CardiacSystem(1, 0), PulmonarySystem.defaults(),
                maxNeural, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        PhysicalTraits maxTraits = new PhysicalTraits(SensorialOrgans.defaults(), maxStructure);
        PlayableCharacter max = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), maxSystems, maxTraits));

        NeuralSystem minNeural = new NeuralSystem(5, 5, 5, 5, 5, 5, 5, 5, 1, 5, 5, 0); // immunity=1
        BodyStructure minStructure = new BodyStructure(3, 5, 1); // cellularHealth=1
        BodySystems minSystems = new BodySystems(new BloodSystem(5, 5), new CardiacSystem(9, 0), PulmonarySystem.defaults(),
                minNeural, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        PhysicalTraits minTraits = new PhysicalTraits(SensorialOrgans.defaults(), minStructure);
        PlayableCharacter min = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), minSystems, minTraits));

        assertThat(max.getPoisonResistance()).isCloseTo(108.0, within(TOLERANCE));
        assertThat(min.getPoisonResistance()).isCloseTo(12.0, within(TOLERANCE));
    }

    @Test
    void getPoisonResistance_higherCellularHealth_increasesResistance() {
        BodyStructure healthy = new BodyStructure(3, 5, 9);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), healthy);
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getPoisonResistance()).isGreaterThan(defaults.getPoisonResistance());
    }

    @Test
    void getDiseaseResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getDiseaseResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getDiseaseResistance_atExtremes_reflectsTheAddedCellularHealthTerm() {
        // Cellular health (added rpg-14) widens the old exact [20,100] bounds by +-8.
        BodyStructure maxStructure = new BodyStructure(3, 5, 9);
        PhysicalTraits maxTraits = new PhysicalTraits(SensorialOrgans.defaults(), maxStructure);
        Body max = Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), maxTraits);
        max.getBodySystems().getNeuralSystem().setImmunity(9);
        max.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(9);

        BodyStructure minStructure = new BodyStructure(3, 5, 1);
        PhysicalTraits minTraits = new PhysicalTraits(SensorialOrgans.defaults(), minStructure);
        Body min = Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), minTraits);
        min.getBodySystems().getNeuralSystem().setImmunity(1);
        min.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(1);

        assertThat(new PlayableCharacter("test", max).getDiseaseResistance()).isCloseTo(108.0, within(TOLERANCE));
        assertThat(new PlayableCharacter("test", min).getDiseaseResistance()).isCloseTo(12.0, within(TOLERANCE));
    }

    @Test
    void getDiseaseResistance_higherCellularHealth_increasesResistance() {
        BodyStructure healthy = new BodyStructure(3, 5, 9);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), healthy);
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getDiseaseResistance()).isGreaterThan(defaults.getDiseaseResistance());
    }

    @Test
    void getBleedingResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getBleedingResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getBleedingResistance_higherBloodThickness_increasesResistance() {
        BodySystems bodySystems = new BodySystems(new BloodSystem(5, 5), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), NeuralSystem.defaults(), HormonalGlandularSystem.defaults(),
                DigestiveSystem.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), bodySystems, PhysicalTraits.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getBleedingResistance()).isGreaterThan(defaults.getBleedingResistance());
    }

    // -------------------------------------------------------------------------
    // Metabolic / survival (rpg-13, DigestiveAbsorption rename Delta V4)
    // -------------------------------------------------------------------------

    @Test
    void getThermalResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getThermalResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getThermalResistance_humanUiCeiling_isEightyThree() {
        // SkinThickness UI-locked to 4 for humans; BodyFat and Hypothalamus at their true max
        BodyComposition composition = new BodyComposition(10, 5, 5, 5, 5, 5, 5);
        NeuralSystem neuralSystem = new NeuralSystem(5, 5, 5, 5, 5, 5, 9, 5, 5, 5, 5, 0);
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), neuralSystem, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        BodyStructure humanMaxSkin = new BodyStructure(4, 5, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), humanMaxSkin);
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(Genetics.defaults(), composition), bodySystems, physicalTraits));

        assertThat(character.getThermalResistance()).isCloseTo(83.0, within(TOLERANCE));
    }

    @Test
    void getThermalResistance_trueRaceCeiling_neverExceedsOneHundred() {
        BodyComposition composition = new BodyComposition(10, 5, 5, 5, 5, 5, 5);
        NeuralSystem neuralSystem = new NeuralSystem(5, 5, 5, 5, 5, 5, 9, 5, 5, 5, 5, 0);
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), neuralSystem, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        BodyStructure raceMaxSkin = new BodyStructure(7, 5, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), raceMaxSkin);
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(Genetics.defaults(), composition), bodySystems, physicalTraits));

        assertThat(character.getThermalResistance()).isLessThanOrEqualTo(100.0);
    }

    @Test
    void getBreathOutput_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getBreathOutput()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getBreathOutput_atExtremes_staysWithinTwentyToOneHundred() {
        Body max = Body.humanTemplate();
        max.getBodySystems().getPulmonarySystem().setPulmonaryCapacity(9);
        Body min = Body.humanTemplate();
        min.getBodySystems().getPulmonarySystem().setPulmonaryCapacity(1);

        assertThat(new PlayableCharacter("test", max).getBreathOutput()).isCloseTo(100.0, within(TOLERANCE));
        assertThat(new PlayableCharacter("test", min).getBreathOutput()).isCloseTo(20.0, within(TOLERANCE));
    }

    @Test
    void getDehydrationResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getDehydrationResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getStarvationResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getStarvationResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getStarvationResistance_higherDigestiveAbsorption_increasesResistance() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getDigestiveSystem().setDigestiveAbsorption(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getStarvationResistance()).isGreaterThan(defaults.getStarvationResistance());
    }

    @Test
    void getFoodPoisoningAlcoholResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getFoodPoisoningAlcoholResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getFoodPoisoningAlcoholResistance_higherImpurityCleaningOrImmunity_increasesResistance() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getDigestiveSystem().setImpurityCleaning(9);
        body.getBodySystems().getNeuralSystem().setImmunity(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getFoodPoisoningAlcoholResistance())
                .isGreaterThan(defaults.getFoodPoisoningAlcoholResistance());
    }

    @Test
    void getFoodPoisoningAlcoholResistance_higherCellularHealth_increasesResistance() {
        BodyStructure healthy = new BodyStructure(3, 5, 9);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), healthy);
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getFoodPoisoningAlcoholResistance())
                .isGreaterThan(defaults.getFoodPoisoningAlcoholResistance());
    }

    @Test
    void getFoodPoisoningAlcoholResistance_higherDigestiveAbsorption_slightlyDecreasesResistance() {
        Body body = Body.humanTemplate();
        body.getBodySystems().getDigestiveSystem().setDigestiveAbsorption(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        double expected = 60 - 1 * (9 - 5);
        assertThat(character.getFoodPoisoningAlcoholResistance()).isCloseTo(expected, within(TOLERANCE));
        assertThat(character.getFoodPoisoningAlcoholResistance())
                .isLessThan(defaults.getFoodPoisoningAlcoholResistance());
    }

    // -------------------------------------------------------------------------
    // Body-growth rates (rpg-14) — zero-baseline, equal 0 (not 60) at human defaults
    // -------------------------------------------------------------------------

    @Test
    void getFatGainRate_onHumanDefaults_equalsZero() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getFatGainRate()).isCloseTo(0.0, within(TOLERANCE));
    }

    @Test
    void getFatGainRate_higherEndomorphyOrDigestiveAbsorption_increasesRate() {
        Genetics highEndomorphy = new Genetics(9, 5, 5, 7, 3);
        Body body = Body.previewTemplate(new Biomechanics(highEndomorphy, BodyComposition.defaults()),
                BodySystems.defaults(), PhysicalTraits.defaults());
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getFatGainRate()).isGreaterThan(defaults.getFatGainRate());
    }

    @Test
    void getFatGainRate_higherEctomorphyOrKetosisOrCellularHealth_decreasesRate() {
        Genetics highEctomorphy = new Genetics(5, 5, 9, 7, 3);
        Body body = Body.previewTemplate(new Biomechanics(highEctomorphy, BodyComposition.defaults()),
                BodySystems.defaults(), PhysicalTraits.defaults());
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getFatGainRate()).isLessThan(defaults.getFatGainRate());
    }

    @Test
    void getMuscleGainRate_onHumanDefaults_equalsZero() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMuscleGainRate()).isCloseTo(0.0, within(TOLERANCE));
    }

    @Test
    void getMuscleGainRate_higherMesomorphyOrDigestiveAbsorptionOrTestosterone_increasesRate() {
        Genetics highMesomorphy = new Genetics(5, 9, 5, 7, 3);
        HormonalGlandularSystem lowHormone = new HormonalGlandularSystem(5, 5, 1, 0); // Tmod = 4
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), NeuralSystem.defaults(), lowHormone, DigestiveSystem.defaults());
        Body body = Body.previewTemplate(new Biomechanics(highMesomorphy, BodyComposition.defaults()), bodySystems,
                PhysicalTraits.defaults());
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMuscleGainRate()).isGreaterThan(defaults.getMuscleGainRate());
    }

    // -------------------------------------------------------------------------
    // Social attributes (rpg-14)
    // -------------------------------------------------------------------------

    @Test
    void getIntimidation_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getIntimidation()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getIntimidation_lowerShapeAestheticsOrHigherTestosteroneOrMass_increasesIntimidation() {
        BodyStructure repulsive = new BodyStructure(3, 1, 5);
        HormonalGlandularSystem lowHormone = new HormonalGlandularSystem(5, 5, 1, 0); // Tmod = 4
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), NeuralSystem.defaults(), lowHormone, DigestiveSystem.defaults());
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), repulsive);
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), bodySystems, physicalTraits));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getIntimidation()).isGreaterThan(defaults.getIntimidation());
    }

    @Test
    void getDiplomacy_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getDiplomacy()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getDiplomacy_higherShapeAestheticsOrProgesterone_increasesDiplomacy() {
        BodyStructure attractive = new BodyStructure(3, 9, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), attractive);
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getDiplomacy()).isGreaterThan(defaults.getDiplomacy());
    }

    @Test
    void getEnfactuation_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getEnfactuation()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getEnfactuation_currentlyEqualsDiplomacy_sinceBothShareTheSameInputsToday() {
        BodyStructure attractive = new BodyStructure(3, 9, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), attractive);
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits));

        assertThat(character.getEnfactuation()).isCloseTo(character.getDiplomacy(), within(TOLERANCE));
    }

    @Test
    void getCommand_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getCommand()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getCommand_isVShaped_bothRepulsiveAndAttractiveExtremesRaiseCommandEqually() {
        BodyStructure repulsive = new BodyStructure(3, 1, 5);
        BodyStructure attractive = new BodyStructure(3, 9, 5);
        PlayableCharacter repulsiveCharacter = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(),
                        new PhysicalTraits(SensorialOrgans.defaults(), repulsive)));
        PlayableCharacter attractiveCharacter = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(),
                        new PhysicalTraits(SensorialOrgans.defaults(), attractive)));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(repulsiveCharacter.getCommand()).isCloseTo(attractiveCharacter.getCommand(), within(TOLERANCE));
        assertThat(repulsiveCharacter.getCommand()).isGreaterThan(defaults.getCommand());
    }

    // -------------------------------------------------------------------------
    // Load capacity (Delta V4 — now derives from LiftStrength, not the old global Strength)
    // -------------------------------------------------------------------------

    @Test
    void getMaxCapacityKg_onHumanDefaults_derivesFromLiftStrengthSquaredOverOneFiftyPlusLiftStrength() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        // LiftStrength = 60; MaxCapacityKg = floor(60^2 / 150) + 60 = 24 + 60
        assertThat(character.getMaxCapacityKg()).isEqualTo(84);
    }

    @Test
    void getLightLoadKg_isExactlyOneThirdOfMaxCapacityKg() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getLightLoadKg()).isEqualTo(28); // floor(84 / 3)
    }

    @Test
    void getHeavyLoadKg_isExactlyTwoThirdsOfMaxCapacityKg() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getHeavyLoadKg()).isEqualTo(56); // floor(84 * 2 / 3)
    }

    @Test
    void getDragCapacityKg_combinesMaxCapacityKgAndDisplayMassKg() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        // 2 * 84 + floor(71 * 0.5) = 168 + 35
        assertThat(character.getDragCapacityKg()).isEqualTo(203);
    }

    @Test
    void getMaxCapacityKg_higherLiftStrength_increasesEveryLoadFigure() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleMass(15);
        PlayableCharacter stronger = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(stronger.getMaxCapacityKg()).isGreaterThan(defaults.getMaxCapacityKg());
        assertThat(stronger.getLightLoadKg()).isGreaterThan(defaults.getLightLoadKg());
        assertThat(stronger.getHeavyLoadKg()).isGreaterThan(defaults.getHeavyLoadKg());
        assertThat(stronger.getDragCapacityKg()).isGreaterThan(defaults.getDragCapacityKg());
    }

    @Test
    void getMaxCapacityKg_flooredWhenLiftStrengthIsFlooredByAnExtremeCoefficient() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleMass(1);
        body.getCoefficients().setKMeanStrengthMuscleMass(1000);
        PlayableCharacter character = new PlayableCharacter("test", body);

        int floor = (int) body.getCoefficients().getAttributeFloor();
        int expected = (int) Math.floor(Math.pow(floor, 2) / 150.0) + floor;
        assertThat(character.getMaxCapacityKg()).isEqualTo(expected);
    }
}
