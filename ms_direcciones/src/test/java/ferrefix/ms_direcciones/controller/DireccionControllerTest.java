package ferrefix.ms_direcciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_direcciones.dto.DireccionRequestDTO;
import ferrefix.ms_direcciones.dto.DireccionResponseDTO;
import ferrefix.ms_direcciones.exception.GlobalExceptionHandler;
import ferrefix.ms_direcciones.service.DireccionService;
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
class DireccionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DireccionService direccionService;

    @Spy
    private ferrefix.ms_direcciones.assembler.DireccionAssembler direccionAssembler;

    @InjectMocks
    private DireccionController direccionController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DireccionResponseDTO response1;
    private DireccionResponseDTO response2;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(direccionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        response1 = DireccionResponseDTO.builder()
                .idDireccion(1L)
                .calle("Av. Providencia")
                .numero(1234)
                .departamento("501")
                .comuna("Providencia")
                .ciudad("Santiago")
                .direccionCompleta("Av. Providencia 1234, Depto 501, Providencia, Santiago")
                .build();

        response2 = DireccionResponseDTO.builder()
                .idDireccion(2L)
                .calle("Alameda")
                .numero(345)
                .comuna("Santiago")
                .ciudad("Santiago")
                .direccionCompleta("Alameda 345, Santiago, Santiago")
                .build();
    }

    @Test
    @DisplayName("Debería crear una dirección")
    void deberiaCrearDireccion() throws Exception {

        DireccionRequestDTO request = DireccionRequestDTO.builder()
                .calle("Av. Providencia")
                .numero(1234)
                .departamento("501")
                .comuna("Providencia")
                .ciudad("Santiago")
                .build();

        when(direccionService.crearDireccion(any(DireccionRequestDTO.class))).thenReturn(response1);

        mockMvc.perform(post("/api/direcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idDireccion", is(1)))
                .andExpect(jsonPath("$.calle", is("Av. Providencia")))
                .andExpect(jsonPath("$.direccionCompleta", is("Av. Providencia 1234, Depto 501, Providencia, Santiago")));

        verify(direccionService, times(1)).crearDireccion(any(DireccionRequestDTO.class));
    }

    @Test
    @DisplayName("Debería listar todas las direcciones")
    void deberiaListarTodas() throws Exception {

        when(direccionService.buscarTodas()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/api/direcciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idDireccion", is(1)))
                .andExpect(jsonPath("$.content[0].calle", is("Av. Providencia")))
                .andExpect(jsonPath("$.content[1].idDireccion", is(2)))
                .andExpect(jsonPath("$.content[1].calle", is("Alameda")));

        verify(direccionService, times(1)).buscarTodas();
    }

    @Test
    @DisplayName("Debería obtener dirección por ID")
    void deberiaObtenerPorId() throws Exception {

        when(direccionService.buscarPorId(1L)).thenReturn(response1);

        mockMvc.perform(get("/api/direcciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDireccion", is(1)))
                .andExpect(jsonPath("$.calle", is("Av. Providencia")))
                .andExpect(jsonPath("$.direccionCompleta", is("Av. Providencia 1234, Depto 501, Providencia, Santiago")));

        verify(direccionService, times(1)).buscarPorId(1L);
    }

    @Test
    @DisplayName("Debería actualizar una dirección")
    void deberiaActualizarDireccion() throws Exception {

        DireccionRequestDTO request = DireccionRequestDTO.builder()
                .calle("Av. Providencia Modificada")
                .numero(1234)
                .departamento("502")
                .comuna("Providencia")
                .ciudad("Santiago")
                .build();

        DireccionResponseDTO responseModificada = DireccionResponseDTO.builder()
                .idDireccion(1L)
                .calle("Av. Providencia Modificada")
                .numero(1234)
                .departamento("502")
                .comuna("Providencia")
                .ciudad("Santiago")
                .direccionCompleta("Av. Providencia Modificada 1234, Depto 502, Providencia, Santiago")
                .build();

        when(direccionService.actualizarDireccion(eq(1L), any(DireccionRequestDTO.class))).thenReturn(responseModificada);

        mockMvc.perform(put("/api/direcciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDireccion", is(1)))
                .andExpect(jsonPath("$.calle", is("Av. Providencia Modificada")))
                .andExpect(jsonPath("$.departamento", is("502")));

        verify(direccionService, times(1)).actualizarDireccion(eq(1L), any(DireccionRequestDTO.class));
    }

    @Test
    @DisplayName("Debería eliminar una dirección")
    void deberiaEliminarDireccion() throws Exception {

        doNothing().when(direccionService).eliminarDireccion(1L);

        mockMvc.perform(delete("/api/direcciones/1"))
                .andExpect(status().isNoContent());

        verify(direccionService, times(1)).eliminarDireccion(1L);
    }
}
