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
    void defaults_loadCapacityFractionsMatchTheDesignDocument() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKLightLoadFraction()).isEqualTo(0.3);
        assertThat(coeff.getKHeavyLoadFraction()).isEqualTo(0.7);
        assertThat(coeff.getKMaxCapacityDivisor()).isEqualTo(25);
        assertThat(coeff.getKDragCapacityMultiplier()).isEqualTo(2);
        assertThat(coeff.getKDragCapacityMassFraction()).isEqualTo(0.5);
    }

    @Test
    void defaults_loadCapacityStrengthOffsetUndoesTheBaselineSixtyShift() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKLoadCapacityStrengthOffset()).isEqualTo(25);
        assertThat(coeff.getBaseline() - coeff.getKLoadCapacityStrengthOffset()).isEqualTo(35);
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
