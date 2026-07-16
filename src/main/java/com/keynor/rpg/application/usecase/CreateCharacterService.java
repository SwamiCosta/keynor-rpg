package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.BodySystems;
import com.keynor.rpg.domain.model.Erudition;
import com.keynor.rpg.domain.model.GeneralPersonality;
import com.keynor.rpg.domain.model.Labours;
import com.keynor.rpg.domain.model.Mind;
import com.keynor.rpg.domain.model.Personality;
import com.keynor.rpg.domain.model.PhysicalTraits;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.model.Values;
import com.keynor.rpg.domain.model.WeaponProficiencies;
import com.keynor.rpg.domain.port.in.CreateCharacterUseCase;
import com.keynor.rpg.domain.port.out.AuditLogger;
import com.keynor.rpg.infrastructure.persistence.character.CharacterRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Depends directly on the concrete {@link CharacterRepository} rather than a domain
 * {@code port.out} interface — a deliberate, scoped exception to this project's usual
 * application-depends-only-on-domain rule, requested by the user (2026-07-15) specifically for
 * {@code PlayableCharacter} persistence while its shape keeps changing quickly. Every other use
 * case in this project follows the standard port/adapter boundary; this is not a precedent for
 * skipping it elsewhere. {@link AuditLogger} is a normal domain port.
 */
public class CreateCharacterService implements CreateCharacterUseCase {

    private final CharacterRepository characterRepository;
    private final AuditLogger auditLogger;

    public CreateCharacterService(CharacterRepository characterRepository, AuditLogger auditLogger) {
        this.characterRepository = characterRepository;
        this.auditLogger = auditLogger;
    }

    @Transactional
    @Override
    public PlayableCharacter create(String name, Biomechanics biomechanics, BodySystems bodySystems,
                                     PhysicalTraits physicalTraits, Values values, Erudition erudition,
                                     Personality personality, Labours labours,
                                     GeneralPersonality generalPersonality,
                                     WeaponProficiencies weaponProficiencies, String actor) {
        Body body = Body.previewTemplate(biomechanics, bodySystems, physicalTraits);
        Mind mind = Mind.previewTemplate(values, erudition, personality, labours, generalPersonality,
                weaponProficiencies);
        PlayableCharacter character = new PlayableCharacter(name, body, mind);
        characterRepository.save(character);
        auditLogger.log(actor, "CREATE", "CHARACTER", String.valueOf(character.getId()));
        return character;
    }
}
