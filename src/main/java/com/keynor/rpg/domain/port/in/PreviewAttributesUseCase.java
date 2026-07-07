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

public interface PreviewAttributesUseCase {

    PlayableCharacter calculate(Biomechanics biomechanics, BodySystems bodySystems, PhysicalTraits physicalTraits,
                                 Values values, Erudition erudition, Personality personality, Labours labours,
                                 GeneralPersonality generalPersonality, WeaponProficiencies weaponProficiencies);
}
