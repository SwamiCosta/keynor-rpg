package com.keynor.rpg.infrastructure.config;

import com.keynor.rpg.application.usecase.CombatActionTimeService;
import com.keynor.rpg.application.usecase.CreateCharacterService;
import com.keynor.rpg.application.usecase.DeleteCharacterService;
import com.keynor.rpg.application.usecase.GetPlayableCharacterService;
import com.keynor.rpg.application.usecase.PreviewAttributesService;
import com.keynor.rpg.application.usecase.UpdateCharacterService;
import com.keynor.rpg.domain.port.in.CalculateCombatActionTimeUseCase;
import com.keynor.rpg.domain.port.in.CreateCharacterUseCase;
import com.keynor.rpg.domain.port.in.DeleteCharacterUseCase;
import com.keynor.rpg.domain.port.in.GetPlayableCharacterUseCase;
import com.keynor.rpg.domain.port.in.PreviewAttributesUseCase;
import com.keynor.rpg.domain.port.in.UpdateCharacterUseCase;
import com.keynor.rpg.domain.port.out.AuditLogger;
import com.keynor.rpg.domain.port.out.RandomSource;
import com.keynor.rpg.domain.service.BodyCascadeResolver;
import com.keynor.rpg.domain.service.CombatActionTimeCalculator;
import com.keynor.rpg.infrastructure.persistence.character.CharacterRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {

    @Bean
    public BodyCascadeResolver bodyCascadeResolver(RandomSource randomSource) {
        return new BodyCascadeResolver(randomSource);
    }

    @Bean
    public GetPlayableCharacterUseCase getPlayableCharacterUseCase(CharacterRepository characterRepository) {
        return new GetPlayableCharacterService(characterRepository);
    }

    @Bean
    public CreateCharacterUseCase createCharacterUseCase(CharacterRepository characterRepository,
                                                           AuditLogger auditLogger) {
        return new CreateCharacterService(characterRepository, auditLogger);
    }

    @Bean
    public UpdateCharacterUseCase updateCharacterUseCase(CharacterRepository characterRepository,
                                                           AuditLogger auditLogger) {
        return new UpdateCharacterService(characterRepository, auditLogger);
    }

    @Bean
    public DeleteCharacterUseCase deleteCharacterUseCase(CharacterRepository characterRepository,
                                                           AuditLogger auditLogger) {
        return new DeleteCharacterService(characterRepository, auditLogger);
    }

    @Bean
    public PreviewAttributesUseCase previewAttributesUseCase() {
        return new PreviewAttributesService();
    }

    @Bean
    public CombatActionTimeCalculator combatActionTimeCalculator() {
        return new CombatActionTimeCalculator();
    }

    @Bean
    public CalculateCombatActionTimeUseCase calculateCombatActionTimeUseCase(CombatActionTimeCalculator calculator) {
        return new CombatActionTimeService(calculator);
    }
}
