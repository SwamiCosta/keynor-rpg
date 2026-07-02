package com.keynor.rpg.application.dto;

public record BiomechanicsPreviewRequest(GeneticsInput genetics, BodyCompositionInput bodyComposition,
                                          BodySystemsInput bodySystems, PhysicalTraitsInput physicalTraits) {
}
