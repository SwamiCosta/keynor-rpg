package com.keynor.rpg.application.dto;

/**
 * Renamed from {@code BiomechanicsPreviewRequest} when the preview contract unified Body and
 * Mind (see {@link CharacterPreviewRequest}) — the old name was already narrower than its
 * content (all four Body data groups, not just {@code Biomechanics}), so the rename also fixes
 * that pre-existing mismatch.
 */
public record BodyPreviewRequest(GeneticsInput genetics, BodyCompositionInput bodyComposition,
                                  BodySystemsInput bodySystems, PhysicalTraitsInput physicalTraits) {
}
