package com.keynor.rpg.infrastructure.web;

import com.keynor.rpg.application.dto.CharacterResponse;
import com.keynor.rpg.application.dto.CreateCharacterRequest;
import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.Language;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.port.in.CreateCharacterUseCase;
import com.keynor.rpg.domain.port.in.DeleteCharacterUseCase;
import com.keynor.rpg.domain.port.in.GetPlayableCharacterUseCase;
import com.keynor.rpg.domain.port.in.UpdateCharacterUseCase;
import com.keynor.rpg.infrastructure.web.shared.LanguageRequestParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@code X-Actor} is an optional, unverified caller-supplied header threaded into every
 * mutating call's audit log entry — this project has no authentication system yet, so it is
 * the best available "who did this" signal until one exists. Defaults to {@code "unknown"}
 * when absent, never blocks the request.
 */
@RestController
@RequestMapping("/api/v1/characters")
public class CharacterController {

    private final GetPlayableCharacterUseCase getPlayableCharacterUseCase;
    private final CreateCharacterUseCase createCharacterUseCase;
    private final UpdateCharacterUseCase updateCharacterUseCase;
    private final DeleteCharacterUseCase deleteCharacterUseCase;

    public CharacterController(GetPlayableCharacterUseCase getPlayableCharacterUseCase,
                                CreateCharacterUseCase createCharacterUseCase,
                                UpdateCharacterUseCase updateCharacterUseCase,
                                DeleteCharacterUseCase deleteCharacterUseCase) {
        this.getPlayableCharacterUseCase = getPlayableCharacterUseCase;
        this.createCharacterUseCase = createCharacterUseCase;
        this.updateCharacterUseCase = updateCharacterUseCase;
        this.deleteCharacterUseCase = deleteCharacterUseCase;
    }

    @GetMapping("/{id}")
    public CharacterResponse getById(@PathVariable String id,
                                      @RequestParam(defaultValue = "EN") String language) {
        Language parsedLanguage = LanguageRequestParser.parse(language);
        return CharacterResponse.from(id, getPlayableCharacterUseCase.getById(id), parsedLanguage);
    }

    @PostMapping
    public ResponseEntity<CharacterResponse> create(@RequestBody CreateCharacterRequest request,
                                                      @RequestParam(defaultValue = "EN") String language,
                                                      @RequestHeader(value = "X-Actor", defaultValue = "unknown")
                                                      String actor) {
        Language parsedLanguage = LanguageRequestParser.parse(language);
        Biomechanics biomechanics = new Biomechanics(
                request.body().genetics().toDomain(), request.body().bodyComposition().toDomain());
        PlayableCharacter character = createCharacterUseCase.create(request.name(), biomechanics,
                request.body().bodySystems().toDomain(), request.body().physicalTraits().toDomain(),
                request.mind().values().toDomain(), request.mind().erudition().toDomain(),
                request.mind().personality().toDomain(), request.mind().labours().toDomain(),
                request.mind().generalPersonality().toDomain(), request.mind().weaponProficiencies().toDomain(),
                actor);
        CharacterResponse response = CharacterResponse.from(String.valueOf(character.getId()), character,
                parsedLanguage);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public CharacterResponse update(@PathVariable String id, @RequestBody CreateCharacterRequest request,
                                     @RequestParam(defaultValue = "EN") String language,
                                     @RequestHeader(value = "X-Actor", defaultValue = "unknown") String actor) {
        Language parsedLanguage = LanguageRequestParser.parse(language);
        Biomechanics biomechanics = new Biomechanics(
                request.body().genetics().toDomain(), request.body().bodyComposition().toDomain());
        PlayableCharacter character = updateCharacterUseCase.update(id, request.name(), biomechanics,
                request.body().bodySystems().toDomain(), request.body().physicalTraits().toDomain(),
                request.mind().values().toDomain(), request.mind().erudition().toDomain(),
                request.mind().personality().toDomain(), request.mind().labours().toDomain(),
                request.mind().generalPersonality().toDomain(), request.mind().weaponProficiencies().toDomain(),
                actor);
        return CharacterResponse.from(id, character, parsedLanguage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id,
                                        @RequestHeader(value = "X-Actor", defaultValue = "unknown") String actor) {
        deleteCharacterUseCase.delete(id, actor);
        return ResponseEntity.noContent().build();
    }
}
