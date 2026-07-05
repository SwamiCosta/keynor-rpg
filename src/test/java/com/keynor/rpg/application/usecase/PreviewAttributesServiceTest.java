package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.BodyComposition;
import com.keynor.rpg.domain.model.BodySystems;
import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.Erudition;
import com.keynor.rpg.domain.model.Genetics;
import com.keynor.rpg.domain.model.Mind;
import com.keynor.rpg.domain.model.NeuralSystem;
import com.keynor.rpg.domain.model.PhysicalTraits;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.model.Values;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PreviewAttributesServiceTest {

    private final PreviewAttributesService service = new PreviewAttributesService();

    @Test
    void calculate_withDefaults_returnsCharacterWithSameAttributesAsHumanTemplate() {
        PlayableCharacter result = service.calculate(Biomechanics.defaults(), BodySystems.defaults(),
                PhysicalTraits.defaults(), Values.defaults(), Erudition.defaults());

        PlayableCharacter expected = new PlayableCharacter("expected", Body.humanTemplate(), Mind.humanTemplate());
        assertThat(result.getPushStrength()).isEqualTo(expected.getPushStrength());
        assertThat(result.getLegDrive()).isEqualTo(expected.getLegDrive());
        assertThat(result.getGripStrength()).isEqualTo(expected.getGripStrength());
        assertThat(result.getLiftStrength()).isEqualTo(expected.getLiftStrength());
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
        assertThat(result.getIntimidation()).isEqualTo(expected.getIntimidation());
        assertThat(result.getFatGainRate()).isEqualTo(expected.getFatGainRate());
        assertThat(result.getAngerResistance()).isEqualTo(expected.getAngerResistance());
        assertThat(result.getPainThreshold()).isEqualTo(expected.getPainThreshold());
        assertThat(result.getSelfConcern()).isEqualTo(expected.getSelfConcern());
        assertThat(result.getSurvivalSkills()).isEqualTo(expected.getSurvivalSkills());
    }

    @Test
    void calculate_reactsToInputChanges_higherMuscleMassIncreasesPushStrength() {
        PlayableCharacter baseline = service.calculate(Biomechanics.defaults(), BodySystems.defaults(),
                PhysicalTraits.defaults(), Values.defaults(), Erudition.defaults());

        BodyComposition heavierMuscle = new BodyComposition(3, 12, 5, 5, 5, 5, 5);
        PlayableCharacter result = service.calculate(new Biomechanics(Genetics.defaults(), heavierMuscle),
                BodySystems.defaults(), PhysicalTraits.defaults(), Values.defaults(), Erudition.defaults());

        assertThat(result.getPushStrength()).isGreaterThan(baseline.getPushStrength());
    }

    @Test
    void calculate_reactsToInputChanges_higherAgilityIncreasesEvasion() {
        PlayableCharacter baseline = service.calculate(Biomechanics.defaults(), BodySystems.defaults(),
                PhysicalTraits.defaults(), Values.defaults(), Erudition.defaults());

        NeuralSystem agileNeural = new NeuralSystem(5, 5, 5, 5, 5, 5, 5, 5, 5, 9, 5, 0);
        BodySystems bodySystems = new BodySystems(BodySystems.defaults().getBloodSystem(),
                BodySystems.defaults().getCardiacSystem(), BodySystems.defaults().getPulmonarySystem(), agileNeural,
                BodySystems.defaults().getHormonalGlandularSystem(), BodySystems.defaults().getDigestiveSystem());
        PlayableCharacter result = service.calculate(Biomechanics.defaults(), bodySystems, PhysicalTraits.defaults(),
                Values.defaults(), Erudition.defaults());

        assertThat(result.getEvasion()).isGreaterThan(baseline.getEvasion());
    }

    @Test
    void calculate_reactsToInputChanges_higherKnowledgeIncreasesShortMemory() {
        PlayableCharacter baseline = service.calculate(Biomechanics.defaults(), BodySystems.defaults(),
                PhysicalTraits.defaults(), Values.defaults(), Erudition.defaults());

        Values knowledgeable = Values.defaults();
        knowledgeable.setKnowledge(5);
        PlayableCharacter result = service.calculate(Biomechanics.defaults(), BodySystems.defaults(),
                PhysicalTraits.defaults(), knowledgeable, Erudition.defaults());

        assertThat(result.getShortMemory()).isGreaterThan(baseline.getShortMemory());
    }
}
