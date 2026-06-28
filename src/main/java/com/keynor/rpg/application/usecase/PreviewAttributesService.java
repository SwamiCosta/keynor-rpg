package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.AttributePointBudget;
import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.BiomechanicsBalance;
import com.keynor.rpg.domain.model.BloodSystem;
import com.keynor.rpg.domain.model.BodyComposition;
import com.keynor.rpg.domain.model.CardiacSystem;
import com.keynor.rpg.domain.model.Genetics;
import com.keynor.rpg.domain.model.NervousSystem;
import com.keynor.rpg.domain.model.PulmonarySystem;
import com.keynor.rpg.domain.port.in.PreviewAttributesUseCase;

/**
 * Stateless: no character identity, persistence, or point-budget enforcement is involved.
 * The point budgets and balance below are placeholders only — every {@link Biomechanics}
 * output formula depends solely on the six system arguments, never on these two, so any
 * fixed value here is safe for calculation purposes.
 */
public class PreviewAttributesService implements PreviewAttributesUseCase {

    private static final int PLACEHOLDER_POINT_BUDGET = 20;

    @Override
    public Biomechanics calculate(Genetics genetics, BodyComposition bodyComposition, BloodSystem bloodSystem,
                                   CardiacSystem cardiacSystem, PulmonarySystem pulmonarySystem,
                                   NervousSystem nervousSystem) {
        return new Biomechanics(genetics, bloodSystem, bodyComposition, nervousSystem, cardiacSystem,
                pulmonarySystem, new AttributePointBudget(PLACEHOLDER_POINT_BUDGET),
                new AttributePointBudget(PLACEHOLDER_POINT_BUDGET), BiomechanicsBalance.defaults());
    }
}
