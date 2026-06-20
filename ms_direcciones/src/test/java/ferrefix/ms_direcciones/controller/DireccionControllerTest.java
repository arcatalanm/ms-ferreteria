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
 * DireccionControllerTest
 * Pruebas unitarias para el controlador
 *
 * Flujo de Endpoints
 *   [Cliente HTTP]
 *         │
 *         ├─── GET    /api/direcciones ───────► [buscarTodas()] ─────────► Service.buscarTodas()
 *         │
 *         ├─── GET    /api/direcciones/{id} ──► [buscarPorId(id)] ───────► Service.buscarPorId(id)
 *         │
 *         ├─── POST   /api/direcciones ───────► [crearDireccion()] ──────► Service.crearDireccion(dto)
 *         │
 *         ├─── PUT    /api/direcciones/{id} ──► [actualizarDireccion()] ─► Service.actualizarDireccion(id, dto)
 *         │
 *         └─── DELETE /api/direcciones/{id} ──► [eliminarDireccion()] ───► Service.eliminarDireccion(id)
 *
 * Estructura JSON de Respuesta (DireccionResponseDTO)
 * {
 *   "idDireccion": 1,
 *   "calle": "Av. Providencia",
 *   "numero": 1234,
 *   "departamento": "501",
 *   "comuna": "Providencia",
 *   "ciudad": "Santiago",
 *   "direccionCompleta": "Av. Providencia 1234, Depto 501, Providencia, Santiago"
 * }
 */
@ExtendWith(MockitoExtension.class)
class DireccionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DireccionService direccionService;

    @InjectMocks
    private DireccionController direccionController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private DireccionResponseDTO response1;
    private DireccionResponseDTO response2;

    @BeforeEach
    void setUp() {
        // Configuración de MockMvc y Datos de Prueba
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

    /**
     * Test: deberiaCrearDireccion
     *
     *   Arrange: Crear DireccionRequestDTO y configurar mock de creación.
     *   Act: Realizar POST a /api/direcciones con JSON serializado.
     *   Assert: Verificar HTTP status 201 (Created), idDireccion 1 y direcciónCompleta.
     */
    @Test
    @DisplayName("Debería crear una dirección")
    void deberiaCrearDireccion() throws Exception {
        // 1. Arrange
        DireccionRequestDTO request = DireccionRequestDTO.builder()
                .calle("Av. Providencia")
                .numero(1234)
                .departamento("501")
                .comuna("Providencia")
                .ciudad("Santiago")
                .build();

        when(direccionService.crearDireccion(any(DireccionRequestDTO.class))).thenReturn(response1);

        // 2. Act & 3. Assert
        mockMvc.perform(post("/api/direcciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idDireccion", is(1)))
                .andExpect(jsonPath("$.calle", is("Av. Providencia")))
                .andExpect(jsonPath("$.direccionCompleta", is("Av. Providencia 1234, Depto 501, Providencia, Santiago")));

        // Verification
        verify(direccionService, times(1)).crearDireccion(any(DireccionRequestDTO.class));
    }

    /**
     * Test: deberiaListarTodas
     *
     *   Arrange: Simular listado retornando dos direcciones.
     *   Act: Realizar GET a /api/direcciones.
     *   Assert: Verificar HTTP status 200 (OK), tamaño del arreglo (2) y datos correspondientes.
     *
     */
    @Test
    @DisplayName("Debería listar todas las direcciones")
    void deberiaListarTodas() throws Exception {
        // 1. Arrange
        when(direccionService.buscarTodas()).thenReturn(List.of(response1, response2));

        // 2. Act & 3. Assert
        mockMvc.perform(get("/api/direcciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idDireccion", is(1)))
                .andExpect(jsonPath("$.content[0].calle", is("Av. Providencia")))
                .andExpect(jsonPath("$.content[1].idDireccion", is(2)))
                .andExpect(jsonPath("$.content[1].calle", is("Alameda")));

        // Verification
        verify(direccionService, times(1)).buscarTodas();
    }

    /**
     * Test: deberiaObtenerPorId
     *
     *   Arrange: Simular retorno de la dirección 1 al buscar por ID.
     *   Act: Realizar GET a /api/direcciones/1.
     *   Assert: Verificar HTTP status 200 (OK), idDireccion 1 e información de la calle.
     *
     */
    @Test
    @DisplayName("Debería obtener dirección por ID")
    void deberiaObtenerPorId() throws Exception {
        // 1. Arrange
        when(direccionService.buscarPorId(1L)).thenReturn(response1);

        // 2. Act & 3. Assert
        mockMvc.perform(get("/api/direcciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDireccion", is(1)))
                .andExpect(jsonPath("$.calle", is("Av. Providencia")))
                .andExpect(jsonPath("$.direccionCompleta", is("Av. Providencia 1234, Depto 501, Providencia, Santiago")));

        // Verification
        verify(direccionService, times(1)).buscarPorId(1L);
    }

    /**
     * Test: deberiaActualizarDireccion
     *
     *   Arrange: Preparar DTO con modificaciones y simular retorno del mock con datos actualizados.
     *   Act: Realizar PUT a /api/direcciones/1 con JSON del request.
     *   Assert: Verificar HTTP status 200 (OK), calle modificada y departamento nuevo.
     *
     */
    @Test
    @DisplayName("Debería actualizar una dirección")
    void deberiaActualizarDireccion() throws Exception {
        // 1. Arrange
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

        // 2. Act & 3. Assert
        mockMvc.perform(put("/api/direcciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDireccion", is(1)))
                .andExpect(jsonPath("$.calle", is("Av. Providencia Modificada")))
                .andExpect(jsonPath("$.departamento", is("502")));

        // Verification
        verify(direccionService, times(1)).actualizarDireccion(eq(1L), any(DireccionRequestDTO.class));
    }

    /**
     * Test: deberiaEliminarDireccion
     *
     *   Arrange: Configurar eliminación exitosa sin retorno (void).
     *   Act: Realizar DELETE a /api/direcciones/1.
     *   Assert: Verificar HTTP status 204 (No Content).
     *
     */
    @Test
    @DisplayName("Debería eliminar una dirección")
    void deberiaEliminarDireccion() throws Exception {
        // 1. Arrange
        doNothing().when(direccionService).eliminarDireccion(1L);

        // 2. Act & 3. Assert
        mockMvc.perform(delete("/api/direcciones/1"))
                .andExpect(status().isNoContent());

        // Verification
        verify(direccionService, times(1)).eliminarDireccion(1L);
    }
}

