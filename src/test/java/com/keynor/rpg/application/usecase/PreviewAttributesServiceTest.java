package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.BodyComposition;
import com.keynor.rpg.domain.model.BodySystems;
import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.Genetics;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.model.SpatialIntelligence;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreviewAttributesServiceTest {

    private final PreviewAttributesService service = new PreviewAttributesService();

    @Test
    void calculate_withDefaults_returnsCharacterWithSameAttributesAsHumanTemplate() {
        PlayableCharacter result = service.calculate(Biomechanics.defaults(), BodySystems.defaults(),
                SpatialIntelligence.defaults());

        PlayableCharacter expected = new PlayableCharacter("expected", Body.humanTemplate());
        assertThat(result.getStrength()).isEqualTo(expected.getStrength());
        assertThat(result.getSpeed()).isEqualTo(expected.getSpeed());
        assertThat(result.getStaminaPool()).isEqualTo(expected.getStaminaPool());
        assertThat(result.getDurability()).isEqualTo(expected.getDurability());
        assertThat(result.getCardiovascularCapacity()).isEqualTo(expected.getCardiovascularCapacity());
        assertThat(result.getSight()).isEqualTo(expected.getSight());
        assertThat(result.getEvasion()).isEqualTo(expected.getEvasion());
        assertThat(result.getAcrobatics()).isEqualTo(expected.getAcrobatics());
    }

    @Test
    void calculate_reactsToInputChanges_higherMuscleMassIncreasesStrength() {
        PlayableCharacter baseline = service.calculate(Biomechanics.defaults(), BodySystems.defaults(),
                SpatialIntelligence.defaults());

        BodyComposition heavierMuscle = new BodyComposition(14, 50, 0.0, 5.0, 5.0);
        PlayableCharacter result = service.calculate(new Biomechanics(Genetics.defaults(), heavierMuscle),
                BodySystems.defaults(), SpatialIntelligence.defaults());

        assertThat(result.getStrength()).isGreaterThan(baseline.getStrength());
    }

    @Test
    void calculate_reactsToInputChanges_higherAgilityIncreasesEvasion() {
        PlayableCharacter baseline = service.calculate(Biomechanics.defaults(), BodySystems.defaults(),
                SpatialIntelligence.defaults());

        PlayableCharacter result = service.calculate(Biomechanics.defaults(), BodySystems.defaults(),
                new SpatialIntelligence(5, 9, 5));

        assertThat(result.getEvasion()).isGreaterThan(baseline.getEvasion());
    }
}
