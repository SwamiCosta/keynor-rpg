package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.BodySystems;
import com.keynor.rpg.domain.model.Erudition;
import com.keynor.rpg.domain.model.Mind;
import com.keynor.rpg.domain.model.PhysicalTraits;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.model.Values;
import com.keynor.rpg.domain.port.in.PreviewAttributesUseCase;

/**
 * Stateless: no character identity, persistence, or point-budget enforcement is involved.
 * Coefficients and point budgets are taken from {@link Body#previewTemplate}/{@link Mind#previewTemplate}
 * defaults — none of the attribute formulas depend on them, so any fixed value is safe.
 */
public class PreviewAttributesService implements PreviewAttributesUseCase {

    @Override
    public PlayableCharacter calculate(Biomechanics biomechanics, BodySystems bodySystems,
                                        PhysicalTraits physicalTraits, Values values, Erudition erudition) {
        Body body = Body.previewTemplate(biomechanics, bodySystems, physicalTraits);
        Mind mind = Mind.previewTemplate(values, erudition);
        return new PlayableCharacter("preview", body, mind);
    }
}
