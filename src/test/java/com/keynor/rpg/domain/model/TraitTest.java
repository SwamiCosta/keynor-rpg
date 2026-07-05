package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TraitTest {

    private final PlayableCharacter character = new PlayableCharacter("test", Body.humanTemplate());

    @Test
    void everyTrait_isEventfulByDefault() {
        for (Trait trait : Trait.values()) {
            assertThat(trait.getNature()).isEqualTo(InputNature.EVENTFUL);
        }
    }

    @Test
    void everyTrait_hasNoPrerequisiteYet_soAlwaysAvailable() {
        for (Trait trait : Trait.values()) {
            assertThat(trait.prerequisitesMet(character)).isTrue();
        }
    }

    @Test
    void groups_matchTheEruditionTabMapping() {
        assertThat(Trait.CALLIGRAPHY.getGroup()).isEqualTo(TraitGroup.LANGUAGES_AND_COMMUNICATION);
        assertThat(Trait.ECOLOGY.getGroup()).isEqualTo(TraitGroup.LIFE_STUDIES);
        assertThat(Trait.BIOLOGY.getGroup()).isEqualTo(TraitGroup.LIFE_STUDIES);
        assertThat(Trait.MEDICINE.getGroup()).isEqualTo(TraitGroup.LIFE_STUDIES);
        assertThat(Trait.HERBOLOGY.getGroup()).isEqualTo(TraitGroup.LIFE_STUDIES);
        assertThat(Trait.ALCHEMY_CHEMISTRY.getGroup()).isEqualTo(TraitGroup.MATTER_STUDIES);
        assertThat(Trait.METALLURGY.getGroup()).isEqualTo(TraitGroup.MATTER_STUDIES);
        assertThat(Trait.POTTERY.getGroup()).isEqualTo(TraitGroup.MATTER_STUDIES);
        assertThat(Trait.COMPUTER_SCIENCE.getGroup()).isEqualTo(TraitGroup.MATHEMATICS);
        assertThat(Trait.ENGINEERING.getGroup()).isEqualTo(TraitGroup.MATHEMATICS);
        assertThat(Trait.WIZARDRY.getGroup()).isEqualTo(TraitGroup.ARCANE_STUDIES);
        assertThat(Trait.SORCERY.getGroup()).isEqualTo(TraitGroup.ARCANE_STUDIES);
        assertThat(Trait.ARCHERY.getGroup()).isEqualTo(TraitGroup.ATHLETISM_AND_MARTIAL_ARTS);
        assertThat(Trait.HISTORY.getGroup()).isEqualTo(TraitGroup.VALKANI_STUDIES);
        assertThat(Trait.PHILOSOPHY.getGroup()).isEqualTo(TraitGroup.VALKANI_STUDIES);
        assertThat(Trait.CARTOGRAPHY.getGroup()).isEqualTo(TraitGroup.VALKANI_STUDIES);
        assertThat(Trait.ART.getGroup()).isEqualTo(TraitGroup.VALKANI_STUDIES);
    }
}
