package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.PlayableCharacter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GetPlayableCharacterServiceTest {

    private final GetPlayableCharacterService service = new GetPlayableCharacterService();

    @Test
    void getById_returnsHumanTemplateCharacterWithBodyAndBiomechanics() {
        PlayableCharacter character = service.getById("any-id");

        assertThat(character.getName()).isEqualTo("Keynor");
        assertThat(character.getBody()).isNotNull();
        assertThat(character.getBody().getBiomechanics()).isNotNull();
        assertThat(character.getBody().rootComponents()).hasSize(10);
    }

    @Test
    void getById_ignoresIdAndAlwaysReturnsSameTemplate() {
        PlayableCharacter first = service.getById("id-1");
        PlayableCharacter second = service.getById("id-2");

        assertThat(first.getName()).isEqualTo(second.getName());
    }
}
