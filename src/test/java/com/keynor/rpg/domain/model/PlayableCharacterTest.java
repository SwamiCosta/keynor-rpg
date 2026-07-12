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
    void getTotalMassKg_onHumanDefaults_equalsSeventyOne() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getTotalMassKg()).isCloseTo(71.0, within(TOLERANCE));
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
        NeuralSystem neuralSystem = new NeuralSystem(5, 9, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0);
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
    // Speed and MovementSpeed
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
        NeuralSystem neuralSystem = new NeuralSystem(5, 1, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0);
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
    void getMovementSpeed_onBalancedMuscleDistributionAndNeutralLimbRatioAndHeight_equalsSpeed() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMovementSpeed()).isCloseTo(character.getSpeed(), within(TOLERANCE));
    }

    @Test
    void getMovementSpeed_legBiasedMuscleDistribution_isHigherThanSpeed() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(1);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getMovementSpeed()).isGreaterThan(character.getSpeed());
    }

    @Test
    void getMovementSpeed_armBiasedMuscleDistribution_isLowerThanSpeed() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(9);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getMovementSpeed()).isLessThan(character.getSpeed());
    }

    @Test
    void getMovementSpeed_longerLimbRatio_increasesMovementSpeed() {
        Genetics longLimbs = new Genetics(5, 5, 5, 7, 5);
        PlayableCharacter longLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(longLimbs, BodyComposition.defaults()), BodySystems.defaults(),
                        PhysicalTraits.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(longLimbed.getMovementSpeed()).isGreaterThan(defaults.getMovementSpeed());
    }

    @Test
    void getMovementSpeed_shorterLimbRatio_reducesMovementSpeed() {
        Genetics shortLimbs = new Genetics(5, 5, 5, 7, 1);
        PlayableCharacter shortLimbed = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(shortLimbs, BodyComposition.defaults()), BodySystems.defaults(),
                        PhysicalTraits.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(shortLimbed.getMovementSpeed()).isLessThan(defaults.getMovementSpeed());
    }

    @Test
    void getMovementSpeed_tallerHeight_increasesMovementSpeed() {
        Genetics tall = new Genetics(5, 5, 5, 15, 3);
        PlayableCharacter taller = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(tall, BodyComposition.defaults()), BodySystems.defaults(),
                        PhysicalTraits.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(taller.getMovementSpeed()).isGreaterThan(defaults.getMovementSpeed());
    }

    @Test
    void getMovementSpeed_isFlooredWhenAnExtremeCoefficientDrivesItBelowTheFloor() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setMuscleDistribution(9);
        body.getCoefficients().setKMovementSpeedMuscleDistribution(1000);
        PlayableCharacter character = new PlayableCharacter("test", body);

        assertThat(character.getMovementSpeed()).isEqualTo(body.getCoefficients().getAttributeFloor());
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
        NeuralSystem neuralSystem = new NeuralSystem(5, 9, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0);
        BodySystems bodySystems = new BodySystems(new BloodSystem(1, 3), new CardiacSystem(1, 0, 0),
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
    // Soft Tissue Durability / Bone Durability (rpg-21, replaces the old unified Durability)
    // -------------------------------------------------------------------------

    @Test
    void getSoftTissueDurability_onHumanDefaults_equalsTen() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSoftTissueDurability()).isCloseTo(10.0, within(TOLERANCE));
    }

    @Test
    void getSoftTissueDurability_higherFlexibility_decreasesSoftTissueDurability() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setFlexibility(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getSoftTissueDurability()).isLessThan(defaults.getSoftTissueDurability());
    }

    @Test
    void getSoftTissueDurability_worstCaseCombination_isFlooredAtAttributeFloor() {
        BodyComposition worstComposition = new BodyComposition(1, 30, 0, 5, 9, 5, 5);
        Biomechanics biomechanics = new Biomechanics(new Genetics(5, 1, 5, 7, 3), worstComposition);
        BodyStructure worstSkin = new BodyStructure(1, 5, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), worstSkin, TrainingAndConditioning.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(biomechanics, BodySystems.defaults(), physicalTraits));

        assertThat(character.getSoftTissueDurability()).isEqualTo(5.0);
    }

    @Test
    void getBoneDurability_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getBoneDurability()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getBoneDurability_higherBoneDensity_increasesBoneDurability() {
        Body body = Body.humanTemplate();
        body.getBiomechanics().getBodyComposition().setBoneDensity(9);
        PlayableCharacter character = new PlayableCharacter("test", body);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getBoneDurability()).isGreaterThan(defaults.getBoneDurability());
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
        PhysicalTraits physicalTraits = new PhysicalTraits(sensorialOrgans, BodyStructure.defaults(), TrainingAndConditioning.defaults());
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
        PhysicalTraits physicalTraits = new PhysicalTraits(sensorialOrgans, BodyStructure.defaults(), TrainingAndConditioning.defaults());
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
        PhysicalTraits physicalTraits = new PhysicalTraits(sensorialOrgans, BodyStructure.defaults(), TrainingAndConditioning.defaults());
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
        PhysicalTraits physicalTraits = new PhysicalTraits(keenEyes, BodyStructure.defaults(), TrainingAndConditioning.defaults());
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
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), thickSkin, TrainingAndConditioning.defaults());
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
        NeuralSystem maxNeural = new NeuralSystem(5, 5, 5, 5, 5, 5, 5, 5, 9, 5, 5, 0, 0); // immunity=9
        BodyStructure maxStructure = new BodyStructure(3, 5, 9); // cellularHealth=9
        BodySystems maxSystems = new BodySystems(new BloodSystem(5, 1), new CardiacSystem(1, 0, 0), PulmonarySystem.defaults(),
                maxNeural, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        PhysicalTraits maxTraits = new PhysicalTraits(SensorialOrgans.defaults(), maxStructure, TrainingAndConditioning.defaults());
        PlayableCharacter max = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), maxSystems, maxTraits));

        NeuralSystem minNeural = new NeuralSystem(5, 5, 5, 5, 5, 5, 5, 5, 1, 5, 5, 0, 0); // immunity=1
        BodyStructure minStructure = new BodyStructure(3, 5, 1); // cellularHealth=1
        BodySystems minSystems = new BodySystems(new BloodSystem(5, 5), new CardiacSystem(9, 0, 0), PulmonarySystem.defaults(),
                minNeural, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        PhysicalTraits minTraits = new PhysicalTraits(SensorialOrgans.defaults(), minStructure, TrainingAndConditioning.defaults());
        PlayableCharacter min = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), minSystems, minTraits));

        assertThat(max.getPoisonResistance()).isCloseTo(108.0, within(TOLERANCE));
        assertThat(min.getPoisonResistance()).isCloseTo(12.0, within(TOLERANCE));
    }

    @Test
    void getPoisonResistance_higherCellularHealth_increasesResistance() {
        BodyStructure healthy = new BodyStructure(3, 5, 9);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), healthy, TrainingAndConditioning.defaults());
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
        PhysicalTraits maxTraits = new PhysicalTraits(SensorialOrgans.defaults(), maxStructure, TrainingAndConditioning.defaults());
        Body max = Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), maxTraits);
        max.getBodySystems().getNeuralSystem().setImmunity(9);
        max.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(9);

        BodyStructure minStructure = new BodyStructure(3, 5, 1);
        PhysicalTraits minTraits = new PhysicalTraits(SensorialOrgans.defaults(), minStructure, TrainingAndConditioning.defaults());
        Body min = Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), minTraits);
        min.getBodySystems().getNeuralSystem().setImmunity(1);
        min.getBodySystems().getNeuralSystem().setAmygdalaAndCingulum(1);

        assertThat(new PlayableCharacter("test", max).getDiseaseResistance()).isCloseTo(108.0, within(TOLERANCE));
        assertThat(new PlayableCharacter("test", min).getDiseaseResistance()).isCloseTo(12.0, within(TOLERANCE));
    }

    @Test
    void getDiseaseResistance_higherCellularHealth_increasesResistance() {
        BodyStructure healthy = new BodyStructure(3, 5, 9);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), healthy, TrainingAndConditioning.defaults());
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
        NeuralSystem neuralSystem = new NeuralSystem(5, 5, 5, 5, 5, 5, 9, 5, 5, 5, 5, 0, 0);
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), neuralSystem, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        BodyStructure humanMaxSkin = new BodyStructure(4, 5, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), humanMaxSkin, TrainingAndConditioning.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(new Biomechanics(Genetics.defaults(), composition), bodySystems, physicalTraits));

        assertThat(character.getThermalResistance()).isCloseTo(83.0, within(TOLERANCE));
    }

    @Test
    void getThermalResistance_trueRaceCeiling_neverExceedsOneHundred() {
        BodyComposition composition = new BodyComposition(10, 5, 5, 5, 5, 5, 5);
        NeuralSystem neuralSystem = new NeuralSystem(5, 5, 5, 5, 5, 5, 9, 5, 5, 5, 5, 0, 0);
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), neuralSystem, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        BodyStructure raceMaxSkin = new BodyStructure(7, 5, 5);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), raceMaxSkin, TrainingAndConditioning.defaults());
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
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), healthy, TrainingAndConditioning.defaults());
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
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), repulsive, TrainingAndConditioning.defaults());
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
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), attractive, TrainingAndConditioning.defaults());
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
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), attractive, TrainingAndConditioning.defaults());
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
                        new PhysicalTraits(SensorialOrgans.defaults(), repulsive, TrainingAndConditioning.defaults())));
        PlayableCharacter attractiveCharacter = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(),
                        new PhysicalTraits(SensorialOrgans.defaults(), attractive, TrainingAndConditioning.defaults())));
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

    // -------------------------------------------------------------------------
    // Mind pillar (new) — Concern mirrors, cross-pillar terms on existing Body attributes, and
    // 9 new Mind-driven attributes. Human template pairs Body.humanTemplate() with
    // Mind.humanTemplate() (every Value at its own neutral, 1), so every Mind-driven deviation
    // term is zero and every baseline-60 Mind attribute equals exactly 60, same convention as
    // the Body-pillar tests above.
    // -------------------------------------------------------------------------

    private static PlayableCharacter withValues(java.util.function.Consumer<Values> customize) {
        Values values = Values.defaults();
        customize.accept(values);
        return new PlayableCharacter("test", Body.humanTemplate(),
                Mind.previewTemplate(values, Erudition.defaults(), Personality.defaults(), Labours.defaults(),
                        GeneralPersonality.defaults(), WeaponProficiencies.defaults()));
    }

    private static PlayableCharacter withTrait(Values values, Trait trait) {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(),
                Mind.previewTemplate(values, Erudition.defaults(), Personality.defaults(), Labours.defaults(),
                        GeneralPersonality.defaults(), WeaponProficiencies.defaults()));
        character.getMind().getPersonality().select(trait, character);
        return character;
    }

    @Test
    void concernAttributes_onHumanDefaults_mirrorEachValueAtOne() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getSelfConcern()).isEqualTo(1);
        assertThat(character.getFriendshipConcern()).isEqualTo(1);
        assertThat(character.getOrderConcern()).isEqualTo(1);
        assertThat(character.getFreedomConcern()).isEqualTo(1);
        assertThat(character.getPatriotismConcern()).isEqualTo(1);
        assertThat(character.getSpiritualConcern()).isEqualTo(1);
        assertThat(character.getPhilosophyConcern()).isEqualTo(1);
        assertThat(character.getAcademicConcern()).isEqualTo(1);
        assertThat(character.getEnvironmentalismConcern()).isEqualTo(1);
        assertThat(character.getMoralityConcern()).isEqualTo(1);
        assertThat(character.getTraditionalismConcern()).isEqualTo(1);
        assertThat(character.getJusticeConcern()).isEqualTo(1);
        assertThat(character.getProgressConcern()).isEqualTo(1);
        assertThat(character.getPeaceConcern()).isEqualTo(1);
    }

    @Test
    void concernAttribute_mirrorsItsValueDirectly_notADeviation() {
        PlayableCharacter character = withValues(values -> values.setKnowledge(4));

        assertThat(character.getAcademicConcern()).isEqualTo(4);
    }

    @Test
    void getShortMemory_higherKnowledgeValue_noLongerAffectsShortMemory_rpg19Revert() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter knowledgeable = withValues(values -> values.setKnowledge(5));

        assertThat(knowledgeable.getShortMemory()).isEqualTo(defaults.getShortMemory());
    }

    @Test
    void getReasoning_higherTruthValue_noLongerAffectsReasoning_rpg19Revert() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter truthful = withValues(values -> values.setTruth(5));

        assertThat(truthful.getReasoning()).isEqualTo(defaults.getReasoning());
    }

    @Test
    void getReasoning_relativistOrIliterateTrait_decreasesReasoning() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter relativist = withTrait(Values.defaults(), Trait.RELATIVIST);

        assertThat(relativist.getReasoning()).isLessThan(defaults.getReasoning());
    }

    @Test
    void getEnfactuation_higherLoyaltyValue_noLongerAffectsEnfactuation_rpg19Revert() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter loyal = withValues(values -> values.setLoyalty(5));

        assertThat(loyal.getEnfactuation()).isEqualTo(defaults.getEnfactuation());
    }

    @Test
    void getEnfactuation_relativistTrait_increasesEnfactuationButNotDiplomacy() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter relativist = withTrait(Values.defaults(), Trait.RELATIVIST);

        assertThat(relativist.getEnfactuation()).isGreaterThan(defaults.getEnfactuation());
        assertThat(relativist.getDiplomacy()).isEqualTo(defaults.getDiplomacy());
    }

    @Test
    void getWill_higherMoralityValue_noLongerAffectsWill_rpg19Revert() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter moral = withValues(values -> values.setMorality(5));

        assertThat(moral.getWill()).isEqualTo(defaults.getWill());
        assertThat(moral.getMentalHealthPool()).isEqualTo(defaults.getMentalHealthPool());
    }

    @Test
    void getWill_nihilistTrait_penalizesWillMoreThanMentalHealthPool() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Values values = Values.defaults();
        values.setMorality(1);
        PlayableCharacter reckless = withTrait(values, Trait.RECKLESS);
        reckless.getMind().getPersonality().select(Trait.NIHILIST, reckless);

        double willPenalty = defaults.getWill() - reckless.getWill();
        double mhpPenalty = defaults.getMentalHealthPool() - reckless.getMentalHealthPool();
        assertThat(willPenalty).isCloseTo(10.0, within(TOLERANCE));
        assertThat(mhpPenalty).isCloseTo(15.0, within(TOLERANCE));
    }

    @Test
    void getSurvivalSkills_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getSurvivalSkills()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getSurvivalSkills_withEcologyKnowledgeLevel_scalesPerPoint() {
        Mind mindLevelOne = Mind.previewTemplate(Values.defaults(),
                new Erudition(java.util.Map.of(Knowledge.ECOLOGY, 1)), Personality.defaults(), Labours.defaults(),
                GeneralPersonality.defaults(), WeaponProficiencies.defaults());
        Mind mindLevelTwo = Mind.previewTemplate(Values.defaults(),
                new Erudition(java.util.Map.of(Knowledge.ECOLOGY, 2)), Personality.defaults(), Labours.defaults(),
                GeneralPersonality.defaults(), WeaponProficiencies.defaults());
        PlayableCharacter levelOne = new PlayableCharacter("test", Body.humanTemplate(), mindLevelOne);
        PlayableCharacter levelTwo = new PlayableCharacter("test", Body.humanTemplate(), mindLevelTwo);

        assertThat(levelOne.getSurvivalSkills()).isCloseTo(62.0, within(TOLERANCE));
        assertThat(levelTwo.getSurvivalSkills()).isCloseTo(64.0, within(TOLERANCE));
    }

    @Test
    void getAnimalCaring_withEcologyAndBiologyKnowledgeLevels_scalesPerPoint() {
        Mind mind = Mind.previewTemplate(Values.defaults(),
                new Erudition(java.util.Map.of(Knowledge.ECOLOGY, 1, Knowledge.BIOLOGY, 1)), Personality.defaults(),
                Labours.defaults(), GeneralPersonality.defaults(), WeaponProficiencies.defaults());
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), mind);

        assertThat(character.getAnimalCaring()).isCloseTo(64.0, within(TOLERANCE));
    }

    @Test
    void getManipulationAndBehaviorReading_onHumanDefaults_equalBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getManipulation()).isCloseTo(60.0, within(TOLERANCE));
        assertThat(character.getBehaviorReading()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getDiscretion_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getDiscretion()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getDiscretion_shapeAestheticsAwayFromNeutralInEitherDirection_alwaysDecreasesDiscretion() {
        Body attractive = Body.humanTemplate();
        attractive.getPhysicalTraits().getBodyStructure().setShapeAesthetics(9);
        Body repulsive = Body.humanTemplate();
        repulsive.getPhysicalTraits().getBodyStructure().setShapeAesthetics(1);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter attractiveCharacter = new PlayableCharacter("test", attractive, Mind.humanTemplate());
        PlayableCharacter repulsiveCharacter = new PlayableCharacter("test", repulsive, Mind.humanTemplate());

        assertThat(attractiveCharacter.getDiscretion()).isLessThan(defaults.getDiscretion());
        assertThat(repulsiveCharacter.getDiscretion()).isLessThan(defaults.getDiscretion());
        assertThat(attractiveCharacter.getDiscretion()).isEqualTo(repulsiveCharacter.getDiscretion());
    }

    @Test
    void getBluffing_higherTruthOrMoralityValue_noLongerAffectsBluffing_rpg19Revert() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter honest = withValues(values -> {
            values.setTruth(5);
            values.setMorality(5);
        });

        assertThat(honest.getBluffing()).isEqualTo(defaults.getBluffing());
    }

    @Test
    void getFaith_higherDivinityValue_noLongerAffectsFaith_rpg19Revert() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter devout = withValues(values -> values.setDivinity(5));

        assertThat(devout.getFaith()).isEqualTo(defaults.getFaith());
    }

    @Test
    void getFaith_paganTrait_decreasesFaithAndMediunity() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter pagan = withTrait(Values.defaults(), Trait.PAGAN);

        assertThat(pagan.getFaith()).isLessThan(defaults.getFaith());
        assertThat(pagan.getMediunity()).isLessThan(defaults.getMediunity());
    }

    @Test
    void getIllusionResistance_higherTruthValue_noLongerAffectsIt_rpg19Revert() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter truthful = withValues(values -> values.setTruth(5));

        assertThat(truthful.getIllusionResistance()).isEqualTo(defaults.getIllusionResistance());
    }

    @Test
    void getIllusionResistance_practicalistCancelsRelativistPenalty() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter relativist = withTrait(Values.defaults(), Trait.RELATIVIST);
        double relativistOnlyResistance = relativist.getIllusionResistance();

        relativist.getMind().getPersonality().select(Trait.PRACTICALIST, relativist);

        assertThat(relativistOnlyResistance).isLessThan(defaults.getIllusionResistance());
        assertThat(relativist.getIllusionResistance()).isEqualTo(defaults.getIllusionResistance());
    }

    @Test
    void getCreativity_higherProgressValue_noLongerAffectsCreativity_rpg19Revert() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter progressive = withValues(values -> values.setProgress(5));

        assertThat(progressive.getCreativity()).isEqualTo(defaults.getCreativity());
    }

    @Test
    void getCreativity_orphanMindTrait_increasesCreativityAndKnowledgePoints() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter orphan = withTrait(Values.defaults(), Trait.ORPHAN_MIND);

        assertThat(orphan.getCreativity()).isGreaterThan(defaults.getCreativity());
        assertThat(orphan.getMind().getErudition().getEffectivePoints(orphan)).isEqualTo(Erudition.BASE_POINTS + 1);
    }

    @Test
    void getMediunity_renamedFromSixthSense_onHumanDefaultAbsentOrgan_equalsTwelve() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMediunity()).isCloseTo(12.0, within(TOLERANCE));
    }

    // -------------------------------------------------------------------------
    // rpg-19 — Analysis, Close/Low/Long Range Combat (new attributes)
    // -------------------------------------------------------------------------

    @Test
    void getAnalysis_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getAnalysis()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getAnalysis_higherReasoning_increasesAnalysis() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Body sharperMind = Body.humanTemplate();
        sharperMind.getBodySystems().getNeuralSystem().setSynapsisQuality(9);
        PlayableCharacter sharper = new PlayableCharacter("test", sharperMind, Mind.humanTemplate());

        assertThat(sharper.getReasoning()).isGreaterThan(defaults.getReasoning());
        assertThat(sharper.getAnalysis()).isGreaterThan(defaults.getAnalysis());
    }

    @Test
    void getCloseCombatAndLowRangeCombat_onHumanDefaults_equalBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getCloseCombat()).isCloseTo(60.0, within(TOLERANCE));
        assertThat(character.getLowRangeCombat()).isCloseTo(60.0, within(TOLERANCE));
        assertThat(character.getLongRangeCombat()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getCloseCombatAndLowRangeCombat_bellicoseTrait_increasesBoth() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter bellicose = withTrait(Values.defaults(), Trait.BELLICOSE);

        assertThat(bellicose.getCloseCombat()).isGreaterThan(defaults.getCloseCombat());
        assertThat(bellicose.getLowRangeCombat()).isGreaterThan(defaults.getLowRangeCombat());
        assertThat(bellicose.getLongRangeCombat()).isEqualTo(defaults.getLongRangeCombat());
    }

    // -------------------------------------------------------------------------
    // rpg-19 — Values-trait forced value and prerequisite gating (Personality)
    // -------------------------------------------------------------------------

    @Test
    void selectingABaseTrait_forcesItsLinkedValueToZero_andUnlocksTheAdvancedTrait() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());
        character.getMind().getPersonality().select(Trait.SELF_SACRIFICE, character);

        assertThat(character.getMind().getValues().getEgo()).isZero();
        assertThat(Trait.SUICIDAL.prerequisitesMet(character)).isTrue();
        assertThat(character.getFearResistance()).isGreaterThan(60.0);
        assertThat(character.getPainThreshold()).isGreaterThan(60.0);
    }

    // -------------------------------------------------------------------------
    // rpg-19 — Labours (Jobs) point budget
    // -------------------------------------------------------------------------

    @Test
    void labours_onHumanDefaults_hasBasePointsUnspent() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getMind().getLabours().getEffectivePoints(character)).isEqualTo(Labours.BASE_POINTS);
        assertThat(character.getMind().getLabours().getSpentPoints()).isZero();
    }

    @Test
    void labours_conservativeAndLudditeTraits_eachGrantOneExtraPoint() {
        Values values = Values.defaults();
        PlayableCharacter character = withTrait(values, Trait.CONSERVATIVE);
        character.getMind().getPersonality().select(Trait.LUDDITE, character);

        assertThat(character.getMind().getLabours().getEffectivePoints(character)).isEqualTo(Labours.BASE_POINTS + 2);
    }

    // -------------------------------------------------------------------------
    // Psyquism Output / Defense, Charm Resistance, Concentration, Purity — new attributes
    // alongside GeneralPersonality (Vanity/Focus) and NeuralSystem.phaxicCerebelum.
    // -------------------------------------------------------------------------

    @Test
    void getPsyquismOutputAndDefense_onHumanDefaults_equalTwelve() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

        // PhaxicCerebelum absent (0) on the human template: 60 + 8*(0-6) = 12
        assertThat(character.getPsyquismOutput()).isCloseTo(12.0, within(TOLERANCE));
        assertThat(character.getPsyquismDefense()).isCloseTo(12.0, within(TOLERANCE));
    }

    @Test
    void getPsyquismOutput_higherPhaxicCerebelumOrCerebralCapacity_increasesOutput() {
        NeuralSystem gifted = new NeuralSystem(5, 5, 9, 5, 5, 5, 5, 5, 5, 5, 5, 0, 9);
        BodySystems bodySystems = new BodySystems(BloodSystem.defaults(), CardiacSystem.defaults(),
                PulmonarySystem.defaults(), gifted, HormonalGlandularSystem.defaults(), DigestiveSystem.defaults());
        PlayableCharacter character = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), bodySystems, PhysicalTraits.defaults()));
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());

        assertThat(character.getPsyquismOutput()).isGreaterThan(defaults.getPsyquismOutput());
        assertThat(character.getPsyquismDefense()).isGreaterThan(defaults.getPsyquismDefense());
    }

    @Test
    void getCharmResistance_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getCharmResistance()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getCharmResistance_higherVanity_decreasesResistance() {
        Mind vainMind = Mind.previewTemplate(Values.defaults(), Erudition.defaults(), Personality.defaults(),
                Labours.defaults(), new GeneralPersonality(9, 5), WeaponProficiencies.defaults());
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), vainMind);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getCharmResistance()).isLessThan(defaults.getCharmResistance());
    }

    @Test
    void getCharmResistance_protagonistTrait_furtherDecreasesResistance() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Values values = Values.defaults();
        values.setEgo(4);
        PlayableCharacter protagonist = withTrait(values, Trait.PROTAGONIST);

        assertThat(protagonist.getCharmResistance()).isLessThan(defaults.getCharmResistance());
    }

    @Test
    void getConcentration_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getConcentration()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getConcentration_higherFocus_increasesConcentration_higherCerebralCapacity_decreasesIt() {
        Mind focusedMind = Mind.previewTemplate(Values.defaults(), Erudition.defaults(), Personality.defaults(),
                Labours.defaults(), new GeneralPersonality(5, 9), WeaponProficiencies.defaults());
        PlayableCharacter focused = new PlayableCharacter("test", Body.humanTemplate(), focusedMind);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(focused.getConcentration()).isGreaterThan(defaults.getConcentration());

        Body sharpBody = Body.humanTemplate();
        sharpBody.getBodySystems().getNeuralSystem().setCerebralCapacity(9);
        PlayableCharacter sharp = new PlayableCharacter("test", sharpBody, Mind.humanTemplate());

        assertThat(sharp.getConcentration()).isLessThan(defaults.getConcentration());
    }

    @Test
    void getPurity_onHumanDefaults_equalsBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getPurity()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getPurity_cleanVesselTrait_increasesPurity() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Values values = Values.defaults();
        values.setDivinity(4);
        PlayableCharacter cleanVessel = withTrait(values, Trait.CLEAN_VESSEL);

        assertThat(cleanVessel.getPurity()).isGreaterThan(defaults.getPurity());
    }

    // -------------------------------------------------------------------------
    // Vanity modifiers on Enfactuation/Intimidation
    // -------------------------------------------------------------------------

    @Test
    void getEnfactuation_higherVanity_increasesEnfactuation() {
        Mind vainMind = Mind.previewTemplate(Values.defaults(), Erudition.defaults(), Personality.defaults(),
                Labours.defaults(), new GeneralPersonality(9, 5), WeaponProficiencies.defaults());
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), vainMind);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getEnfactuation()).isGreaterThan(defaults.getEnfactuation());
    }

    @Test
    void getIntimidation_higherVanity_decreasesIntimidation() {
        Mind vainMind = Mind.previewTemplate(Values.defaults(), Erudition.defaults(), Personality.defaults(),
                Labours.defaults(), new GeneralPersonality(9, 5), WeaponProficiencies.defaults());
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), vainMind);
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getIntimidation()).isLessThan(defaults.getIntimidation());
    }

    // -------------------------------------------------------------------------
    // 12 new concern-threshold Values-linked traits — standalone, gated by a concern
    // sitting at/above a threshold rather than the base/advanced pair's exact-default check.
    // -------------------------------------------------------------------------

    @Test
    void protagonistAndEgotist_prerequisitesGatedBySelfConcernThreshold() {
        Values lowInvestment = Values.defaults();
        lowInvestment.setEgo(2);
        PlayableCharacter lowCharacter = characterWithMindValues(lowInvestment);
        assertThat(Trait.EGOTIST.prerequisitesMet(lowCharacter)).isTrue();
        assertThat(Trait.PROTAGONIST.prerequisitesMet(lowCharacter)).isFalse();

        Values highInvestment = Values.defaults();
        highInvestment.setEgo(4);
        PlayableCharacter highCharacter = characterWithMindValues(highInvestment);
        assertThat(Trait.PROTAGONIST.prerequisitesMet(highCharacter)).isTrue();
        assertThat(Trait.EGOTIST.prerequisitesMet(highCharacter)).isTrue();
    }

    @Test
    void reliableTrait_increasesEnfactuation() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Values values = Values.defaults();
        values.setLoyalty(4);
        PlayableCharacter reliable = withTrait(values, Trait.RELIABLE);

        assertThat(reliable.getEnfactuation()).isGreaterThan(defaults.getEnfactuation());
    }

    @Test
    void realiticTrait_increasesIllusionResistanceAndDecreasesBluffing() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Values values = Values.defaults();
        values.setTruth(4);
        PlayableCharacter realitic = withTrait(values, Trait.REALITIC);

        assertThat(realitic.getIllusionResistance()).isGreaterThan(defaults.getIllusionResistance());
        assertThat(realitic.getBluffing()).isLessThan(defaults.getBluffing());
    }

    @Test
    void philosopherTrait_increasesReasoning() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Values values = Values.defaults();
        values.setTruth(2);
        PlayableCharacter philosopher = withTrait(values, Trait.PHILOSOPHER);

        assertThat(philosopher.getReasoning()).isGreaterThan(defaults.getReasoning());
    }

    @Test
    void outdoorLifestyleTrait_increasesSurvivalSkillsAndAnimalCaring() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Values values = Values.defaults();
        values.setNature(4);
        PlayableCharacter outdoorsy = withTrait(values, Trait.OUTDOOR_LIFESTYLE);

        assertThat(outdoorsy.getSurvivalSkills()).isGreaterThan(defaults.getSurvivalSkills());
        assertThat(outdoorsy.getAnimalCaring()).isGreaterThan(defaults.getAnimalCaring());
    }

    @Test
    void retributionSeekerTrait_isGatedByJusticeConcern_notEnvironmentalismConcern() {
        Values values = Values.defaults();
        values.setJustice(4);
        PlayableCharacter character = characterWithMindValues(values);

        assertThat(Trait.RETRIBUTION_SEEKER.prerequisitesMet(character)).isTrue();
    }

    @Test
    void inventorTrait_increasesCreativity() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Values values = Values.defaults();
        values.setProgress(2);
        PlayableCharacter inventor = withTrait(values, Trait.INVENTOR);

        assertThat(inventor.getCreativity()).isGreaterThan(defaults.getCreativity());
    }

    @Test
    void religionPractitionerTrait_increasesFaith() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Values values = Values.defaults();
        values.setDivinity(2);
        PlayableCharacter devout = withTrait(values, Trait.RELIGION_PRACTITIONER);

        assertThat(devout.getFaith()).isGreaterThan(defaults.getFaith());
    }

    @Test
    void peacekeeperTrait_decreasesIntimidationAndIncreasesEnfactuation() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Values values = Values.defaults();
        values.setPeace(4);
        PlayableCharacter peacekeeper = withTrait(values, Trait.PEACEKEEPER);

        assertThat(peacekeeper.getIntimidation()).isLessThan(defaults.getIntimidation());
        assertThat(peacekeeper.getEnfactuation()).isGreaterThan(defaults.getEnfactuation());
    }

    @Test
    void loyalistAndEgotistAndRetributionSeeker_grantNoFormulaTerms_entirelySituational() {
        // These three traits have no unconditional numeric effect — only narrative/situational
        // text in getDescription(). Selecting Loyalist must not force any Values field or move
        // any attribute that isn't a direct mirror of the Society value it was gated on.
        Values values = Values.defaults();
        values.setSociety(4);
        PlayableCharacter loyalist = withTrait(values, Trait.LOYALIST);
        PlayableCharacter unaffectedSameSociety = characterWithMindValues(values);

        assertThat(loyalist.getMind().getValues().getSociety()).isEqualTo(4);
        assertThat(loyalist.getCommand()).isEqualTo(unaffectedSameSociety.getCommand());
        assertThat(loyalist.getEnfactuation()).isEqualTo(unaffectedSameSociety.getEnfactuation());
    }

    private static PlayableCharacter characterWithMindValues(Values values) {
        return new PlayableCharacter("test", Body.humanTemplate(),
                Mind.previewTemplate(values, Erudition.defaults(), Personality.defaults(), Labours.defaults(),
                        GeneralPersonality.defaults(), WeaponProficiencies.defaults()));
    }

    // -------------------------------------------------------------------------
    // Training and Conditioning (rpg-21) — Intensity/Coordination/Resilience/Fighting/
    // WeaponPracticing/Shooting join Vigor/Reflexes, same raw-value shape
    // -------------------------------------------------------------------------

    @Test
    void intensity_increasesMeanStrengthDrivenAttributesAndSpeed() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());
        PlayableCharacter trained = characterWithTrainingAndConditioning(t -> t.setIntensity(8));

        assertThat(trained.getPushStrength()).isGreaterThan(defaults.getPushStrength());
        assertThat(trained.getSpeed()).isGreaterThan(defaults.getSpeed());
    }

    @Test
    void coordination_increasesAcrobaticsEvasionAndBalance() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());
        PlayableCharacter trained = characterWithTrainingAndConditioning(t -> t.setCoordination(8));

        assertThat(trained.getAcrobatics()).isGreaterThan(defaults.getAcrobatics());
        assertThat(trained.getEvasion()).isGreaterThan(defaults.getEvasion());
        assertThat(trained.getBalance()).isGreaterThan(defaults.getBalance());
    }

    @Test
    void resilience_increasesPainThresholdAndSoftTissueDurability() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());
        PlayableCharacter trained = characterWithTrainingAndConditioning(t -> t.setResilience(8));

        assertThat(trained.getPainThreshold()).isGreaterThan(defaults.getPainThreshold());
        assertThat(trained.getSoftTissueDurability()).isGreaterThan(defaults.getSoftTissueDurability());
    }

    @Test
    void fighting_increasesCloseCombat() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());
        PlayableCharacter trained = characterWithTrainingAndConditioning(t -> t.setFighting(5));

        assertThat(trained.getCloseCombat()).isGreaterThan(defaults.getCloseCombat());
    }

    @Test
    void weaponPracticing_increasesLowRangeCombat() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());
        PlayableCharacter trained = characterWithTrainingAndConditioning(t -> t.setWeaponPracticing(5));

        assertThat(trained.getLowRangeCombat()).isGreaterThan(defaults.getLowRangeCombat());
    }

    @Test
    void shooting_increasesLongRangeCombatAndAim() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate());
        PlayableCharacter trained = characterWithTrainingAndConditioning(t -> t.setShooting(5));

        assertThat(trained.getLongRangeCombat()).isGreaterThan(defaults.getLongRangeCombat());
        assertThat(trained.getAim()).isGreaterThan(defaults.getAim());
    }

    // -------------------------------------------------------------------------
    // Athletism and Martial Arts (rpg-21) — Dancing/Fencing new Knowledge constants; Archery
    // wired to a real formula effect for the first time
    // -------------------------------------------------------------------------

    @Test
    void dancing_increasesAcrobaticsEvasionAndBalance() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter dancer = characterWithErudition(Knowledge.DANCING, 2);

        assertThat(dancer.getAcrobatics()).isGreaterThan(defaults.getAcrobatics());
        assertThat(dancer.getEvasion()).isGreaterThan(defaults.getEvasion());
        assertThat(dancer.getBalance()).isGreaterThan(defaults.getBalance());
    }

    @Test
    void fencing_increasesLowRangeCombat() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter fencer = characterWithErudition(Knowledge.FENCING, 2);

        assertThat(fencer.getLowRangeCombat()).isGreaterThan(defaults.getLowRangeCombat());
    }

    @Test
    void archery_increasesLongRangeCombatAndAim() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter archer = characterWithErudition(Knowledge.ARCHERY, 2);

        assertThat(archer.getLongRangeCombat()).isGreaterThan(defaults.getLongRangeCombat());
        assertThat(archer.getAim()).isGreaterThan(defaults.getAim());
    }

    // -------------------------------------------------------------------------
    // Skills (rpg-21) — new Knowledge-level-driven craft/practice attributes
    // -------------------------------------------------------------------------

    @Test
    void newSkillsAttributes_onHumanDefaults_allEqualBaseline() {
        PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());

        assertThat(character.getAlchemy()).isCloseTo(60.0, within(TOLERANCE));
        assertThat(character.getMachineHandling()).isCloseTo(60.0, within(TOLERANCE));
        assertThat(character.getPerformance()).isCloseTo(60.0, within(TOLERANCE));
        assertThat(character.getSciencePractice()).isCloseTo(60.0, within(TOLERANCE));
        assertThat(character.getHealing()).isCloseTo(60.0, within(TOLERANCE));
        assertThat(character.getHackingAndPrograming()).isCloseTo(60.0, within(TOLERANCE));
    }

    @Test
    void getAlchemy_withChemistryAndWizardryLevels_increasesAlchemy() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter alchemist = characterWithErudition(Knowledge.CHEMISTRY, 2, Knowledge.WIZARDRY, 2);

        assertThat(alchemist.getAlchemy()).isGreaterThan(defaults.getAlchemy());
    }

    @Test
    void getMachineHandling_withEngineeringLevel_increasesMachineHandling() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter engineer = characterWithErudition(Knowledge.ENGINEERING, 2);

        assertThat(engineer.getMachineHandling()).isGreaterThan(defaults.getMachineHandling());
    }

    @Test
    void getPerformance_withCoordinationDancingAndArt_increasesPerformance() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        Mind mind = Mind.previewTemplate(Values.defaults(),
                new Erudition(java.util.Map.of(Knowledge.DANCING, 2, Knowledge.ART, 2)), Personality.defaults(),
                Labours.defaults(), GeneralPersonality.defaults(), WeaponProficiencies.defaults());
        PhysicalTraits trained = new PhysicalTraits(SensorialOrgans.defaults(), BodyStructure.defaults(),
                new TrainingAndConditioning(0, 0, 0, 8, 0, 0, 0, 0));
        PlayableCharacter performer = new PlayableCharacter("test",
                Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), trained), mind);

        assertThat(performer.getPerformance()).isGreaterThan(defaults.getPerformance());
    }

    @Test
    void getSciencePractice_withBiologyAndChemistryLevels_increasesSciencePractice() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter scientist = characterWithErudition(Knowledge.BIOLOGY, 2, Knowledge.CHEMISTRY, 2);

        assertThat(scientist.getSciencePractice()).isGreaterThan(defaults.getSciencePractice());
    }

    @Test
    void getHealing_withMedicineAndBiologyLevels_increasesHealing() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter healer = characterWithErudition(Knowledge.MEDICINE, 2, Knowledge.BIOLOGY, 2);

        assertThat(healer.getHealing()).isGreaterThan(defaults.getHealing());
    }

    @Test
    void getHackingAndPrograming_withComputerScienceLevel_increasesHackingAndPrograming() {
        PlayableCharacter defaults = new PlayableCharacter("test", Body.humanTemplate(), Mind.humanTemplate());
        PlayableCharacter hacker = characterWithErudition(Knowledge.COMPUTER_SCIENCE, 2);

        assertThat(hacker.getHackingAndPrograming()).isGreaterThan(defaults.getHackingAndPrograming());
    }

    private static PlayableCharacter characterWithTrainingAndConditioning(
            java.util.function.Consumer<TrainingAndConditioning> customize) {
        TrainingAndConditioning trainingAndConditioning = TrainingAndConditioning.defaults();
        customize.accept(trainingAndConditioning);
        PhysicalTraits physicalTraits = new PhysicalTraits(SensorialOrgans.defaults(), BodyStructure.defaults(),
                trainingAndConditioning);
        Body body = Body.previewTemplate(Biomechanics.defaults(), BodySystems.defaults(), physicalTraits);
        return new PlayableCharacter("test", body);
    }

    private static PlayableCharacter characterWithErudition(Knowledge knowledge, int level) {
        Mind mind = Mind.previewTemplate(Values.defaults(), new Erudition(java.util.Map.of(knowledge, level)),
                Personality.defaults(), Labours.defaults(), GeneralPersonality.defaults(),
                WeaponProficiencies.defaults());
        return new PlayableCharacter("test", Body.humanTemplate(), mind);
    }

    private static PlayableCharacter characterWithErudition(Knowledge first, int firstLevel, Knowledge second,
                                                              int secondLevel) {
        Mind mind = Mind.previewTemplate(Values.defaults(),
                new Erudition(java.util.Map.of(first, firstLevel, second, secondLevel)), Personality.defaults(),
                Labours.defaults(), GeneralPersonality.defaults(), WeaponProficiencies.defaults());
        return new PlayableCharacter("test", Body.humanTemplate(), mind);
    }
}
