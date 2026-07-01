package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BodySystemsTest {

    @Test
    void defaults_wiresUpAllFourSystemsAtTheirOwnDefaults() {
        BodySystems bodySystems = BodySystems.defaults();

        assertThat(bodySystems.getBloodSystem()).isNotNull();
        assertThat(bodySystems.getCardiacSystem()).isNotNull();
        assertThat(bodySystems.getPulmonarySystem()).isNotNull();
        assertThat(bodySystems.getNervousSystem()).isNotNull();
        assertThat(bodySystems.getBloodSystem().getOxygenCarryingCapacity()).isEqualTo(5);
        assertThat(bodySystems.getCardiacSystem().getCardiacOutput()).isEqualTo(5);
        assertThat(bodySystems.getPulmonarySystem().getPulmonaryCapacity()).isEqualTo(5);
        assertThat(bodySystems.getNervousSystem().getNeuralDrive()).isEqualTo(5);
        assertThat(bodySystems.getNervousSystem().getNeuromuscularEfficiency()).isEqualTo(5);
    }

    @Test
    void constructor_storesEachSystemAsProvided() {
        BloodSystem blood = new BloodSystem(7);
        CardiacSystem cardiac = new CardiacSystem(8);
        PulmonarySystem pulmonary = new PulmonarySystem(6);
        NervousSystem nervous = new NervousSystem(9, 8);
        BodySystems bodySystems = new BodySystems(blood, cardiac, pulmonary, nervous);

        assertThat(bodySystems.getBloodSystem()).isSameAs(blood);
        assertThat(bodySystems.getCardiacSystem()).isSameAs(cardiac);
        assertThat(bodySystems.getPulmonarySystem()).isSameAs(pulmonary);
        assertThat(bodySystems.getNervousSystem()).isSameAs(nervous);
    }
}
