package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TrainingAndConditioningTest {

    @Test
    void defaults_bothFieldsAtZero() {
        TrainingAndConditioning trainingAndConditioning = TrainingAndConditioning.defaults();

        assertThat(trainingAndConditioning.getVigor()).isZero();
        assertThat(trainingAndConditioning.getReflexes()).isZero();
    }

    @Test
    void setVigor_mutatesStateToModelTrainingProgress() {
        TrainingAndConditioning trainingAndConditioning = TrainingAndConditioning.defaults();

        trainingAndConditioning.setVigor(6);

        assertThat(trainingAndConditioning.getVigor()).isEqualTo(6);
    }

    @Test
    void setReflexes_mutatesStateToModelTrainingProgress() {
        TrainingAndConditioning trainingAndConditioning = TrainingAndConditioning.defaults();

        trainingAndConditioning.setReflexes(8);

        assertThat(trainingAndConditioning.getReflexes()).isEqualTo(8);
    }
}
