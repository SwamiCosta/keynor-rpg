package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BodyCoefficientsTest {

    @Test
    void defaults_baselineIsSixty() {
        assertThat(BodyCoefficients.defaults().getBaseline()).isEqualTo(60);
    }

    @Test
    void defaults_meanStrengthWeightsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKMeanStrengthMuscleMass()).isEqualTo(4);
        assertThat(coeff.getKMeanStrengthNeuromuscular()).isEqualTo(2);
        assertThat(coeff.getKMeanStrengthFiberType()).isEqualTo(1);
    }

    @Test
    void defaults_specializedStrengthWeightsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKPushStrengthLimbRatio()).isEqualTo(2);
        assertThat(coeff.getKPushStrengthMuscleDistribution()).isEqualTo(1);
        assertThat(coeff.getKPushStrengthTendons()).isEqualTo(1);
        assertThat(coeff.getKPushStrengthHeight()).isEqualTo(0.5);
        assertThat(coeff.getKLegDriveLimbRatio()).isEqualTo(2);
        assertThat(coeff.getKLegDriveMuscleDistribution()).isEqualTo(1);
        assertThat(coeff.getKLegDriveTendons()).isEqualTo(1);
        assertThat(coeff.getKLegDriveHeight()).isEqualTo(0.5);
        assertThat(coeff.getKGripStrengthMuscleDistribution()).isEqualTo(1);
        assertThat(coeff.getKGripStrengthTendons()).isEqualTo(2);
        assertThat(coeff.getKLiftStrengthLimbRatio()).isEqualTo(2);
        assertThat(coeff.getKLiftStrengthTendons()).isEqualTo(1);
    }

    @Test
    void defaults_speedWeightsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKSpeedNeuromuscular()).isEqualTo(4);
        assertThat(coeff.getKSpeedMuscleMass()).isEqualTo(1);
        assertThat(coeff.getKSpeedFiberType()).isEqualTo(2);
        assertThat(coeff.getKSpeedMassNeutral()).isEqualTo(25);
        assertThat(coeff.getKSpeedMassDivisor()).isEqualTo(3);
        assertThat(coeff.getKMovementSpeedHeight()).isEqualTo(0.5);
    }

    @Test
    void defaults_fatigueResistanceWeightsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKFatigueResistanceCardiac()).isEqualTo(3);
        assertThat(coeff.getKFatigueResistancePulmonary()).isEqualTo(1);
        assertThat(coeff.getKFatigueResistanceOxygen()).isEqualTo(1);
        assertThat(coeff.getKFatigueResistanceNeuromuscular()).isEqualTo(2);
        assertThat(coeff.getKFatigueResistanceMassNeutral()).isEqualTo(25);
        assertThat(coeff.getKFatigueResistanceMassDivisor()).isEqualTo(2);
        assertThat(coeff.getKFatigueResistanceMuscleMass()).isEqualTo(1);
    }

    @Test
    void defaults_loadCapacityCoefficientsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKMaxCapacityDivisor()).isEqualTo(150);
        assertThat(coeff.getKLightLoadDivisor()).isEqualTo(3);
        assertThat(coeff.getKHeavyLoadMultiplier()).isEqualTo(2);
        assertThat(coeff.getKHeavyLoadDivisor()).isEqualTo(3);
        assertThat(coeff.getKDragCapacityMultiplier()).isEqualTo(2);
        assertThat(coeff.getKDragCapacityMassFraction()).isEqualTo(0.5);
    }

    @Test
    void defaults_rpg13WeightsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKReasoningSynapsis()).isEqualTo(10);
        assertThat(coeff.getKMentalHealthAmygdala()).isEqualTo(5);
        assertThat(coeff.getKStressResistanceAmygdala()).isEqualTo(5);
        assertThat(coeff.getKStressResistanceAdrenal()).isEqualTo(5);
        assertThat(coeff.getKPoisonResistanceImmunity()).isEqualTo(5);
        assertThat(coeff.getKPoisonResistanceCardiac()).isEqualTo(3);
        assertThat(coeff.getKPoisonResistanceBloodThickness()).isEqualTo(4);
        assertThat(coeff.getKDiseaseResistanceImmunity()).isEqualTo(9);
        assertThat(coeff.getKDiseaseResistanceAmygdala()).isEqualTo(1);
        assertThat(coeff.getKBleedingResistanceBloodThickness()).isEqualTo(10);
        assertThat(coeff.getKBleedingResistanceCardiac()).isEqualTo(5);
        assertThat(coeff.getKThermalResistanceSkin()).isEqualTo(5);
        assertThat(coeff.getKThermalResistanceBodyFat()).isEqualTo(2);
        assertThat(coeff.getKThermalResistanceHypothalamus()).isEqualTo(1);
        assertThat(coeff.getKBreathOutputPulmonary()).isEqualTo(10);
        assertThat(coeff.getKDehydrationResistanceHypothalamus()).isEqualTo(5);
        assertThat(coeff.getKDehydrationResistanceKetosis()).isEqualTo(5);
        assertThat(coeff.getKStarvationResistanceHypothalamus()).isEqualTo(4);
        assertThat(coeff.getKStarvationResistanceKetosis()).isEqualTo(3);
        assertThat(coeff.getKFoodPoisoningImpurity()).isEqualTo(5);
        assertThat(coeff.getKFoodPoisoningImmunity()).isEqualTo(5);
        assertThat(coeff.getKFatigueResistanceHypothalamus()).isEqualTo(1);
        assertThat(coeff.getKFatigueResistanceThyroid()).isEqualTo(2);
    }

    @Test
    void defaults_rpg14WeightsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKSoftTissueDurabilitySkin()).isEqualTo(1);
        assertThat(coeff.getKSightEyesSensitivity()).isEqualTo(6);
        assertThat(coeff.getKSightNeuralDrive()).isEqualTo(1);
        assertThat(coeff.getKSightPmod()).isEqualTo(2);
        assertThat(coeff.getKHearingEarsSensitivity()).isEqualTo(6);
        assertThat(coeff.getKSmellNoseSensitivity()).isEqualTo(6);
        assertThat(coeff.getKMentalHealthTmod()).isEqualTo(5);
        assertThat(coeff.getKMentalHealthPmod()).isEqualTo(5);
        assertThat(coeff.getKPoisonResistanceCellularHealth()).isEqualTo(2);
        assertThat(coeff.getKDiseaseResistanceCellularHealth()).isEqualTo(2);
        assertThat(coeff.getKFoodPoisoningCellularHealth()).isEqualTo(2);
        assertThat(coeff.getKFatGainRateEndomorphy()).isEqualTo(1.0);
        assertThat(coeff.getKFatGainRateCellularHealth()).isEqualTo(0.5);
        assertThat(coeff.getKMuscleGainRateTmod()).isEqualTo(1.0);
        assertThat(coeff.getKIntimidationShapeAesthetics()).isEqualTo(5);
        assertThat(coeff.getKIntimidationMassNeutral()).isEqualTo(25);
        assertThat(coeff.getKDiplomacyShapeAesthetics()).isEqualTo(7);
        assertThat(coeff.getKEnfactuationShapeAesthetics()).isEqualTo(7);
        assertThat(coeff.getKCommandShapeAesthetics()).isEqualTo(10);
    }

    @Test
    void defaults_deltaV4WeightsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        // Sight/Hearing/Smell: Hippocampus swapped for Thalamus, same weights
        assertThat(coeff.getKSightThalamus()).isEqualTo(1);
        assertThat(coeff.getKHearingThalamus()).isEqualTo(1);
        assertThat(coeff.getKSmellThalamus()).isEqualTo(1);

        // Memory reweighted
        assertThat(coeff.getKMemoryPoolCerebral()).isEqualTo(6);
        assertThat(coeff.getKMemoryPoolHippocampus()).isEqualTo(4);
        assertThat(coeff.getKShortMemoryCerebral()).isEqualTo(3);
        assertThat(coeff.getKShortMemorySynapsis()).isEqualTo(3);
        assertThat(coeff.getKShortMemoryHippocampus()).isEqualTo(4);

        // Balance rebuilt: Thalamus + NeuralDrive (kept) + LegDrive term, Tendons/Hippocampus gone
        assertThat(coeff.getKBalanceThalamus()).isEqualTo(4);
        assertThat(coeff.getKBalanceNeuralDrive()).isEqualTo(1);
        assertThat(coeff.getKBalanceLegDrive()).isEqualTo(0.2);

        // Aim: Precision reweighted, Hippocampus swapped for Thalamus and reweighted
        assertThat(coeff.getKAimPrecision()).isEqualTo(5);
        assertThat(coeff.getKAimThalamus()).isEqualTo(3);

        // New resistance/threshold attributes
        assertThat(coeff.getKAngerResistanceAmygdala()).isEqualTo(10);
        assertThat(coeff.getKFearResistanceAmygdala()).isEqualTo(10);
        assertThat(coeff.getKPainThresholdBodyFat()).isEqualTo(3);
        assertThat(coeff.getKPainThresholdSkin()).isEqualTo(3);
        assertThat(coeff.getKPainThresholdAmygdala()).isEqualTo(4);

        // DigestiveAbsorption renames (value unchanged from the old NutrientAbsorption fields)
        assertThat(coeff.getKStaminaPoolDigestiveAbsorption()).isEqualTo(2);
        assertThat(coeff.getKStarvationResistanceDigestiveAbsorption()).isEqualTo(3);
        assertThat(coeff.getKFatGainRateDigestiveAbsorption()).isEqualTo(1.0);
        assertThat(coeff.getKMuscleGainRateDigestiveAbsorption()).isEqualTo(1.0);
        assertThat(coeff.getKFoodPoisoningDigestiveAbsorption()).isEqualTo(1);
    }

    @Test
    void defaults_attributeFloorIsFive() {
        assertThat(BodyCoefficients.defaults().getAttributeFloor()).isEqualTo(5);
    }

    @Test
    void defaults_mediunityRenamedFromSixthSense_keepsTheSameWeight() {
        assertThat(BodyCoefficients.defaults().getKMediunityNoeticPlexus()).isEqualTo(8);
    }

    @Test
    void defaults_mindPillarWeightsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKSurvivalSkillsEcology()).isEqualTo(2);
        assertThat(coeff.getKAnimalCaringEcology()).isEqualTo(2);
        assertThat(coeff.getKAnimalCaringBiology()).isEqualTo(2);
    }

    @Test
    void defaults_valuesTraitWeightsMatchTheDesignDocument_rpg19() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKFearResistanceSelfSacrifice()).isEqualTo(4);
        assertThat(coeff.getKPainThresholdSelfSacrifice()).isEqualTo(8);
        assertThat(coeff.getKDiscretionLoneWolf()).isEqualTo(8);
        assertThat(coeff.getKDiscretionBackstabber()).isEqualTo(8);
        assertThat(coeff.getKCommandDominant()).isEqualTo(4);
        assertThat(coeff.getKManipulationRelativist()).isEqualTo(4);
        assertThat(coeff.getKSurvivalSkillsExpatriated()).isEqualTo(10);
        assertThat(coeff.getKMediunityPagan()).isEqualTo(5);
        assertThat(coeff.getKFaithPagan()).isEqualTo(10);
        assertThat(coeff.getKWillNihilist()).isEqualTo(10);
        assertThat(coeff.getKMentalHealthNihilist()).isEqualTo(15);
        assertThat(coeff.getKMemoryPoolIliterate()).isEqualTo(20);
        assertThat(coeff.getKAnalysisReasoning()).isEqualTo(0.5);
        assertThat(coeff.getKAnalysisDogEatDog()).isEqualTo(5);
        assertThat(coeff.getKCloseCombatBellicose()).isEqualTo(4);
        assertThat(coeff.getKLowRangeCombatBellicose()).isEqualTo(4);
    }

    @Test
    void setters_allowRebalancingBaselineAndWeightsIndependently() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        coeff.setBaseline(70);
        coeff.setKPushStrengthLimbRatio(5);
        coeff.setAttributeFloor(10);

        assertThat(coeff.getBaseline()).isEqualTo(70);
        assertThat(coeff.getKPushStrengthLimbRatio()).isEqualTo(5);
        assertThat(coeff.getAttributeFloor()).isEqualTo(10);
        assertThat(coeff.getKPushStrengthMuscleDistribution()).isEqualTo(1);
    }
}
