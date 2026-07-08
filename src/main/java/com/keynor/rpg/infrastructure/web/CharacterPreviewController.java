package com.keynor.rpg.infrastructure.web;

import com.keynor.rpg.application.dto.CharacterPreviewRequest;
import com.keynor.rpg.application.dto.CharacterPreviewResponse;
import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.port.in.PreviewAttributesUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Replaces {@code BiomechanicsPreviewController} (was {@code /api/v1/biomechanics/preview}) —
 * renamed and expanded to accept both pillars once Mind-pillar formulas started reading Body
 * inputs and vice versa (e.g. Reasoning reads both NeuralSystem and Values).
 */
@RestController
@RequestMapping("/api/v1/character")
public class CharacterPreviewController {

    private final PreviewAttributesUseCase previewAttributesUseCase;

    public CharacterPreviewController(PreviewAttributesUseCase previewAttributesUseCase) {
        this.previewAttributesUseCase = previewAttributesUseCase;
    }

    @PostMapping("/preview")
    public CharacterPreviewResponse preview(@RequestBody CharacterPreviewRequest request) {
        Biomechanics biomechanics = new Biomechanics(
                request.body().genetics().toDomain(), request.body().bodyComposition().toDomain());
        return CharacterPreviewResponse.from(
                previewAttributesUseCase.calculate(biomechanics, request.body().bodySystems().toDomain(),
                        request.body().physicalTraits().toDomain(), request.mind().values().toDomain(),
                        request.mind().erudition().toDomain(), request.mind().personality().toDomain(),
                        request.mind().labours().toDomain(), request.mind().generalPersonality().toDomain(),
                        request.mind().weaponProficiencies().toDomain()));
    }
}
