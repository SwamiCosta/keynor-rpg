package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DigestiveSystemTest {

    @Test
    void defaults_returnsMidRangeForAllThreeFields() {
        DigestiveSystem digestiveSystem = DigestiveSystem.defaults();

        assertThat(digestiveSystem.getNutrientAbsorption()).isEqualTo(5);
        assertThat(digestiveSystem.getImpurityCleaning()).isEqualTo(5);
        assertThat(digestiveSystem.getKetosisQuality()).isEqualTo(5);
    }

    @Test
    void setters_mutateStateToModelTrainingProgress() {
        DigestiveSystem digestiveSystem = DigestiveSystem.defaults();

        digestiveSystem.setNutrientAbsorption(8);
        digestiveSystem.setImpurityCleaning(2);
        digestiveSystem.setKetosisQuality(9);

        assertThat(digestiveSystem.getNutrientAbsorption()).isEqualTo(8);
        assertThat(digestiveSystem.getImpurityCleaning()).isEqualTo(2);
        assertThat(digestiveSystem.getKetosisQuality()).isEqualTo(9);
    }
}
