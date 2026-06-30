package com.keynor.rpg.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keynor.rpg.application.dto.BiomechanicsPreviewRequest;
import com.keynor.rpg.application.dto.BloodSystemInput;
import com.keynor.rpg.application.dto.BodyCompositionInput;
import com.keynor.rpg.application.dto.BodySystemsInput;
import com.keynor.rpg.application.dto.CardiacSystemInput;
import com.keynor.rpg.application.dto.GeneticsInput;
import com.keynor.rpg.application.dto.NervousSystemInput;
import com.keynor.rpg.application.dto.PulmonarySystemInput;
import com.keynor.rpg.application.dto.SpatialIntelligenceInput;
import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.port.in.PreviewAttributesUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BiomechanicsPreviewController.class)
class BiomechanicsPreviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PreviewAttributesUseCase previewAttributesUseCase;

    @Test
    void preview_returnsAttributesComputedFromRequestBody() throws Exception {
        BiomechanicsPreviewRequest request = new BiomechanicsPreviewRequest(
                new GeneticsInput(5, 5, 5, 170, 1.0, 5),
                new BodyCompositionInput(14, 30, 0.0, 5.0, 5.0),
                new BodySystemsInput(new BloodSystemInput(5), new CardiacSystemInput(5),
                        new PulmonarySystemInput(5), new NervousSystemInput(5, 0.5)),
                new SpatialIntelligenceInput(5, 5, 5));

        when(previewAttributesUseCase.calculate(any(), any(), any()))
                .thenReturn(new PlayableCharacter("preview", Body.humanTemplate()));

        mockMvc.perform(post("/api/v1/biomechanics/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes.strength").exists())
                .andExpect(jsonPath("$.attributes.speed").exists())
                .andExpect(jsonPath("$.attributes.maxMovementSpeed").exists())
                .andExpect(jsonPath("$.attributes.staminaPool").exists())
                .andExpect(jsonPath("$.attributes.durability").exists())
                .andExpect(jsonPath("$.attributes.cardiovascularCapacity").exists())
                .andExpect(jsonPath("$.attributes.fatigueRate").exists())
                .andExpect(jsonPath("$.attributes.sight").exists())
                .andExpect(jsonPath("$.attributes.hearing").exists())
                .andExpect(jsonPath("$.attributes.smell").exists())
                .andExpect(jsonPath("$.attributes.evasion").exists())
                .andExpect(jsonPath("$.attributes.acrobatics").exists())
                .andExpect(jsonPath("$.attributes.meleeAccuracy").exists())
                .andExpect(jsonPath("$.attributes.aim").exists())
                .andExpect(jsonPath("$.calculatedValues.totalMass").exists())
                .andExpect(jsonPath("$.calculatedValues.boneMass").exists())
                .andExpect(jsonPath("$.calculatedValues.organWaterMass").exists());
    }
}
