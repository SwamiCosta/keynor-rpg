package com.keynor.rpg.infrastructure.web;

import com.keynor.rpg.domain.model.Body;
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
        PlayableCharacter character = new PlayableCharacter("Keynor", Body.humanTemplate());
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
                .andExpect(jsonPath("$.body.bodySystems.neuralSystem.agility").value(5))
                .andExpect(jsonPath("$.body.bodySystems.hormonalSystem.thyroid").value(5))
                .andExpect(jsonPath("$.body.bodySystems.hormonalSystem.predominantMorphicHormone").value(5))
                .andExpect(jsonPath("$.body.bodySystems.digestiveSystem.nutrientAbsorption").value(5))
                .andExpect(jsonPath("$.body.spatialIntelligence").doesNotExist())
                .andExpect(jsonPath("$.body.physicalTraits.sensorialOrgans.eyesSensitivity").value(5))
                .andExpect(jsonPath("$.body.physicalTraits.bodyStructure.skinThickness").value(3))
                .andExpect(jsonPath("$.body.physicalTraits.bodyStructure.shapeAesthetics").value(5))
                .andExpect(jsonPath("$.body.calculatedValues.symbolicTotalMass").value(25))
                .andExpect(jsonPath("$.body.calculatedValues.displayMassKg").exists())
                .andExpect(jsonPath("$.body.attributes.fatigueResistance").exists())
                .andExpect(jsonPath("$.body.attributes.staminaRecovery").exists())
                .andExpect(jsonPath("$.body.attributes.sight").exists())
                .andExpect(jsonPath("$.body.attributes.evasion").exists())
                .andExpect(jsonPath("$.body.attributes.acrobatics").exists())
                .andExpect(jsonPath("$.body.attributes.meleeAccuracy").exists())
                .andExpect(jsonPath("$.body.attributes.aim").exists())
                .andExpect(jsonPath("$.body.attributes.memoryPool").exists())
                .andExpect(jsonPath("$.body.attributes.reasoning").exists())
                .andExpect(jsonPath("$.body.attributes.shortMemory").exists())
                .andExpect(jsonPath("$.body.attributes.mentalHealthPool").exists())
                .andExpect(jsonPath("$.body.attributes.will").exists())
                .andExpect(jsonPath("$.body.attributes.balance").exists())
                .andExpect(jsonPath("$.body.attributes.stressResistance").exists())
                .andExpect(jsonPath("$.body.attributes.poisonResistance").exists())
                .andExpect(jsonPath("$.body.attributes.diseaseResistance").exists())
                .andExpect(jsonPath("$.body.attributes.bleedingResistance").exists())
                .andExpect(jsonPath("$.body.attributes.thermalResistance").exists())
                .andExpect(jsonPath("$.body.attributes.breathOutput").exists())
                .andExpect(jsonPath("$.body.attributes.dehydrationResistance").exists())
                .andExpect(jsonPath("$.body.attributes.starvationResistance").exists())
                .andExpect(jsonPath("$.body.attributes.foodPoisoningAlcoholResistance").exists())
                .andExpect(jsonPath("$.body.attributes.cardiovascularCapacity").doesNotExist())
                .andExpect(jsonPath("$.body.attributes.fatigueRate").doesNotExist())
                .andExpect(jsonPath("$.body.attributes.energyCost").doesNotExist())
                .andExpect(jsonPath("$.body.loadCapacity.lightLoadKg").exists())
                .andExpect(jsonPath("$.body.loadCapacity.heavyLoadKg").exists())
                .andExpect(jsonPath("$.body.loadCapacity.maxCapacityKg").exists())
                .andExpect(jsonPath("$.body.loadCapacity.dragCapacityKg").exists())
                .andExpect(jsonPath("$.body.geneticPoints.total").value(20))
                .andExpect(jsonPath("$.body.trainingPoints.total").value(20));
    }
}
