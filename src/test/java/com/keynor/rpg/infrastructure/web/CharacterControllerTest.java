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
                .andExpect(jsonPath("$.body.biomechanics.genetics.height").value(170.0))
                .andExpect(jsonPath("$.body.biomechanics.bodyComposition.bodyFat").value(14.0))
                .andExpect(jsonPath("$.body.bodySystems.nervousSystem.neuralDrive").value(5.0))
                .andExpect(jsonPath("$.body.spatialIntelligence.perception").value(5.0))
                .andExpect(jsonPath("$.body.calculatedValues.totalMass").exists())
                .andExpect(jsonPath("$.body.calculatedValues.boneMass").exists())
                .andExpect(jsonPath("$.body.calculatedValues.organWaterMass").exists())
                .andExpect(jsonPath("$.body.attributes.fatigueRate").exists())
                .andExpect(jsonPath("$.body.attributes.sight").exists())
                .andExpect(jsonPath("$.body.attributes.evasion").exists())
                .andExpect(jsonPath("$.body.attributes.acrobatics").exists())
                .andExpect(jsonPath("$.body.attributes.meleeAccuracy").exists())
                .andExpect(jsonPath("$.body.attributes.aim").exists())
                .andExpect(jsonPath("$.body.attributes.energyCost").doesNotExist())
                .andExpect(jsonPath("$.body.geneticPoints.total").value(20))
                .andExpect(jsonPath("$.body.trainingPoints.total").value(20));
    }
}
