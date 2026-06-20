package ferrefix.ms_inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_inventario.dto.UnidadMedidaRequestDTO;
import ferrefix.ms_inventario.dto.UnidadMedidaResponseDTO;
import ferrefix.ms_inventario.exception.GlobalExceptionHandler;
import ferrefix.ms_inventario.service.UnidadMedidaService;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ┌──────────────────────────────────────────────────────────┐
 * │              UnidadMedidaControllerTest                  │
 * ├──────────────────────────────────────────────────────────┤
 * │  Mocks the MVC layer to test UnidadMedidaController       │
 * │                                                          │
 * │  [MockMvc] ────► [UnidadMedidaController]                │
 * │                           │ (delegates)                  │
 * │                           ▼                              │
 * │              [UnidadMedidaService] (Mock)                │
 * └──────────────────────────────────────────────────────────┘
 */
@ExtendWith(MockitoExtension.class)
class UnidadMedidaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UnidadMedidaService unidadMedidaService;

    @InjectMocks
    private UnidadMedidaController unidadMedidaController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UnidadMedidaResponseDTO u1;
    private UnidadMedidaResponseDTO u2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(unidadMedidaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        u1 = UnidadMedidaResponseDTO.builder()
                .idUnidadMedida(1)
                .nombreUnidadMedida("Unidad")
                .build();

        u2 = UnidadMedidaResponseDTO.builder()
                .idUnidadMedida(2)
                .nombreUnidadMedida("Metros")
                .build();
    }

    @Test
    @DisplayName("Debería crear una unidad de medida")
    void deberiaCrear() throws Exception {
        /*
         *  FLOW CHART:
         *  ┌─────────────────────────┐
         *  │ ARRANGE: Mock & Request │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────────┐
         *  │ ACT: POST /unidades_medida  │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ASSERT: HTTP 201 + JSON │
         *  └─────────────────────────┘
         *
         *  JSON STRUCTURE:
         *  Request DTO:
         *  └── UnidadMedidaRequestDTO
         *      └── nombreUnidadMedida: "Unidad" (String)
         *
         *  Response DTO (Expected):
         *  └── UnidadMedidaResponseDTO
         *      ├── idUnidadMedida: 1 (Integer)
         *      └── nombreUnidadMedida: "Unidad" (String)
         */

        // === ARRANGE ===
        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Unidad")
                .build();

        when(unidadMedidaService.crearUnidadMedida(any(UnidadMedidaRequestDTO.class))).thenReturn(u1);

        // === ACT & ASSERT ===
        mockMvc.perform(post("/api/inventario/unidades_medida")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUnidadMedida", is(1)))
                .andExpect(jsonPath("$.nombreUnidadMedida", is("Unidad")));

        verify(unidadMedidaService, times(1)).crearUnidadMedida(any(UnidadMedidaRequestDTO.class));
    }

    @Test
    @DisplayName("Debería listar todas las unidades de medida")
    void deberiaListarTodas() throws Exception {
        /*
         *  FLOW CHART:
         *  ┌─────────────────────────┐
         *  │ ARRANGE: Mock service   │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────────┐
         *  │ ACT: GET /unidades_medida   │
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
         *          ├── [0]: { idUnidadMedida: 1, nombreUnidadMedida: "Unidad" }
         *          └── [1]: { idUnidadMedida: 2, nombreUnidadMedida: "Metros" }
         */

        // === ARRANGE ===
        when(unidadMedidaService.buscarTodasUnidadesMedida()).thenReturn(List.of(u1, u2));

        // === ACT & ASSERT ===
        mockMvc.perform(get("/api/inventario/unidades_medida"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idUnidadMedida", is(1)))
                .andExpect(jsonPath("$.content[0].nombreUnidadMedida", is("Unidad")))
                .andExpect(jsonPath("$.content[1].idUnidadMedida", is(2)))
                .andExpect(jsonPath("$.content[1].nombreUnidadMedida", is("Metros")));

        verify(unidadMedidaService, times(1)).buscarTodasUnidadesMedida();
    }

    @Test
    @DisplayName("Debería obtener unidad de medida por ID")
    void deberiaObtenerPorId() throws Exception {
        /*
         *  FLOW CHART:
         *  ┌─────────────────────────┐
         *  │ ARRANGE: Mock service   │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌───────────────────────────────┐
         *  │ ACT: GET /unidades_medida/1   │
         *  └────────────┬───────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ASSERT: HTTP 200 + JSON │
         *  └─────────────────────────┘
         *
         *  JSON STRUCTURE (Expected):
         *  └── UnidadMedidaResponseDTO
         *      ├── idUnidadMedida: 1 (Integer)
         *      └── nombreUnidadMedida: "Unidad" (String)
         */

        // === ARRANGE ===
        when(unidadMedidaService.buscarUnidadMedidaPorId(1)).thenReturn(u1);

        // === ACT & ASSERT ===
        mockMvc.perform(get("/api/inventario/unidades_medida/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUnidadMedida", is(1)))
                .andExpect(jsonPath("$.nombreUnidadMedida", is("Unidad")));

        verify(unidadMedidaService, times(1)).buscarUnidadMedidaPorId(1);
    }

    @Test
    @DisplayName("Debería actualizar una unidad de medida")
    void deberiaActualizar() throws Exception {
        /*
         *  FLOW CHART:
         *  ┌─────────────────────────┐
         *  │ ARRANGE: Mock & Request │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌───────────────────────────────┐
         *  │ ACT: PUT /unidades_medida/1   │
         *  └────────────┬───────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ASSERT: HTTP 200 + JSON │
         *  └─────────────────────────┘
         *
         *  JSON STRUCTURE:
         *  Request DTO:
         *  └── UnidadMedidaRequestDTO
         *      └── nombreUnidadMedida: "Unidades" (String)
         *
         *  Response DTO (Expected):
         *  └── UnidadMedidaResponseDTO
         *      ├── idUnidadMedida: 1 (Integer)
         *      └── nombreUnidadMedida: "Unidades" (String)
         */

        // === ARRANGE ===
        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Unidades")
                .build();

        UnidadMedidaResponseDTO actualizada = UnidadMedidaResponseDTO.builder()
                .idUnidadMedida(1)
                .nombreUnidadMedida("Unidades")
                .build();

        when(unidadMedidaService.actualizarUnidadMedida(eq(1), any(UnidadMedidaRequestDTO.class))).thenReturn(actualizada);

        // === ACT & ASSERT ===
        mockMvc.perform(put("/api/inventario/unidades_medida/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUnidadMedida", is(1)))
                .andExpect(jsonPath("$.nombreUnidadMedida", is("Unidades")));

        verify(unidadMedidaService, times(1)).actualizarUnidadMedida(eq(1), any(UnidadMedidaRequestDTO.class));
    }

    @Test
    @DisplayName("Debería eliminar una unidad de medida")
    void deberiaEliminar() throws Exception {
        /*
         *  FLOW CHART:
         *  ┌───────────────────────────┐
         *  │ ARRANGE: Mock doNothing() │
         *  └─────────────┬─────────────┘
         *                │
         *                ▼
         *  ┌──────────────────────────────────┐
         *  │ ACT: DELETE /unidades_medida/1   │
         *  └─────────────┬──────────────────┘
         *                │
         *                ▼
         *  ┌───────────────────────────┐
         *  │ ASSERT: HTTP 204 NoContent│
         *  └───────────────────────────┘
         */

        // === ARRANGE ===
        doNothing().when(unidadMedidaService).eliminarUnidadMedida(1);

        // === ACT & ASSERT ===
        mockMvc.perform(delete("/api/inventario/unidades_medida/1"))
                .andExpect(status().isNoContent());

        verify(unidadMedidaService, times(1)).eliminarUnidadMedida(1);
    }
}
