package com.keynor.rpg.infrastructure.config;

import com.keynor.rpg.application.usecase.GetPlayableCharacterService;
import com.keynor.rpg.domain.port.in.GetPlayableCharacterUseCase;
import com.keynor.rpg.domain.port.out.RandomSource;
import com.keynor.rpg.domain.service.BodyCascadeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfiguration {

    @Bean
    public BodyCascadeResolver bodyCascadeResolver(RandomSource randomSource) {
        return new BodyCascadeResolver(randomSource);
    }

    @Bean
    public GetPlayableCharacterUseCase getPlayableCharacterUseCase() {
        return new GetPlayableCharacterService();
    }
}
