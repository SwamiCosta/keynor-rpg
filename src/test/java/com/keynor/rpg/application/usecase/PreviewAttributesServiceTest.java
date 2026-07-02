package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.BodyComposition;
import com.keynor.rpg.domain.model.BodySystems;
import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.Genetics;
import com.keynor.rpg.domain.model.NeuralSystem;
import com.keynor.rpg.domain.model.PlayableCharacter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreviewAttributesServiceTest {

    private final PreviewAttributesService service = new PreviewAttributesService();

    @Test
    void calculate_withDefaults_returnsCharacterWithSameAttributesAsHumanTemplate() {
        PlayableCharacter result = service.calculate(Biomechanics.defaults(), BodySystems.defaults());

        PlayableCharacter expected = new PlayableCharacter("expected", Body.humanTemplate());
        assertThat(result.getStrength()).isEqualTo(expected.getStrength());
        assertThat(result.getSpeed()).isEqualTo(expected.getSpeed());
        assertThat(result.getStaminaPool()).isEqualTo(expected.getStaminaPool());
        assertThat(result.getDurability()).isEqualTo(expected.getDurability());
        assertThat(result.getFatigueResistance()).isEqualTo(expected.getFatigueResistance());
        assertThat(result.getStaminaRecovery()).isEqualTo(expected.getStaminaRecovery());
        assertThat(result.getSight()).isEqualTo(expected.getSight());
        assertThat(result.getEvasion()).isEqualTo(expected.getEvasion());
        assertThat(result.getAcrobatics()).isEqualTo(expected.getAcrobatics());
        assertThat(result.getMemoryPool()).isEqualTo(expected.getMemoryPool());
        assertThat(result.getThermalResistance()).isEqualTo(expected.getThermalResistance());
    }

    @Test
    void calculate_reactsToInputChanges_higherMuscleMassIncreasesStrength() {
        PlayableCharacter baseline = service.calculate(Biomechanics.defaults(), BodySystems.defaults());

        BodyComposition heavierMuscle = new BodyComposition(3, 12, 5, 5, 5);
        PlayableCharacter result = service.calculate(new Biomechanics(Genetics.defaults(), heavierMuscle),
                BodySystems.defaults());

        assertThat(result.getStrength()).isGreaterThan(baseline.getStrength());
    }

    @Test
    void calculate_reactsToInputChanges_higherAgilityIncreasesEvasion() {
        PlayableCharacter baseline = service.calculate(Biomechanics.defaults(), BodySystems.defaults());

        NeuralSystem agileNeural = new NeuralSystem(5, 5, 5, 5, 5, 5, 5, 5, 9, 5);
        BodySystems bodySystems = new BodySystems(BodySystems.defaults().getBloodSystem(),
                BodySystems.defaults().getCardiacSystem(), BodySystems.defaults().getPulmonarySystem(), agileNeural,
                BodySystems.defaults().getHormonalSystem(), BodySystems.defaults().getDigestiveSystem());
        PlayableCharacter result = service.calculate(Biomechanics.defaults(), bodySystems);

        assertThat(result.getEvasion()).isGreaterThan(baseline.getEvasion());
    }
}
