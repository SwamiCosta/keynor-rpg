package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BodyCoefficientsTest {

    @Test
    void defaults_baselineIsSixty() {
        assertThat(BodyCoefficients.defaults().getBaseline()).isEqualTo(60);
    }

    @Test
    void defaults_strengthWeightsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKStrengthMuscleMass()).isEqualTo(4);
        assertThat(coeff.getKStrengthNeuromuscular()).isEqualTo(2);
        assertThat(coeff.getKStrengthFiberType()).isEqualTo(1);
        assertThat(coeff.getKStrengthLimbRatio()).isEqualTo(2);
        assertThat(coeff.getKStrengthMuscleDistribution()).isEqualTo(1);
    }

    @Test
    void defaults_speedWeightsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKSpeedNeuromuscular()).isEqualTo(4);
        assertThat(coeff.getKSpeedMuscleMass()).isEqualTo(1);
        assertThat(coeff.getKSpeedFiberType()).isEqualTo(2);
        assertThat(coeff.getKSpeedMassNeutral()).isEqualTo(25);
        assertThat(coeff.getKSpeedMassDivisor()).isEqualTo(3);
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

        assertThat(coeff.getKMemoryPoolCerebral()).isEqualTo(8);
        assertThat(coeff.getKMemoryPoolHippocampus()).isEqualTo(2);
        assertThat(coeff.getKReasoningSynapsis()).isEqualTo(10);
        assertThat(coeff.getKShortMemoryCerebral()).isEqualTo(4);
        assertThat(coeff.getKShortMemorySynapsis()).isEqualTo(4);
        assertThat(coeff.getKShortMemoryHippocampus()).isEqualTo(2);
        assertThat(coeff.getKMentalHealthAmygdala()).isEqualTo(10);
        assertThat(coeff.getKBalanceHippocampus()).isEqualTo(3);
        assertThat(coeff.getKBalanceNeuralDrive()).isEqualTo(1);
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
        assertThat(coeff.getKStarvationResistanceNutrient()).isEqualTo(3);
        assertThat(coeff.getKStarvationResistanceKetosis()).isEqualTo(3);
        assertThat(coeff.getKFoodPoisoningImpurity()).isEqualTo(5);
        assertThat(coeff.getKFoodPoisoningImmunity()).isEqualTo(5);
        assertThat(coeff.getKStaminaPoolNutrientAbsorption()).isEqualTo(2);
        assertThat(coeff.getKFatigueResistanceHypothalamus()).isEqualTo(1);
        assertThat(coeff.getKFatigueResistanceThyroid()).isEqualTo(2);
    }

    @Test
    void defaults_attributeFloorIsFive() {
        assertThat(BodyCoefficients.defaults().getAttributeFloor()).isEqualTo(5);
    }

    @Test
    void setters_allowRebalancingBaselineAndWeightsIndependently() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        coeff.setBaseline(70);
        coeff.setKStrengthMuscleMass(5);
        coeff.setAttributeFloor(10);

        assertThat(coeff.getBaseline()).isEqualTo(70);
        assertThat(coeff.getKStrengthMuscleMass()).isEqualTo(5);
        assertThat(coeff.getAttributeFloor()).isEqualTo(10);
        assertThat(coeff.getKStrengthNeuromuscular()).isEqualTo(2);
    }
}
