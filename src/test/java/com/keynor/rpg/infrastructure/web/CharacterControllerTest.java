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
    void getById_returnsCharacterSheetWithBiomechanics() throws Exception {
        PlayableCharacter character = new PlayableCharacter("Keynor", Body.humanTemplate());
        when(getPlayableCharacterUseCase.getById("keynor-1")).thenReturn(character);

        mockMvc.perform(get("/api/v1/characters/keynor-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("keynor-1"))
                .andExpect(jsonPath("$.name").value("Keynor"))
                .andExpect(jsonPath("$.body.biomechanics.genetics.height").value(170.0))
                .andExpect(jsonPath("$.body.biomechanics.bodyComposition.bodyFat").value(14.0))
                .andExpect(jsonPath("$.body.biomechanics.calculatedValues.totalMass").exists())
                .andExpect(jsonPath("$.body.biomechanics.calculatedValues.boneMass").exists())
                .andExpect(jsonPath("$.body.biomechanics.calculatedValues.organWaterMass").exists())
                .andExpect(jsonPath("$.body.biomechanics.attributes.fatigueRate").exists())
                .andExpect(jsonPath("$.body.biomechanics.attributes.energyCost").doesNotExist())
                .andExpect(jsonPath("$.body.biomechanics.geneticPoints.total").value(20))
                .andExpect(jsonPath("$.body.biomechanics.trainingPoints.total").value(20));
    }
}
