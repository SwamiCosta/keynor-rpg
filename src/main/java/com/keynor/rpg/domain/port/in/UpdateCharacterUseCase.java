package com.keynor.rpg.domain.port.in;

import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.BodySystems;
import com.keynor.rpg.domain.model.Erudition;
import com.keynor.rpg.domain.model.GeneralPersonality;
import com.keynor.rpg.domain.model.Labours;
import com.keynor.rpg.domain.model.Personality;
import com.keynor.rpg.domain.model.PhysicalTraits;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.model.Values;
import com.keynor.rpg.domain.model.WeaponProficiencies;

/**
 * Same Body/Mind shape as {@link CreateCharacterUseCase}, targeting an existing character by
 * id. Existing wound-tree damage and point-budget spend counts are preserved across the
 * update — only the raw input groups this method receives are replaced.
 */
public interface UpdateCharacterUseCase {

    PlayableCharacter update(String id, String name, Biomechanics biomechanics, BodySystems bodySystems,
                              PhysicalTraits physicalTraits, Values values, Erudition erudition,
                              Personality personality, Labours labours, GeneralPersonality generalPersonality,
                              WeaponProficiencies weaponProficiencies, String actor);
}
