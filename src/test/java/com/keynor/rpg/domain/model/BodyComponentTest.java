package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BodyComponentTest {

    @Test
    void applyDamage_reducesCurrentHitPoints() {
        BodyComponent component = BodyComponent.structural("Torso", 30, 4, false, 6);

        component.applyDamage(10, false);

        assertThat(component.getCurrentHitPoints()).isEqualTo(20);
        assertThat(component.getReversibleDamage()).isEqualTo(10);
    }

    @Test
    void applyDamage_neverDropsBelowZero() {
        BodyComponent component = BodyComponent.structural("Torso", 30, 4, false, 6);

        component.applyDamage(50, false);

        assertThat(component.getCurrentHitPoints()).isZero();
    }

    @Test
    void irreversibleDamage_isTrackedSeparatelyFromCurrentHitPoints() {
        BodyComponent component = BodyComponent.structural("RightEye", 3, 1, false, 16);

        component.applyDamage(3, true);

        assertThat(component.getCurrentHitPoints()).isZero();
        assertThat(component.getIrreversibleDamage()).isEqualTo(3);
        assertThat(component.getReversibleDamage()).isZero();
    }

    @Test
    void addChild_setsParentReference() {
        BodyComponent skull = BodyComponent.structural("Skull", 20, 5, true, 8);
        BodyComponent brain = BodyComponent.protectedInternal("Brain", 10, 5, true, 18);

        skull.addChild(brain);

        assertThat(brain.getParent()).isEqualTo(skull);
        assertThat(skull.getChildren()).containsExactly(brain);
    }
}
