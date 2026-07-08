package com.keynor.rpg.infrastructure.web;

import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.Mind;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.port.in.GetPlayableCharacterUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CharacterController.class)
class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetPlayableCharacterUseCase getPlayableCharacterUseCase;

    @Test
    void getById_returnsCharacterSheetWithAllBodyGroups() throws Exception {
        PlayableCharacter character = new PlayableCharacter("Keynor", Body.humanTemplate(), Mind.humanTemplate());
        when(getPlayableCharacterUseCase.getById("keynor-1")).thenReturn(character);

        mockMvc.perform(get("/api/v1/characters/keynor-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("keynor-1"))
                .andExpect(jsonPath("$.name").value("Keynor"))
                .andExpect(jsonPath("$.body.biomechanics.genetics.height").value(7))
                .andExpect(jsonPath("$.body.biomechanics.genetics.skinThickness").doesNotExist())
                .andExpect(jsonPath("$.body.biomechanics.bodyComposition.bodyFat").value(3))
                .andExpect(jsonPath("$.body.biomechanics.bodyComposition.boneDensity").value(5))
                .andExpect(jsonPath("$.body.biomechanics.bodyComposition.tendonsAndLigaments").value(5))
                .andExpect(jsonPath("$.body.bodySystems.bloodSystem.bloodThickness").value(3))
                .andExpect(jsonPath("$.body.bodySystems.neuralSystem.neuralDrive").value(5))
                .andExpect(jsonPath("$.body.bodySystems.neuralSystem.hippocampus").value(5))
                .andExpect(jsonPath("$.body.bodySystems.neuralSystem.thalamus").value(5))
                .andExpect(jsonPath("$.body.bodySystems.neuralSystem.agility").value(5))
                .andExpect(jsonPath("$.body.bodySystems.neuralSystem.noeticPlexus").value(0))
                .andExpect(jsonPath("$.body.bodySystems.neuralSystem.phaxicCerebelum").value(0))
                .andExpect(jsonPath("$.body.bodySystems.cardiacSystem.astralVentriculum").value(0))
                .andExpect(jsonPath("$.body.bodySystems.cardiacSystem.astralAtrium").value(0))
                .andExpect(jsonPath("$.body.bodySystems.hormonalGlandularSystem.thyroid").value(5))
                .andExpect(jsonPath("$.body.bodySystems.hormonalGlandularSystem.predominantMorphicHormone").value(5))
                .andExpect(jsonPath("$.body.bodySystems.hormonalGlandularSystem.subtleEpiphysealGland").value(0))
                .andExpect(jsonPath("$.body.bodySystems.digestiveSystem.digestiveAbsorption").value(5))
                .andExpect(jsonPath("$.body.bodySystems.digestiveSystem.ketosisEfficiency").value(5))
                .andExpect(jsonPath("$.body.spatialIntelligence").doesNotExist())
                .andExpect(jsonPath("$.body.physicalTraits.sensorialOrgans.eyesSensitivity").value(5))
                .andExpect(jsonPath("$.body.physicalTraits.bodyStructure.skinThickness").value(3))
                .andExpect(jsonPath("$.body.physicalTraits.bodyStructure.shapeAesthetics").value(5))
                .andExpect(jsonPath("$.body.physicalTraits.trainingAndConditioning.vigor").value(0))
                .andExpect(jsonPath("$.body.physicalTraits.trainingAndConditioning.reflexes").value(0))
                .andExpect(jsonPath("$.body.attributes").doesNotExist())
                .andExpect(jsonPath("$.mind.values.ego").value(1))
                .andExpect(jsonPath("$.mind.values.knowledge").value(1))
                .andExpect(jsonPath("$.mind.erudition.levels.ECOLOGY").value(0))
                .andExpect(jsonPath("$.mind.erudition.points.total").value(2))
                .andExpect(jsonPath("$.mind.personality.selectedTraits").isEmpty())
                .andExpect(jsonPath("$.mind.labours.levels.MASONRY").value(0))
                .andExpect(jsonPath("$.mind.labours.points.total").value(2))
                .andExpect(jsonPath("$.mind.generalPersonality.vanity").value(5))
                .andExpect(jsonPath("$.mind.generalPersonality.focus").value(5))
                .andExpect(jsonPath("$.mind.weaponProficiencies.levels.DAGGERS").value(0))
                .andExpect(jsonPath("$.mind.eventPoints.total").value(20))
                .andExpect(jsonPath("$.calculatedValues.symbolicTotalMass").value(25))
                .andExpect(jsonPath("$.calculatedValues.totalMassKg").exists())
                .andExpect(jsonPath("$.attributes.fatigueResistance").exists())
                .andExpect(jsonPath("$.attributes.staminaRecovery").exists())
                .andExpect(jsonPath("$.attributes.sight").exists())
                .andExpect(jsonPath("$.attributes.evasion").exists())
                .andExpect(jsonPath("$.attributes.acrobatics").exists())
                .andExpect(jsonPath("$.attributes.meleeAccuracy").exists())
                .andExpect(jsonPath("$.attributes.aim").exists())
                .andExpect(jsonPath("$.attributes.memoryPool").doesNotExist())
                .andExpect(jsonPath("$.poolAttributes.memoryPool.total").exists())
                .andExpect(jsonPath("$.poolAttributes.memoryPool.current").exists())
                .andExpect(jsonPath("$.attributes.reasoning").exists())
                .andExpect(jsonPath("$.attributes.shortMemory").exists())
                .andExpect(jsonPath("$.attributes.mentalHealthPool").doesNotExist())
                .andExpect(jsonPath("$.poolAttributes.mentalHealthPool.total").exists())
                .andExpect(jsonPath("$.poolAttributes.mentalHealthPool.current").exists())
                .andExpect(jsonPath("$.attributes.will").exists())
                .andExpect(jsonPath("$.attributes.balance").exists())
                .andExpect(jsonPath("$.attributes.stressResistance").exists())
                .andExpect(jsonPath("$.attributes.poisonResistance").exists())
                .andExpect(jsonPath("$.attributes.diseaseResistance").exists())
                .andExpect(jsonPath("$.attributes.bleedingResistance").exists())
                .andExpect(jsonPath("$.attributes.thermalResistance").exists())
                .andExpect(jsonPath("$.attributes.breathOutput").exists())
                .andExpect(jsonPath("$.attributes.dehydrationResistance").exists())
                .andExpect(jsonPath("$.attributes.starvationResistance").exists())
                .andExpect(jsonPath("$.attributes.foodPoisoningAlcoholResistance").exists())
                .andExpect(jsonPath("$.attributes.manaPool").doesNotExist())
                .andExpect(jsonPath("$.poolAttributes.manaPool.total").value(12.0))
                .andExpect(jsonPath("$.poolAttributes.manaPool.current").value(12.0))
                .andExpect(jsonPath("$.poolAttributes.chiPool.total").value(12.0))
                .andExpect(jsonPath("$.poolAttributes.chiPool.current").value(12.0))
                .andExpect(jsonPath("$.poolAttributes.staminaPool.total").exists())
                .andExpect(jsonPath("$.poolAttributes.staminaPool.current").exists())
                .andExpect(jsonPath("$.attributes.arcaneOutput").value(12.0))
                .andExpect(jsonPath("$.attributes.mediunity").value(12.0))
                .andExpect(jsonPath("$.attributes.sixthSense").doesNotExist())
                .andExpect(jsonPath("$.attributes.selfConcern").value(1.0))
                .andExpect(jsonPath("$.attributes.academicConcern").value(1.0))
                .andExpect(jsonPath("$.attributes.survivalSkills").value(60.0))
                .andExpect(jsonPath("$.attributes.animalCaring").value(60.0))
                .andExpect(jsonPath("$.attributes.manipulation").value(60.0))
                .andExpect(jsonPath("$.attributes.behaviorReading").value(60.0))
                .andExpect(jsonPath("$.attributes.discretion").value(60.0))
                .andExpect(jsonPath("$.attributes.bluffing").value(60.0))
                .andExpect(jsonPath("$.attributes.faith").value(60.0))
                .andExpect(jsonPath("$.attributes.illusionResistance").value(60.0))
                .andExpect(jsonPath("$.attributes.creativity").value(60.0))
                .andExpect(jsonPath("$.attributes.analysis").value(60.0))
                .andExpect(jsonPath("$.attributes.closeCombat").value(60.0))
                .andExpect(jsonPath("$.attributes.lowRangeCombat").value(60.0))
                .andExpect(jsonPath("$.attributes.longRangeCombat").value(60.0))
                .andExpect(jsonPath("$.attributes.psyquismOutput").value(12.0))
                .andExpect(jsonPath("$.attributes.psyquismDefense").value(12.0))
                .andExpect(jsonPath("$.attributes.charmResistance").value(60.0))
                .andExpect(jsonPath("$.attributes.concentration").value(60.0))
                .andExpect(jsonPath("$.attributes.purity").value(60.0))
                .andExpect(jsonPath("$.attributes.reactionSpeed").exists())
                .andExpect(jsonPath("$.attributes.hiding").exists())
                .andExpect(jsonPath("$.attributes.sneaking").exists())
                .andExpect(jsonPath("$.attributes.cardiovascularCapacity").doesNotExist())
                .andExpect(jsonPath("$.attributes.fatigueRate").doesNotExist())
                .andExpect(jsonPath("$.attributes.energyCost").doesNotExist())
                .andExpect(jsonPath("$.loadCapacity.lightLoadKg").exists())
                .andExpect(jsonPath("$.loadCapacity.heavyLoadKg").exists())
                .andExpect(jsonPath("$.loadCapacity.maxCapacityKg").exists())
                .andExpect(jsonPath("$.loadCapacity.dragCapacityKg").exists())
                .andExpect(jsonPath("$.body.geneticPoints.total").value(20))
                .andExpect(jsonPath("$.body.trainingPoints.total").value(20));
    }
}
