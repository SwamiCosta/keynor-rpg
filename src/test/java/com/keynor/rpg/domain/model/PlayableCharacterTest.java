package com.keynor.rpg.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayableCharacterTest {

    @Test
    void newCharacter_hasNoLoreReferenceUntilLinked() {
        PlayableCharacter character = new PlayableCharacter("Keynor", Body.humanTemplate(), Biomechanics.humanDefaults());

        assertThat(character.getName()).isEqualTo("Keynor");
        assertThat(character.getBody()).isNotNull();
        assertThat(character.getBiomechanics()).isNotNull();
        assertThat(character.getLoreReference()).isNull();
    }

    @Test
    void linkToLore_setsLoreReference() {
        PlayableCharacter character = new PlayableCharacter("Keynor", Body.humanTemplate(), Biomechanics.humanDefaults());

        character.linkToLore("character-keynor");

        assertThat(character.getLoreReference()).isEqualTo("character-keynor");
    }
}
