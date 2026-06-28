package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.BloodSystem;
import com.keynor.rpg.domain.model.BodyComposition;
import com.keynor.rpg.domain.model.CardiacSystem;
import com.keynor.rpg.domain.model.Genetics;
import com.keynor.rpg.domain.model.NervousSystem;
import com.keynor.rpg.domain.model.PulmonarySystem;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreviewAttributesServiceTest {

    private final PreviewAttributesService service = new PreviewAttributesService();

    @Test
    void calculate_withDefaults_matchesHumanDefaultsAttributes() {
        Biomechanics result = service.calculate(Genetics.defaults(), BodyComposition.defaults(),
                BloodSystem.defaults(), CardiacSystem.defaults(), PulmonarySystem.defaults(),
                NervousSystem.defaults());

        Biomechanics expected = Biomechanics.humanDefaults();
        assertThat(result.getStrength()).isEqualTo(expected.getStrength());
        assertThat(result.getSpeed()).isEqualTo(expected.getSpeed());
        assertThat(result.getStaminaPool()).isEqualTo(expected.getStaminaPool());
        assertThat(result.getDurability()).isEqualTo(expected.getDurability());
        assertThat(result.getCardiovascularCapacity()).isEqualTo(expected.getCardiovascularCapacity());
    }

    @Test
    void calculate_reactsToInputChanges_higherMuscleMassIncreasesStrength() {
        Biomechanics baseline = service.calculate(Genetics.defaults(), BodyComposition.defaults(),
                BloodSystem.defaults(), CardiacSystem.defaults(), PulmonarySystem.defaults(),
                NervousSystem.defaults());

        BodyComposition heavierMuscle = new BodyComposition(70, 0.20, 50, 0.0, 0.5);
        Biomechanics result = service.calculate(Genetics.defaults(), heavierMuscle, BloodSystem.defaults(),
                CardiacSystem.defaults(), PulmonarySystem.defaults(), NervousSystem.defaults());

        assertThat(result.getStrength()).isGreaterThan(baseline.getStrength());
    }
}
