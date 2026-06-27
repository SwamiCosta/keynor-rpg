package com.keynor.rpg.domain.port.in;

import com.keynor.rpg.domain.model.PlayableCharacter;

public interface GetPlayableCharacterUseCase {

    PlayableCharacter getById(String id);
}
