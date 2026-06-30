package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BodyCoefficientsTest {

    @Test
    void defaults_mainFormulaCoefficientsAreAllNeutralMultipliers() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getK1()).isEqualTo(1);
        assertThat(coeff.getC()).isEqualTo(1);
        assertThat(coeff.getK2()).isEqualTo(1);
        assertThat(coeff.getK3()).isEqualTo(1);
        assertThat(coeff.getK4()).isEqualTo(1);
        assertThat(coeff.getK5()).isEqualTo(1);
        assertThat(coeff.getK6()).isEqualTo(1);
        assertThat(coeff.getKBmr()).isEqualTo(1);
        assertThat(coeff.getKActivityCost()).isEqualTo(1);
        assertThat(coeff.getKEfficiency()).isEqualTo(1);
        assertThat(coeff.getK7()).isEqualTo(1);
        assertThat(coeff.getK8()).isEqualTo(1);
        assertThat(coeff.getK9()).isEqualTo(1);
        assertThat(coeff.getKFlexibilityDurability()).isEqualTo(1);
        assertThat(coeff.getKSense()).isEqualTo(1);
        assertThat(coeff.getKEvasion()).isEqualTo(1);
        assertThat(coeff.getKAcrobatics()).isEqualTo(1);
        assertThat(coeff.getKMelee()).isEqualTo(1);
        assertThat(coeff.getKAim()).isEqualTo(1);
    }

    @Test
    void defaults_massCoefficientsMatchTheHumanDefaultReconciliation() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKBoneMass()).isEqualTo(2.7);
        assertThat(coeff.getKBoneDensity()).isEqualTo(0.06);
        assertThat(coeff.getKOrganWaterMass()).isEqualTo(6.3);
    }

    @Test
    void defaults_muscleDistributionCoefficientsAreSmallAndSpeedIsLargerThanStrength() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKMuscleDistributionStrength()).isEqualTo(0.02);
        assertThat(coeff.getKMuscleDistributionSpeed()).isEqualTo(0.04);
        assertThat(coeff.getKMuscleDistributionSpeed()).isGreaterThan(coeff.getKMuscleDistributionStrength());
    }

    @Test
    void defaults_evasionModifiersAreSmallToAvoidOverwhelming11xMultipliers() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        assertThat(coeff.getKEvasionNeural()).isEqualTo(0.1);
        assertThat(coeff.getKEvasionFlex()).isEqualTo(0.1);
    }

    @Test
    void setters_allowRebalancingEachCoefficientIndependently() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        coeff.setK1(2.5);
        coeff.setC(0.5);

        assertThat(coeff.getK1()).isEqualTo(2.5);
        assertThat(coeff.getC()).isEqualTo(0.5);
        assertThat(coeff.getK2()).isEqualTo(1);
    }

    @Test
    void setters_allowRebalancingMassCoefficientsIndependently() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        coeff.setKBoneMass(3.0);
        coeff.setKBoneDensity(0.1);
        coeff.setKOrganWaterMass(7.0);

        assertThat(coeff.getKBoneMass()).isEqualTo(3.0);
        assertThat(coeff.getKBoneDensity()).isEqualTo(0.1);
        assertThat(coeff.getKOrganWaterMass()).isEqualTo(7.0);
    }

    @Test
    void setters_allowRebalancingMuscleDistributionAndSpatialCoefficients() {
        BodyCoefficients coeff = BodyCoefficients.defaults();

        coeff.setKMuscleDistributionStrength(0.1);
        coeff.setKMuscleDistributionSpeed(0.2);
        coeff.setKSense(1.5);
        coeff.setKEvasion(0.8);
        coeff.setKAcrobatics(1.2);

        assertThat(coeff.getKMuscleDistributionStrength()).isEqualTo(0.1);
        assertThat(coeff.getKMuscleDistributionSpeed()).isEqualTo(0.2);
        assertThat(coeff.getKSense()).isEqualTo(1.5);
        assertThat(coeff.getKEvasion()).isEqualTo(0.8);
        assertThat(coeff.getKAcrobatics()).isEqualTo(1.2);
    }
}
