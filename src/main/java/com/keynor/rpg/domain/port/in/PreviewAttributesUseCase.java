package com.keynor.rpg.domain.port.in;

import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.BloodSystem;
import com.keynor.rpg.domain.model.BodyComposition;
import com.keynor.rpg.domain.model.CardiacSystem;
import com.keynor.rpg.domain.model.Genetics;
import com.keynor.rpg.domain.model.NervousSystem;
import com.keynor.rpg.domain.model.PulmonarySystem;

public interface PreviewAttributesUseCase {

    Biomechanics calculate(Genetics genetics, BodyComposition bodyComposition, BloodSystem bloodSystem,
                            CardiacSystem cardiacSystem, PulmonarySystem pulmonarySystem, NervousSystem nervousSystem);
}
