package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EruditionTest {

    private final PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

    @Test
    void defaults_hasNoSelectedTraits() {
        assertThat(Erudition.defaults().getSelectedTraits()).isEmpty();
    }

    @Test
    void select_upToFreeSlots_succeeds() {
        Erudition erudition = Erudition.defaults();

        erudition.select(Trait.ECOLOGY, character);
        erudition.select(Trait.BIOLOGY, character);

        assertThat(erudition.getSelectedTraits()).containsExactlyInAnyOrder(Trait.ECOLOGY, Trait.BIOLOGY);
    }

    @Test
    void select_reselectingAnAlreadySelectedTrait_isANoOp() {
        Erudition erudition = Erudition.defaults();
        erudition.select(Trait.ECOLOGY, character);

        erudition.select(Trait.ECOLOGY, character);

        assertThat(erudition.getSelectedTraits()).containsExactly(Trait.ECOLOGY);
    }

    @Test
    void select_beyondFreeSlots_throws() {
        Erudition erudition = Erudition.defaults();
        erudition.select(Trait.ECOLOGY, character);
        erudition.select(Trait.BIOLOGY, character);

        assertThatThrownBy(() -> erudition.select(Trait.CALLIGRAPHY, character))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void canSelect_beyondFreeSlots_isFalse() {
        Erudition erudition = new Erudition(Set.of(Trait.ECOLOGY, Trait.BIOLOGY));

        assertThat(erudition.canSelect(Trait.CALLIGRAPHY, character)).isFalse();
        assertThat(erudition.canSelect(Trait.ECOLOGY, character)).isTrue();
    }

    @Test
    void deselect_removesTraitAndFreesASlot() {
        Erudition erudition = Erudition.defaults();
        erudition.select(Trait.ECOLOGY, character);
        erudition.select(Trait.BIOLOGY, character);

        erudition.deselect(Trait.ECOLOGY);

        assertThat(erudition.getSelectedTraits()).containsExactly(Trait.BIOLOGY);
        assertThat(erudition.canSelect(Trait.CALLIGRAPHY, character)).isTrue();
    }

    @Test
    void hasTrait_reflectsSelection() {
        Erudition erudition = Erudition.defaults();

        assertThat(erudition.hasTrait(Trait.ECOLOGY)).isFalse();

        erudition.select(Trait.ECOLOGY, character);

        assertThat(erudition.hasTrait(Trait.ECOLOGY)).isTrue();
    }
}
