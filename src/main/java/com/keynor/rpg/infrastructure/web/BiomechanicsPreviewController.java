package com.keynor.rpg.infrastructure.web;

import com.keynor.rpg.application.dto.AttributesResponse;
import com.keynor.rpg.application.dto.BiomechanicsPreviewRequest;
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
    public AttributesResponse preview(@RequestBody BiomechanicsPreviewRequest request) {
        return AttributesResponse.from(previewAttributesUseCase.calculate(
                request.genetics().toDomain(),
                request.bodyComposition().toDomain(),
                request.bloodSystem().toDomain(),
                request.cardiacSystem().toDomain(),
                request.pulmonarySystem().toDomain(),
                request.nervousSystem().toDomain()));
    }
}
