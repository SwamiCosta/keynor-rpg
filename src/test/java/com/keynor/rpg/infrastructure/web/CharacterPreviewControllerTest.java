package com.keynor.rpg.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keynor.rpg.application.dto.BloodSystemInput;
import com.keynor.rpg.application.dto.BodyCompositionInput;
import com.keynor.rpg.application.dto.BodyPreviewRequest;
import com.keynor.rpg.application.dto.BodySystemsInput;
import com.keynor.rpg.application.dto.CardiacSystemInput;
import com.keynor.rpg.application.dto.CharacterPreviewRequest;
import com.keynor.rpg.application.dto.DigestiveSystemInput;
import com.keynor.rpg.application.dto.EruditionInput;
import com.keynor.rpg.application.dto.GeneralPersonalityInput;
import com.keynor.rpg.application.dto.GeneticsInput;
import com.keynor.rpg.application.dto.HormonalGlandularSystemInput;
import com.keynor.rpg.application.dto.LaboursInput;
import com.keynor.rpg.application.dto.MindPreviewRequest;
import com.keynor.rpg.application.dto.NeuralSystemInput;
import com.keynor.rpg.application.dto.PersonalityInput;
import com.keynor.rpg.application.dto.PhysicalTraitsInput;
import com.keynor.rpg.application.dto.PulmonarySystemInput;
import com.keynor.rpg.application.dto.BodyStructureInput;
import com.keynor.rpg.application.dto.SensorialOrgansInput;
import com.keynor.rpg.application.dto.TrainingAndConditioningInput;
import com.keynor.rpg.application.dto.ValuesInput;
import com.keynor.rpg.application.dto.WeaponProficienciesInput;
import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.Mind;
import com.keynor.rpg.domain.model.PlayableCharacter;
import com.keynor.rpg.domain.port.in.PreviewAttributesUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CharacterPreviewController.class)
class CharacterPreviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PreviewAttributesUseCase previewAttributesUseCase;

    @Test
    void preview_returnsAttributesComputedFromBodyAndMind() throws Exception {
        CharacterPreviewRequest request = new CharacterPreviewRequest(
                new BodyPreviewRequest(
                        new GeneticsInput(5, 5, 5, 7, 3),
                        new BodyCompositionInput(3, 5, 5, 5, 5, 5, 5),
                        new BodySystemsInput(new BloodSystemInput(5, 3), new CardiacSystemInput(5, 0, 0),
                                new PulmonarySystemInput(5),
                                new NeuralSystemInput(5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 0, 0),
                                new HormonalGlandularSystemInput(5, 5, 5, 0), new DigestiveSystemInput(5, 5, 5)),
                        new PhysicalTraitsInput(new SensorialOrgansInput(5, 5, 5), new BodyStructureInput(3, 5, 5),
                                new TrainingAndConditioningInput(0, 0, 0, 0, 0, 0, 0, 0))),
                new MindPreviewRequest(
                        new ValuesInput(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1),
                        new EruditionInput(Map.of("ECOLOGY", 2)),
                        new PersonalityInput(Set.of()),
                        new LaboursInput(Map.of()),
                        new GeneralPersonalityInput(5, 5),
                        new WeaponProficienciesInput(Map.of())));

        when(previewAttributesUseCase.calculate(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new PlayableCharacter("preview", Body.humanTemplate(), Mind.humanTemplate()));

        mockMvc.perform(post("/api/v1/character/preview")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes.pushStrength").exists())
                .andExpect(jsonPath("$.attributes.legDrive").exists())
                .andExpect(jsonPath("$.attributes.gripStrength").exists())
                .andExpect(jsonPath("$.attributes.liftStrength").exists())
                .andExpect(jsonPath("$.attributeBreakdowns.breakdowns.pushStrength").exists())
                .andExpect(jsonPath("$.attributes.speed").exists())
                .andExpect(jsonPath("$.attributes.movementSpeed").exists())
                .andExpect(jsonPath("$.attributes.staminaPool").doesNotExist())
                .andExpect(jsonPath("$.poolAttributes.staminaPool.total").exists())
                .andExpect(jsonPath("$.poolAttributes.staminaPool.current").exists())
                .andExpect(jsonPath("$.attributes.fatigueResistance").exists())
                .andExpect(jsonPath("$.attributes.staminaRecovery").exists())
                .andExpect(jsonPath("$.attributes.durability").doesNotExist())
                .andExpect(jsonPath("$.attributes.softTissueDurability").exists())
                .andExpect(jsonPath("$.attributes.boneDurability").exists())
                .andExpect(jsonPath("$.attributes.cardiovascularCapacity").doesNotExist())
                .andExpect(jsonPath("$.attributes.fatigueRate").doesNotExist())
                .andExpect(jsonPath("$.attributes.sight").exists())
                .andExpect(jsonPath("$.attributes.hearing").exists())
                .andExpect(jsonPath("$.attributes.smell").exists())
                .andExpect(jsonPath("$.attributes.evasion").exists())
                .andExpect(jsonPath("$.attributes.acrobatics").exists())
                .andExpect(jsonPath("$.attributes.meleeDexterity").exists())
                .andExpect(jsonPath("$.attributes.aim").exists())
                .andExpect(jsonPath("$.attributes.memoryPool").doesNotExist())
                .andExpect(jsonPath("$.poolAttributes.memoryPool.total").exists())
                .andExpect(jsonPath("$.attributes.reasoning").exists())
                .andExpect(jsonPath("$.attributes.shortMemory").exists())
                .andExpect(jsonPath("$.attributes.mentalHealthPool").doesNotExist())
                .andExpect(jsonPath("$.poolAttributes.mentalHealthPool.total").exists())
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
                .andExpect(jsonPath("$.attributes.manaPool").doesNotExist())
                .andExpect(jsonPath("$.poolAttributes.manaPool.total").exists())
                .andExpect(jsonPath("$.poolAttributes.chiPool.total").exists())
                .andExpect(jsonPath("$.poolAttributes.chiPool.current").exists())
                .andExpect(jsonPath("$.attributes.arcaneOutput").exists())
                .andExpect(jsonPath("$.attributes.mediunity").exists())
                .andExpect(jsonPath("$.attributes.sixthSense").doesNotExist())
                .andExpect(jsonPath("$.attributes.selfConcern").exists())
                .andExpect(jsonPath("$.attributes.friendshipConcern").exists())
                .andExpect(jsonPath("$.attributes.orderConcern").exists())
                .andExpect(jsonPath("$.attributes.freedomConcern").exists())
                .andExpect(jsonPath("$.attributes.patriotismConcern").exists())
                .andExpect(jsonPath("$.attributes.spiritualConcern").exists())
                .andExpect(jsonPath("$.attributes.philosophyConcern").exists())
                .andExpect(jsonPath("$.attributes.academicConcern").exists())
                .andExpect(jsonPath("$.attributes.environmentalismConcern").exists())
                .andExpect(jsonPath("$.attributes.moralityConcern").exists())
                .andExpect(jsonPath("$.attributes.traditionalismConcern").exists())
                .andExpect(jsonPath("$.attributes.justiceConcern").exists())
                .andExpect(jsonPath("$.attributes.progressConcern").exists())
                .andExpect(jsonPath("$.attributes.peaceConcern").exists())
                .andExpect(jsonPath("$.attributes.survivalSkills").exists())
                .andExpect(jsonPath("$.attributes.animalCaring").exists())
                .andExpect(jsonPath("$.attributes.manipulation").exists())
                .andExpect(jsonPath("$.attributes.behaviorReading").exists())
                .andExpect(jsonPath("$.attributes.discretion").exists())
                .andExpect(jsonPath("$.attributes.bluffing").exists())
                .andExpect(jsonPath("$.attributes.faith").exists())
                .andExpect(jsonPath("$.attributes.illusionResistance").exists())
                .andExpect(jsonPath("$.attributes.illusionResistanceSanity").doesNotExist())
                .andExpect(jsonPath("$.attributes.creativity").exists())
                .andExpect(jsonPath("$.attributes.analysis").exists())
                .andExpect(jsonPath("$.attributes.psyquismOutput").exists())
                .andExpect(jsonPath("$.attributes.psyquismDefense").exists())
                .andExpect(jsonPath("$.attributes.charmResistance").exists())
                .andExpect(jsonPath("$.attributes.concentration").exists())
                .andExpect(jsonPath("$.attributes.purity").exists())
                .andExpect(jsonPath("$.attributes.cognitiveSpeed").exists())
                .andExpect(jsonPath("$.attributes.hiding").exists())
                .andExpect(jsonPath("$.attributes.sneaking").exists())
                .andExpect(jsonPath("$.attributes.alchemy").exists())
                .andExpect(jsonPath("$.attributes.machineHandling").exists())
                .andExpect(jsonPath("$.attributes.performance").exists())
                .andExpect(jsonPath("$.attributes.sciencePractice").exists())
                .andExpect(jsonPath("$.attributes.healing").exists())
                .andExpect(jsonPath("$.attributes.hackingAndPrograming").exists())
                .andExpect(jsonPath("$.calculatedValues.symbolicTotalMass").exists())
                .andExpect(jsonPath("$.calculatedValues.totalMassKg").exists())
                .andExpect(jsonPath("$.calculatedValues.displayMassKg").doesNotExist())
                .andExpect(jsonPath("$.loadCapacity.lightLoadKg").exists())
                .andExpect(jsonPath("$.loadCapacity.heavyLoadKg").exists())
                .andExpect(jsonPath("$.loadCapacity.maxCapacityKg").exists())
                .andExpect(jsonPath("$.loadCapacity.dragCapacityKg").exists());
    }
}
