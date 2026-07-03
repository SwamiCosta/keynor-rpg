package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BodySystemsTest {

    @Test
    void defaults_wiresUpAllSixSystemsAtTheirOwnDefaults() {
        BodySystems bodySystems = BodySystems.defaults();

        assertThat(bodySystems.getBloodSystem()).isNotNull();
        assertThat(bodySystems.getCardiacSystem()).isNotNull();
        assertThat(bodySystems.getPulmonarySystem()).isNotNull();
        assertThat(bodySystems.getNeuralSystem()).isNotNull();
        assertThat(bodySystems.getHormonalSystem()).isNotNull();
        assertThat(bodySystems.getDigestiveSystem()).isNotNull();
        assertThat(bodySystems.getBloodSystem().getOxygenCarryingCapacity()).isEqualTo(5);
        assertThat(bodySystems.getCardiacSystem().getCardiacOutput()).isEqualTo(5);
        assertThat(bodySystems.getPulmonarySystem().getPulmonaryCapacity()).isEqualTo(5);
        assertThat(bodySystems.getNeuralSystem().getNeuralDrive()).isEqualTo(5);
        assertThat(bodySystems.getHormonalSystem().getThyroid()).isEqualTo(5);
        assertThat(bodySystems.getDigestiveSystem().getDigestiveAbsorption()).isEqualTo(5);
    }

    @Test
    void constructor_storesEachSystemAsProvided() {
        BloodSystem blood = new BloodSystem(7, 4);
        CardiacSystem cardiac = new CardiacSystem(8);
        PulmonarySystem pulmonary = new PulmonarySystem(6);
        NeuralSystem neural = new NeuralSystem(9, 8, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        HormonalSystem hormonal = new HormonalSystem(6, 7, 9);
        DigestiveSystem digestive = new DigestiveSystem(3, 4, 5);
        BodySystems bodySystems = new BodySystems(blood, cardiac, pulmonary, neural, hormonal, digestive);

        assertThat(bodySystems.getBloodSystem()).isSameAs(blood);
        assertThat(bodySystems.getCardiacSystem()).isSameAs(cardiac);
        assertThat(bodySystems.getPulmonarySystem()).isSameAs(pulmonary);
        assertThat(bodySystems.getNeuralSystem()).isSameAs(neural);
        assertThat(bodySystems.getHormonalSystem()).isSameAs(hormonal);
        assertThat(bodySystems.getDigestiveSystem()).isSameAs(digestive);
    }
}
