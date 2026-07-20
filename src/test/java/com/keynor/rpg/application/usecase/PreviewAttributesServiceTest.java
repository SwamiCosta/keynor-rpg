package com.keynor.rpg.application.usecase;

import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.model.BodyComposition;
import com.keynor.rpg.domain.model.BodySystems;
import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.Erudition;
import com.keynor.rpg.domain.model.GeneralPersonality;
import com.keynor.rpg.domain.model.Genetics;
import com.keynor.rpg.domain.model.Knowledge;
import com.keynor.rpg.domain.model.Labours;
import com.keynor.rpg.domain.model.Mind;
import com.keynor.rpg.domain.model.NeuralSystem;
import com.keynor.rpg.domain.model.Personality;
import com.keynor.rpg.domain.model.PhysicalTraits;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.model.Values;
import com.keynor.rpg.domain.model.WeaponProficiencies;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PreviewAttributesServiceTest {

    private final PreviewAttributesService service = new PreviewAttributesService();

    private PlayableCharacter calculateWithDefaults(Biomechanics biomechanics, BodySystems bodySystems,
                                                      PhysicalTraits physicalTraits, Values values,
                                                      Erudition erudition) {
        return service.calculate(biomechanics, bodySystems, physicalTraits, values, erudition,
                Personality.defaults(), Labours.defaults(), GeneralPersonality.defaults(),
                WeaponProficiencies.defaults());
    }

    @Test
    void calculate_withDefaults_returnsCharacterWithSameAttributesAsHumanTemplate() {
        PlayableCharacter result = calculateWithDefaults(Biomechanics.defaults(), BodySystems.defaults(),
                PhysicalTraits.defaults(), Values.defaults(), Erudition.defaults());

        PlayableCharacter expected = new PlayableCharacter("expected", Body.humanTemplate(), Mind.humanTemplate());
        assertThat(result.getPushStrength()).isEqualTo(expected.getPushStrength());
        assertThat(result.getLegDrive()).isEqualTo(expected.getLegDrive());
        assertThat(result.getGripStrength()).isEqualTo(expected.getGripStrength());
        assertThat(result.getLiftStrength()).isEqualTo(expected.getLiftStrength());
        assertThat(result.getSpeed()).isEqualTo(expected.getSpeed());
        assertThat(result.getStaminaPool()).isEqualTo(expected.getStaminaPool());
        assertThat(result.getSoftTissueDurability()).isEqualTo(expected.getSoftTissueDurability());
        assertThat(result.getBoneDurability()).isEqualTo(expected.getBoneDurability());
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
        assertThat(result.getAnalysis()).isEqualTo(expected.getAnalysis());
    }

    @Test
    void calculate_reactsToInputChanges_higherMuscleMassIncreasesPushStrength() {
        PlayableCharacter baseline = calculateWithDefaults(Biomechanics.defaults(), BodySystems.defaults(),
                PhysicalTraits.defaults(), Values.defaults(), Erudition.defaults());

        BodyComposition heavierMuscle = new BodyComposition(3, 12, 5, 5, 5, 5, 5);
        PlayableCharacter result = calculateWithDefaults(new Biomechanics(Genetics.defaults(), heavierMuscle),
                BodySystems.defaults(), PhysicalTraits.defaults(), Values.defaults(), Erudition.defaults());

        assertThat(result.getPushStrength()).isGreaterThan(baseline.getPushStrength());
    }

    @Test
    void calculate_reactsToInputChanges_higherAgilityIncreasesEvasion() {
        PlayableCharacter baseline = calculateWithDefaults(Biomechanics.defaults(), BodySystems.defaults(),
                PhysicalTraits.defaults(), Values.defaults(), Erudition.defaults());

        NeuralSystem agileNeural = new NeuralSystem(5, 5, 5, 5, 5, 5, 5, 5, 5, 9, 5, 0, 0);
        BodySystems bodySystems = new BodySystems(BodySystems.defaults().getBloodSystem(),
                BodySystems.defaults().getCardiacSystem(), BodySystems.defaults().getPulmonarySystem(), agileNeural,
                BodySystems.defaults().getHormonalGlandularSystem(), BodySystems.defaults().getDigestiveSystem());
        PlayableCharacter result = calculateWithDefaults(Biomechanics.defaults(), bodySystems,
                PhysicalTraits.defaults(), Values.defaults(), Erudition.defaults());

        assertThat(result.getEvasion()).isGreaterThan(baseline.getEvasion());
    }

    @Test
    void calculate_reactsToInputChanges_higherEcologyLevelIncreasesSurvivalSkills() {
        PlayableCharacter baseline = calculateWithDefaults(Biomechanics.defaults(), BodySystems.defaults(),
                PhysicalTraits.defaults(), Values.defaults(), Erudition.defaults());

        Erudition ecologist = new Erudition(Map.of(Knowledge.ECOLOGY, 2));
        PlayableCharacter result = calculateWithDefaults(Biomechanics.defaults(), BodySystems.defaults(),
                PhysicalTraits.defaults(), Values.defaults(), ecologist);

        assertThat(result.getSurvivalSkills()).isGreaterThan(baseline.getSurvivalSkills());
    }
}
