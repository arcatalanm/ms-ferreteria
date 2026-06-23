package ferrefix.ms_marcas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_marcas.dto.MarcaRequestDTO;
import ferrefix.ms_marcas.dto.MarcaResponseDTO;
import ferrefix.ms_marcas.exception.GlobalExceptionHandler;
import ferrefix.ms_marcas.service.MarcaService;
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

@ExtendWith(MockitoExtension.class)
class MarcaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MarcaService marcaService;

    @InjectMocks
    private MarcaController marcaController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MarcaResponseDTO marcaBosch;
    private MarcaResponseDTO marcaStanley;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(marcaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        marcaBosch = MarcaResponseDTO.builder()
                .idMarca(1)
                .nombreMarca("Bosch")
                .build();

        marcaStanley = MarcaResponseDTO.builder()
                .idMarca(2)
                .nombreMarca("Stanley")
                .build();
    }

    @Test
    @DisplayName("Debería listar todas las marcas")
    void deberiaListarTodasLasMarcas() throws Exception {

        when(marcaService.listarTodas()).thenReturn(List.of(marcaBosch, marcaStanley));

        mockMvc.perform(get("/api/marcas"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idMarca", is(1)))
                .andExpect(jsonPath("$.content[0].nombreMarca", is("Bosch")))
                .andExpect(jsonPath("$.content[1].idMarca", is(2)))
                .andExpect(jsonPath("$.content[1].nombreMarca", is("Stanley")));

        verify(marcaService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Debería obtener marca por ID")
    void deberiaObtenerMarcaPorId() throws Exception {

        when(marcaService.obtenerPorId(1)).thenReturn(marcaBosch);

        mockMvc.perform(get("/api/marcas/1"))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.idMarca", is(1)))
                .andExpect(jsonPath("$.nombreMarca", is("Bosch")));

        verify(marcaService, times(1)).obtenerPorId(1);
    }

    @Test
    @DisplayName("Debería crear una marca")
    void deberiaCrearMarca() throws Exception {

        MarcaRequestDTO request = MarcaRequestDTO.builder()
                .nombreMarca("Bosch")
                .build();

        when(marcaService.crear(any(MarcaRequestDTO.class))).thenReturn(marcaBosch);

        mockMvc.perform(post("/api/marcas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMarca", is(1)))
                .andExpect(jsonPath("$.nombreMarca", is("Bosch")));

        verify(marcaService, times(1)).crear(any(MarcaRequestDTO.class));
    }

    @Test
    @DisplayName("Debería actualizar una marca")
    void deberiaActualizarMarca() throws Exception {

        MarcaRequestDTO request = MarcaRequestDTO.builder()
                .nombreMarca("Bosch Professional")
                .build();

        MarcaResponseDTO actualizada = MarcaResponseDTO.builder()
                .idMarca(1)
                .nombreMarca("Bosch Professional")
                .build();

        when(marcaService.actualizar(eq(1), any(MarcaRequestDTO.class))).thenReturn(actualizada);

        mockMvc.perform(put("/api/marcas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMarca", is(1)))
                .andExpect(jsonPath("$.nombreMarca", is("Bosch Professional")));

        verify(marcaService, times(1)).actualizar(eq(1), any(MarcaRequestDTO.class));
    }

    @Test
    @DisplayName("Debería eliminar una marca")
    void deberiaEliminarMarca() throws Exception {

        doNothing().when(marcaService).eliminar(1);

        mockMvc.perform(delete("/api/marcas/1"))
                .andExpect(status().isNoContent());

        verify(marcaService, times(1)).eliminar(1);
    }
}
