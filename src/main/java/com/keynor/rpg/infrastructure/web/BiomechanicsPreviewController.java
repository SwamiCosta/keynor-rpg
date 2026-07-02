package com.keynor.rpg.infrastructure.web;

import com.keynor.rpg.application.dto.BiomechanicsPreviewRequest;
import com.keynor.rpg.application.dto.BiomechanicsPreviewResponse;
import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.port.in.PreviewAttributesUseCase;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/biomechanics")
public class BiomechanicsPreviewController {

    private final PreviewAttributesUseCase previewAttributesUseCase;

    public BiomechanicsPreviewController(PreviewAttributesUseCase previewAttributesUseCase) {
        this.previewAttributesUseCase = previewAttributesUseCase;
    }

    @PostMapping("/preview")
    public BiomechanicsPreviewResponse preview(@RequestBody BiomechanicsPreviewRequest request) {
        Biomechanics biomechanics = new Biomechanics(
                request.genetics().toDomain(), request.bodyComposition().toDomain());
        return BiomechanicsPreviewResponse.from(
                previewAttributesUseCase.calculate(biomechanics, request.bodySystems().toDomain()));
    }
}
