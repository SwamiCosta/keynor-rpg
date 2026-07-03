package com.keynor.rpg.domain.port.in;

import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.BodySystems;
import com.keynor.rpg.domain.model.PlayableCharacter;

public interface PreviewAttributesUseCase {

    PlayableCharacter calculate(Biomechanics biomechanics, BodySystems bodySystems);
}
