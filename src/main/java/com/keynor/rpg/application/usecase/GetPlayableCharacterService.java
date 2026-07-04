package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.Mind;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.port.in.GetPlayableCharacterUseCase;

public class GetPlayableCharacterService implements GetPlayableCharacterUseCase {

    /**
     * No character persistence or creation flow exists yet, so every id resolves to the same
     * in-memory human template — the {@code id} parameter is kept on the port/method signature
     * so the contract does not change once real lookup is implemented.
     */
    @Override
    public PlayableCharacter getById(String id) {
        return new PlayableCharacter("Keynor", Body.humanTemplate(), Mind.humanTemplate());
    }
}
