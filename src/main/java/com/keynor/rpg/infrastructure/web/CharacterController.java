package com.keynor.rpg.infrastructure.web;

import com.keynor.rpg.application.dto.CharacterResponse;
import com.keynor.rpg.domain.model.Language;
import com.keynor.rpg.domain.port.in.GetPlayableCharacterUseCase;
import com.keynor.rpg.infrastructure.web.shared.LanguageRequestParser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/characters")
public class CharacterController {

    private final GetPlayableCharacterUseCase getPlayableCharacterUseCase;

    public CharacterController(GetPlayableCharacterUseCase getPlayableCharacterUseCase) {
        this.getPlayableCharacterUseCase = getPlayableCharacterUseCase;
    }

    @GetMapping("/{id}")
    public CharacterResponse getById(@PathVariable String id,
                                      @RequestParam(defaultValue = "EN") String language) {
        Language parsedLanguage = LanguageRequestParser.parse(language);
        return CharacterResponse.from(id, getPlayableCharacterUseCase.getById(id), parsedLanguage);
    }
}
