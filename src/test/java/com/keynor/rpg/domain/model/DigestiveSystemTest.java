package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DigestiveSystemTest {

    @Test
    void defaults_returnsMidRangeForAllThreeFields() {
        DigestiveSystem digestiveSystem = DigestiveSystem.defaults();

        assertThat(digestiveSystem.getDigestiveAbsorption()).isEqualTo(5);
        assertThat(digestiveSystem.getImpurityCleaning()).isEqualTo(5);
        assertThat(digestiveSystem.getKetosisEfficiency()).isEqualTo(5);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        DigestiveSystem digestiveSystem = DigestiveSystem.defaults();

        digestiveSystem.setDigestiveAbsorption(8);
        digestiveSystem.setImpurityCleaning(2);
        digestiveSystem.setKetosisEfficiency(9);

        assertThat(digestiveSystem.getDigestiveAbsorption()).isEqualTo(8);
        assertThat(digestiveSystem.getImpurityCleaning()).isEqualTo(2);
        assertThat(digestiveSystem.getKetosisEfficiency()).isEqualTo(9);
    }
}
