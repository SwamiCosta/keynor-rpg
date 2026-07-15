package com.keynor.rpg.infrastructure.web;

import com.keynor.rpg.domain.model.CombatActionTimeResult;
import com.keynor.rpg.domain.model.CombatActionType;
import com.keynor.rpg.domain.model.CombatAttributeInputs;
import com.keynor.rpg.domain.port.in.CalculateCombatActionTimeUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CombatActionTimeController.class)
class CombatActionTimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalculateCombatActionTimeUseCase calculateCombatActionTimeUseCase;

    @Test
    void actionTime_walk1mWithSpeed_returnsUtAndScore() throws Exception {
        when(calculateCombatActionTimeUseCase.calculate(eq(CombatActionType.WALK_1M), any(CombatAttributeInputs.class)))
                .thenReturn(new CombatActionTimeResult(5, 60.0));

        mockMvc.perform(post("/api/v1/combat/action-time")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"action\":\"WALK_1M\",\"speed\":60}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ut").value(5))
                .andExpect(jsonPath("$.score").value(60.0));
    }

    @Test
    void actionTime_missingRequiredAttribute_propagatesError() {
        when(calculateCombatActionTimeUseCase.calculate(eq(CombatActionType.WALK_1M), any(CombatAttributeInputs.class)))
                .thenThrow(new IllegalArgumentException("Combat action WALK_1M requires 'speed' to be provided"));

        Exception thrown = assertThrows(Exception.class, () -> mockMvc.perform(post("/api/v1/combat/action-time")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"action\":\"WALK_1M\"}")));

        assertInstanceOf(IllegalArgumentException.class, thrown.getCause());
    }
}
