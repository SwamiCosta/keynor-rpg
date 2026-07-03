package com.keynor.rpg.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keynor.rpg.application.dto.BiomechanicsPreviewRequest;
import com.keynor.rpg.application.dto.BloodSystemInput;
import com.keynor.rpg.application.dto.BodyCompositionInput;
import com.keynor.rpg.application.dto.BodySystemsInput;
import com.keynor.rpg.application.dto.CardiacSystemInput;
import com.keynor.rpg.application.dto.DigestiveSystemInput;
import com.keynor.rpg.application.dto.GeneticsInput;
import com.keynor.rpg.application.dto.HormonalSystemInput;
import com.keynor.rpg.application.dto.NeuralSystemInput;
import com.keynor.rpg.application.dto.PhysicalTraitsInput;
import com.keynor.rpg.application.dto.PulmonarySystemInput;
import com.keynor.rpg.application.dto.BodyStructureInput;
import com.keynor.rpg.application.dto.SensorialOrgansInput;
import com.keynor.rpg.domain.model.Body;
import com.keynor.rpg.domain.model.PlayableCharacter;
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
                new GeneticsInput(5, 5, 5, 7, 3),
                new BodyCompositionInput(3, 5, 5, 5, 5, 5, 5),
                new BodySystemsInput(new BloodSystemInput(5, 3), new CardiacSystemInput(5),
                        new PulmonarySystemInput(5), new NeuralSystemInput(5, 5, 5, 5, 5, 5, 5, 5, 5, 5),
                        new HormonalSystemInput(5, 5, 5), new DigestiveSystemInput(5, 5, 5)),
                new PhysicalTraitsInput(new SensorialOrgansInput(5, 5, 5), new BodyStructureInput(3, 5, 5)));

        when(previewAttributesUseCase.calculate(any(), any(), any()))
                .thenReturn(new PlayableCharacter("preview", Body.humanTemplate()));

        mockMvc.perform(post("/api/v1/biomechanics/preview")
                        .header(HttpHeaders.ORIGIN, ALLOWED_ORIGIN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, ALLOWED_ORIGIN));
    }
}
