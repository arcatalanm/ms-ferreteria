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
import org.mockito.Spy;
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

@ExtendWith(MockitoExtension.class)
class UnidadMedidaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UnidadMedidaService unidadMedidaService;

    @Spy
    private ferrefix.ms_inventario.assembler.UnidadMedidaAssembler unidadMedidaAssembler;

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

        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Unidad")
                .build();

        when(unidadMedidaService.crearUnidadMedida(any(UnidadMedidaRequestDTO.class))).thenReturn(u1);

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

        when(unidadMedidaService.buscarTodasUnidadesMedida()).thenReturn(List.of(u1, u2));

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

        when(unidadMedidaService.buscarUnidadMedidaPorId(1)).thenReturn(u1);

        mockMvc.perform(get("/api/inventario/unidades_medida/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUnidadMedida", is(1)))
                .andExpect(jsonPath("$.nombreUnidadMedida", is("Unidad")));

        verify(unidadMedidaService, times(1)).buscarUnidadMedidaPorId(1);
    }

    @Test
    @DisplayName("Debería actualizar una unidad de medida")
    void deberiaActualizar() throws Exception {

        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Unidades")
                .build();

        UnidadMedidaResponseDTO actualizada = UnidadMedidaResponseDTO.builder()
                .idUnidadMedida(1)
                .nombreUnidadMedida("Unidades")
                .build();

        when(unidadMedidaService.actualizarUnidadMedida(eq(1), any(UnidadMedidaRequestDTO.class))).thenReturn(actualizada);

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

        doNothing().when(unidadMedidaService).eliminarUnidadMedida(1);

        mockMvc.perform(delete("/api/inventario/unidades_medida/1"))
                .andExpect(status().isNoContent());

        verify(unidadMedidaService, times(1)).eliminarUnidadMedida(1);
    }
}
