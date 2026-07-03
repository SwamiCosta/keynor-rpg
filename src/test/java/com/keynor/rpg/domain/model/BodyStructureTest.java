package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BodyStructureTest {

    @Test
    void defaults_returnsNeutralSkinThicknessAndMidRangeShapeAndCellularHealth() {
        BodyStructure bodyStructure = BodyStructure.defaults();

        assertThat(bodyStructure.getSkinThickness()).isEqualTo(3);
        assertThat(bodyStructure.getShapeAesthetics()).isEqualTo(5);
        assertThat(bodyStructure.getCellularHealth()).isEqualTo(5);
    }

    @Test
    void setters_mutateOnlyTheTrainableFields() {
        BodyStructure bodyStructure = BodyStructure.defaults();

        bodyStructure.setShapeAesthetics(9);
        bodyStructure.setCellularHealth(1);

        assertThat(bodyStructure.getShapeAesthetics()).isEqualTo(9);
        assertThat(bodyStructure.getCellularHealth()).isEqualTo(1);
    }

    @Test
    void constructor_acceptsTheFullOneToSevenSkinThicknessRangeForFutureNonHumanRaces() {
        BodyStructure extremeThin = new BodyStructure(1, 5, 5);
        BodyStructure extremeThick = new BodyStructure(7, 5, 5);

        assertThat(extremeThin.getSkinThickness()).isEqualTo(1);
        assertThat(extremeThick.getSkinThickness()).isEqualTo(7);
    }
}
