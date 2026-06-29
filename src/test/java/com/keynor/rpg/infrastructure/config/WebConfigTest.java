package com.keynor.rpg.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keynor.rpg.application.dto.BiomechanicsPreviewRequest;
import com.keynor.rpg.application.dto.BloodSystemInput;
import com.keynor.rpg.application.dto.BodyCompositionInput;
import com.keynor.rpg.application.dto.CardiacSystemInput;
import com.keynor.rpg.application.dto.GeneticsInput;
import com.keynor.rpg.application.dto.NervousSystemInput;
import com.keynor.rpg.application.dto.PulmonarySystemInput;
import com.keynor.rpg.domain.model.Biomechanics;
import com.keynor.rpg.domain.port.in.PreviewAttributesUseCase;
import com.keynor.rpg.infrastructure.web.BiomechanicsPreviewController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BiomechanicsPreviewController.class)
class WebConfigTest {

    private static final String ALLOWED_ORIGIN = "http://localhost:5173";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PreviewAttributesUseCase previewAttributesUseCase;

    @Test
    void preflight_fromAllowedOrigin_isAccepted() throws Exception {
        mockMvc.perform(options("/api/v1/biomechanics/preview")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN));
    }

    @Test
    void actualRequest_fromAllowedOrigin_carriesCorsHeader() throws Exception {
        BiomechanicsPreviewRequest request = new BiomechanicsPreviewRequest(
                new GeneticsInput(5, 5, 5, 170, 1.0, 5),
                new BodyCompositionInput(14, 30, 0.0, 0.5),
                new BloodSystemInput(5),
                new CardiacSystemInput(5),
                new PulmonarySystemInput(5),
                new NervousSystemInput(5));

        when(previewAttributesUseCase.calculate(any(), any(), any(), any(), any(), any()))
                .thenReturn(Biomechanics.humanDefaults());

        mockMvc.perform(post("/api/v1/biomechanics/preview")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN));
    }
}
