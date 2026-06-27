package com.keynor.rpg.application.dto;

import com.keynor.rpg.domain.model.PlayableCharacter;

public record CharacterResponse(String id, String name, BodyResponse body) {

    public static CharacterResponse from(String id, PlayableCharacter character) {
        return new CharacterResponse(id, character.getName(), BodyResponse.from(character.getBody()));
    }
}
