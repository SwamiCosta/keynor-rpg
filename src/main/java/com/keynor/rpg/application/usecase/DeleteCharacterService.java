package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.port.in.DeleteCharacterUseCase;
import com.keynor.rpg.domain.port.out.AuditLogger;
import com.keynor.rpg.infrastructure.persistence.character.CharacterRepository;
import org.springframework.transaction.annotation.Transactional;

public class DeleteCharacterService implements DeleteCharacterUseCase {

    private final CharacterRepository characterRepository;
    private final AuditLogger auditLogger;

    public DeleteCharacterService(CharacterRepository characterRepository, AuditLogger auditLogger) {
        this.characterRepository = characterRepository;
        this.auditLogger = auditLogger;
    }

    @Transactional
    @Override
    public void delete(String id, String actor) {
        characterRepository.deleteById(Long.valueOf(id));
        auditLogger.log(actor, "DELETE", "CHARACTER", id);
    }
}
