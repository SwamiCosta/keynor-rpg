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
import com.keynor.rpg.domain.port.in.UpdateCharacterUseCase;
import com.keynor.rpg.domain.port.out.AuditLogger;
import com.keynor.rpg.infrastructure.persistence.character.CharacterRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Loads the existing character first so wound-tree damage state and point-budget spend counts
 * (neither of which this method's parameters carry) survive the update — only the raw input
 * groups actually received here are replaced. Depends directly on the concrete
 * {@link CharacterRepository}, same scoped exception as {@link CreateCharacterService}.
 */
public class UpdateCharacterService implements UpdateCharacterUseCase {

    private final CharacterRepository characterRepository;
    private final AuditLogger auditLogger;

    public UpdateCharacterService(CharacterRepository characterRepository, AuditLogger auditLogger) {
        this.characterRepository = characterRepository;
        this.auditLogger = auditLogger;
    }

    @Transactional
    @Override
    public PlayableCharacter update(String id, String name, Biomechanics biomechanics, BodySystems bodySystems,
                                     PhysicalTraits physicalTraits, Values values, Erudition erudition,
                                     Personality personality, Labours labours,
                                     GeneralPersonality generalPersonality,
                                     WeaponProficiencies weaponProficiencies, String actor) {
        Long characterId = Long.valueOf(id);
        PlayableCharacter existing = characterRepository.findById(characterId)
                .orElseThrow(() -> new NoSuchElementException("Character not found: " + id));

        Body existingBody = existing.getBody();
        Body body = Body.reconstruct(biomechanics, bodySystems, physicalTraits, existingBody.getGeneticPoints(),
                existingBody.getTrainingPoints(), existingBody.woundState());
        Mind mind = Mind.reconstruct(values, erudition, personality, labours, generalPersonality,
                weaponProficiencies, existing.getMind().getEventPoints());

        PlayableCharacter updated = new PlayableCharacter(name, body, mind);
        updated.assignId(characterId);
        if (existing.getLoreReference() != null) {
            updated.linkToLore(existing.getLoreReference());
        }
        characterRepository.save(updated);
        auditLogger.log(actor, "UPDATE", "CHARACTER", id);
        return updated;
    }
}
