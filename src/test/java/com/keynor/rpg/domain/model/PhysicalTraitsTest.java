package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PhysicalTraitsTest {

    @Test
    void defaults_wiresUpSensorialOrgansAndBodyStructureAtTheirOwnDefaults() {
        PhysicalTraits physicalTraits = PhysicalTraits.defaults();

        assertThat(physicalTraits.getSensorialOrgans()).isNotNull();
        assertThat(physicalTraits.getBodyStructure()).isNotNull();
        assertThat(physicalTraits.getSensorialOrgans().getEyesSensitivity()).isEqualTo(5);
        assertThat(physicalTraits.getBodyStructure().getSkinThickness()).isEqualTo(3);
    }

    @Test
    void constructor_storesEachGroupAsProvided() {
        SensorialOrgans sensorialOrgans = new SensorialOrgans(6, 7, 8);
        BodyStructure bodyStructure = new BodyStructure(4, 9, 2);
        PhysicalTraits physicalTraits = new PhysicalTraits(sensorialOrgans, bodyStructure);

        assertThat(physicalTraits.getSensorialOrgans()).isSameAs(sensorialOrgans);
        assertThat(physicalTraits.getBodyStructure()).isSameAs(bodyStructure);
    }
}
