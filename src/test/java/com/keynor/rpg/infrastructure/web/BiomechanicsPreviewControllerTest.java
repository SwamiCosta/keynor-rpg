package com.keynor.rpg.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keynor.rpg.application.dto.BiomechanicsPreviewRequest;
import com.keynor.rpg.application.dto.BloodSystemInput;
import com.keynor.rpg.application.dto.BodyCompositionInput;
import com.keynor.rpg.application.dto.BodySystemsInput;
import com.keynor.rpg.application.dto.CardiacSystemInput;
import com.keynor.rpg.application.dto.DigestiveSystemInput;
import com.keynor.rpg.application.dto.GeneticsInput;
import com.keynor.rpg.application.dto.HormonalGlandularSystemInput;
import com.keynor.rpg.application.dto.NeuralSystemInput;
import com.keynor.rpg.application.dto.PhysicalTraitsInput;
import com.keynor.rpg.application.dto.PulmonarySystemInput;
import com.keynor.rpg.application.dto.BodyStructureInput;
import com.keynor.rpg.application.dto.SensorialOrgansInput;
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
                new GeneticsInput(5, 5, 5, 7, 3),
                new BodyCompositionInput(3, 5, 5, 5, 5, 5, 5),
                new BodySystemsInput(new BloodSystemInput(5, 3), new CardiacSystemInput(5, 0),
                        new PulmonarySystemInput(5), new NeuralSystemInput(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0),
                        new HormonalGlandularSystemInput(5, 5, 5, 0), new DigestiveSystemInput(5, 5, 5)),
                new PhysicalTraitsInput(new SensorialOrgansInput(5, 5, 5), new BodyStructureInput(3, 5, 5)));

        when(previewAttributesUseCase.calculate(any(), any(), any()))
                .thenReturn(new PlayableCharacter("preview", Body.humanTemplate()));

        mockMvc.perform(post("/api/v1/biomechanics/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes.pushStrength").exists())
                .andExpect(jsonPath("$.attributes.legDrive").exists())
                .andExpect(jsonPath("$.attributes.gripStrength").exists())
                .andExpect(jsonPath("$.attributes.liftStrength").exists())
                .andExpect(jsonPath("$.attributeBreakdowns.breakdowns.pushStrength").exists())
                .andExpect(jsonPath("$.attributes.speed").exists())
                .andExpect(jsonPath("$.attributes.maxMovementSpeed").exists())
                .andExpect(jsonPath("$.attributes.staminaPool").exists())
                .andExpect(jsonPath("$.attributes.fatigueResistance").exists())
                .andExpect(jsonPath("$.attributes.staminaRecovery").exists())
                .andExpect(jsonPath("$.attributes.durability").exists())
                .andExpect(jsonPath("$.attributes.cardiovascularCapacity").doesNotExist())
                .andExpect(jsonPath("$.attributes.fatigueRate").doesNotExist())
                .andExpect(jsonPath("$.attributes.sight").exists())
                .andExpect(jsonPath("$.attributes.hearing").exists())
                .andExpect(jsonPath("$.attributes.smell").exists())
                .andExpect(jsonPath("$.attributes.evasion").exists())
                .andExpect(jsonPath("$.attributes.acrobatics").exists())
                .andExpect(jsonPath("$.attributes.meleeAccuracy").exists())
                .andExpect(jsonPath("$.attributes.aim").exists())
                .andExpect(jsonPath("$.attributes.memoryPool").exists())
                .andExpect(jsonPath("$.attributes.reasoning").exists())
                .andExpect(jsonPath("$.attributes.shortMemory").exists())
                .andExpect(jsonPath("$.attributes.mentalHealthPool").exists())
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
                .andExpect(jsonPath("$.attributes.fatGainRate").exists())
                .andExpect(jsonPath("$.attributes.muscleGainRate").exists())
                .andExpect(jsonPath("$.attributes.intimidation").exists())
                .andExpect(jsonPath("$.attributes.diplomacy").exists())
                .andExpect(jsonPath("$.attributes.enfactuation").exists())
                .andExpect(jsonPath("$.attributes.command").exists())
                .andExpect(jsonPath("$.attributes.manaPool").exists())
                .andExpect(jsonPath("$.attributes.arcaneOutput").exists())
                .andExpect(jsonPath("$.attributes.sixthSense").exists())
                .andExpect(jsonPath("$.calculatedValues.symbolicTotalMass").exists())
                .andExpect(jsonPath("$.calculatedValues.displayMassKg").exists())
                .andExpect(jsonPath("$.loadCapacity.lightLoadKg").exists())
                .andExpect(jsonPath("$.loadCapacity.heavyLoadKg").exists())
                .andExpect(jsonPath("$.loadCapacity.maxCapacityKg").exists())
                .andExpect(jsonPath("$.loadCapacity.dragCapacityKg").exists());
    }
}
