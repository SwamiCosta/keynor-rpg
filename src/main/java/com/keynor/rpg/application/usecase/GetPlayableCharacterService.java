package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.port.in.GetPlayableCharacterUseCase;
import com.keynor.rpg.infrastructure.persistence.character.CharacterRepository;

import java.util.NoSuchElementException;

/**
 * Depends directly on the concrete {@link CharacterRepository} — same scoped exception as
 * {@link CreateCharacterService}, see its javadoc.
 */
public class GetPlayableCharacterService implements GetPlayableCharacterUseCase {

    private final CharacterRepository characterRepository;

    public GetPlayableCharacterService(CharacterRepository characterRepository) {
        this.characterRepository = characterRepository;
    }

    @Override
    public PlayableCharacter getById(String id) {
        return characterRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new NoSuchElementException("Character not found: " + id));
    }
}
