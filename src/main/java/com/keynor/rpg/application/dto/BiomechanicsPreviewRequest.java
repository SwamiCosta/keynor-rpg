package com.keynor.rpg.application.dto;

public record BiomechanicsPreviewRequest(GeneticsInput genetics, BodyCompositionInput bodyComposition,
                                          BloodSystemInput bloodSystem, CardiacSystemInput cardiacSystem,
                                          PulmonarySystemInput pulmonarySystem, NervousSystemInput nervousSystem) {
}
