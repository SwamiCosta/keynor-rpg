package com.keynor.rpg.infrastructure.web;

import com.keynor.rpg.application.dto.CharacterResponse;
import com.keynor.rpg.application.dto.CreateCharacterRequest;
import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.Language;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.port.in.CreateCharacterUseCase;
import com.keynor.rpg.domain.port.in.GetPlayableCharacterUseCase;
import com.keynor.rpg.infrastructure.web.shared.LanguageRequestParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/characters")
public class CharacterController {

    private final GetPlayableCharacterUseCase getPlayableCharacterUseCase;
    private final CreateCharacterUseCase createCharacterUseCase;

    public CharacterController(GetPlayableCharacterUseCase getPlayableCharacterUseCase,
                                CreateCharacterUseCase createCharacterUseCase) {
        this.getPlayableCharacterUseCase = getPlayableCharacterUseCase;
        this.createCharacterUseCase = createCharacterUseCase;
    }

    @GetMapping("/{id}")
    public CharacterResponse getById(@PathVariable String id,
                                      @RequestParam(defaultValue = "EN") String language) {
        Language parsedLanguage = LanguageRequestParser.parse(language);
        return CharacterResponse.from(id, getPlayableCharacterUseCase.getById(id), parsedLanguage);
    }

    @PostMapping
    public ResponseEntity<CharacterResponse> create(@RequestBody CreateCharacterRequest request,
                                                      @RequestParam(defaultValue = "EN") String language) {
        Language parsedLanguage = LanguageRequestParser.parse(language);
        Biomechanics biomechanics = new Biomechanics(
                request.body().genetics().toDomain(), request.body().bodyComposition().toDomain());
        PlayableCharacter character = createCharacterUseCase.create(request.name(), biomechanics,
                request.body().bodySystems().toDomain(), request.body().physicalTraits().toDomain(),
                request.mind().values().toDomain(), request.mind().erudition().toDomain(),
                request.mind().personality().toDomain(), request.mind().labours().toDomain(),
                request.mind().generalPersonality().toDomain(), request.mind().weaponProficiencies().toDomain());
        CharacterResponse response = CharacterResponse.from(String.valueOf(character.getId()), character,
                parsedLanguage);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
