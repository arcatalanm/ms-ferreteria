package ferrefix.ms_sugerencia.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_sugerencia.dto.SugerenciaRequestDTO;
import ferrefix.ms_sugerencia.dto.SugerenciaResponseDTO;
import ferrefix.ms_sugerencia.exception.GlobalExceptionHandler;
import ferrefix.ms_sugerencia.service.SugerenciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ┌──────────────────────────────────────────────────────────┐
 * │                 SugerenciaControllerTest                 │
 * ├──────────────────────────────────────────────────────────┤
 * │  Mocks the MVC layer to test SugerenciaController        │
 * │                                                          │
 * │  [MockMvc] ────► [SugerenciaController]                  │
 * │                           │ (delegates)                  │
 * │                           ▼                              │
 * │              [SugerenciaService] (Mock)                  │
 * └──────────────────────────────────────────────────────────┘
 */
@ExtendWith(MockitoExtension.class)
class SugerenciaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SugerenciaService sugerenciaService;

    @InjectMocks
    private SugerenciaController sugerenciaController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private SugerenciaResponseDTO response1;
    private SugerenciaResponseDTO response2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sugerenciaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        response1 = SugerenciaResponseDTO.builder()
                .idSugerencia(1L)
                .contenidoMensaje("Excelente atención.")
                .fechaIngreso(LocalDateTime.now())
                .build();

        response2 = SugerenciaResponseDTO.builder()
                .idSugerencia(2L)
                .contenidoMensaje("Mejorar stock de herramientas.")
                .fechaIngreso(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Debería crear una sugerencia")
    void deberiaCrear() throws Exception {
        /*
         *  FLOW CHART:
         *  ┌─────────────────────────┐
         *  │ ARRANGE: Mock & Request │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ACT: POST /sugerencias  │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ASSERT: HTTP 201 + JSON │
         *  └─────────────────────────┘
         *
         *  JSON STRUCTURE:
         *  Request DTO:
         *  └── SugerenciaRequestDTO
         *      └── contenidoMensaje: "Excelente atención." (String)
         *
         *  Response DTO (Expected):
         *  └── SugerenciaResponseDTO
         *      ├── idSugerencia: 1 (Long)
         *      └── contenidoMensaje: "Excelente atención." (String)
         */

        // === ARRANGE ===
        SugerenciaRequestDTO request = SugerenciaRequestDTO.builder()
                .contenidoMensaje("Excelente atención.")
                .build();

        when(sugerenciaService.crear(any(SugerenciaRequestDTO.class))).thenReturn(response1);

        // === ACT & ASSERT ===
        mockMvc.perform(post("/api/sugerencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idSugerencia", is(1)))
                .andExpect(jsonPath("$.contenidoMensaje", is("Excelente atención.")));

        verify(sugerenciaService, times(1)).crear(any(SugerenciaRequestDTO.class));
    }

    @Test
    @DisplayName("Debería listar todas las sugerencias")
    void deberiaListarTodas() throws Exception {
        /*
         *  FLOW CHART:
         *  ┌─────────────────────────┐
         *  │ ARRANGE: Mock service   │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ACT: GET /sugerencias   │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ASSERT: HTTP 200 + LIST │
         *  └─────────────────────────┘
         *
         *  JSON STRUCTURE (Expected):
         *  └── Response (List wrapper)
         *      └── content [Array]
         *          ├── [0]: { idSugerencia: 1, contenidoMensaje: "Excelente atención.", ... }
         *          └── [1]: { idSugerencia: 2, contenidoMensaje: "Mejorar stock de herramientas.", ... }
         */

        // === ARRANGE ===
        when(sugerenciaService.listarTodas()).thenReturn(List.of(response1, response2));

        // === ACT & ASSERT ===
        mockMvc.perform(get("/api/sugerencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idSugerencia", is(1)))
                .andExpect(jsonPath("$.content[0].contenidoMensaje", is("Excelente atención.")))
                .andExpect(jsonPath("$.content[1].idSugerencia", is(2)))
                .andExpect(jsonPath("$.content[1].contenidoMensaje", is("Mejorar stock de herramientas.")));

        verify(sugerenciaService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Debería eliminar una sugerencia")
    void deberiaEliminar() throws Exception {
        /*
         *  FLOW CHART:
         *  ┌───────────────────────────┐
         *  │ ARRANGE: Mock doNothing() │
         *  └─────────────┬─────────────┘
         *                │
         *                ▼
         *  ┌───────────────────────────┐
         *  │ ACT: DELETE /sugerencias/1│
         *  └─────────────┬─────────────┘
         *                │
         *                ▼
         *  ┌───────────────────────────┐
         *  │ ASSERT: HTTP 204 NoContent│
         *  └───────────────────────────┘
         */

        // === ARRANGE ===
        doNothing().when(sugerenciaService).eliminar(1L);

        // === ACT & ASSERT ===
        mockMvc.perform(delete("/api/sugerencias/1"))
                .andExpect(status().isNoContent());

        verify(sugerenciaService, times(1)).eliminar(1L);
    }
}
