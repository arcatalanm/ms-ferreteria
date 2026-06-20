package ferrefix.ms_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_usuarios.dto.CargoRequestDTO;
import ferrefix.ms_usuarios.dto.CargoResponseDTO;
import ferrefix.ms_usuarios.exception.GlobalExceptionHandler;
import ferrefix.ms_usuarios.mapper.CargoMapper;
import ferrefix.ms_usuarios.model.Cargo;
import ferrefix.ms_usuarios.service.CargoService;
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

/**
 * CargoControllerTest
 *
 * Visual Flowchart of the Integration/MockMvc Testing Architecture:
 *
 *   [Test Request] --------(JSON Payload)--------> [MockMvc Engine]
 *                                                         |
 *                                                         v
 *                                                 [CargoController]
 *                                                         |
 *                                           (Delegates to Mocked Service)
 *                                                         v
 *                                              [CargoService (Mock)]
 *                                                         |
 *                                                (Returns Mock Entity)
 *                                                         v
 *   [Test Assertions] <--(Status & JSON Path)-- [MockMvc Response]
 *
 */
@ExtendWith(MockitoExtension.class)
class CargoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CargoService cargoService;

    @Spy
    private CargoMapper cargoMapper = new CargoMapper();

    @InjectMocks
    private CargoController cargoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private Cargo cargoAdmin;
    private CargoResponseDTO responseAdmin;
    private CargoResponseDTO responseVendedor;

    // ==========================================
    // SETUP & INITIALIZATION
    // ==========================================
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cargoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        cargoAdmin = Cargo.builder()
                .idCargo(1)
                .nombreCargo("Administrador")
                .build();

        responseAdmin = CargoResponseDTO.builder()
                .idCargo(1)
                .nombreCargo("Administrador")
                .build();

        responseVendedor = CargoResponseDTO.builder()
                .idCargo(2)
                .nombreCargo("Vendedor")
                .build();
    }

    /**
     * deberiaCrear:
     * Verifies that a valid POST request to /api/usuarios/cargos successfully creates a cargo.
     *
     * Input Payload (JSON):
     * ┌───────────────────────────────┐
     * │ {                             │
     * │   "nombreCargo": "Administrador"│
     * │ }                             │
     * └───────────────────────────────┘
     *
     * Output Payload (JSON):
     * ┌───────────────────────────────┐
     * │ {                             │
     * │   "idCargo": 1,               │
     * │   "nombreCargo": "Administrador"│
     * │ }                             │
     * └───────────────────────────────┘
     *
     */
    @Test
    @DisplayName("Debería crear un cargo")
    void deberiaCrear() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        CargoRequestDTO request = CargoRequestDTO.builder()
                .nombreCargo("Administrador")
                .build();

        when(cargoService.crearCargo(any(CargoRequestDTO.class))).thenReturn(cargoAdmin);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(post("/api/usuarios/cargos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCargo", is(1)))
                .andExpect(jsonPath("$.nombreCargo", is("Administrador")));

        verify(cargoService, times(1)).crearCargo(any(CargoRequestDTO.class));
    }

    /**
     * deberiaListarTodos:
     * Verifies that GET request to /api/usuarios/cargos retrieves all cargo records.
     *
     * Output Payload (JSON List):
     * ┌──────────────────────────────────────────────┐
     * │ {                                            │
     * │   "content": [                               │
     * │     { "idCargo": 1, "nombreCargo": "Admin" },│
     * │     { "idCargo": 2, "nombreCargo": "Vendedor" }│
     * │   ]                                          │
     * │ }                                            │
     * └──────────────────────────────────────────────┘
     *
     */
    @Test
    @DisplayName("Debería listar todos los cargos")
    void deberiaListarTodos() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(cargoService.buscarTodos()).thenReturn(List.of(responseAdmin, responseVendedor));

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(get("/api/usuarios/cargos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idCargo", is(1)))
                .andExpect(jsonPath("$.content[0].nombreCargo", is("Administrador")))
                .andExpect(jsonPath("$.content[1].idCargo", is(2)))
                .andExpect(jsonPath("$.content[1].nombreCargo", is("Vendedor")));

        verify(cargoService, times(1)).buscarTodos();
    }

    /**
     * deberiaObtenerPorId:
     * Verifies that GET request to /api/usuarios/cargos/{id} fetches correct cargo details.
     *
     * Endpoint path: /api/usuarios/cargos/1
     *
     */
    @Test
    @DisplayName("Debería obtener cargo por ID")
    void deberiaObtenerPorId() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(cargoService.buscarCargoPorId(1)).thenReturn(responseAdmin);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(get("/api/usuarios/cargos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCargo", is(1)))
                .andExpect(jsonPath("$.nombreCargo", is("Administrador")));

        verify(cargoService, times(1)).buscarCargoPorId(1);
    }

    /**
     * deberiaActualizar:
     * Verifies that PUT request to /api/usuarios/cargos/{id} successfully updates an existing cargo.
     *
     * Input Payload (JSON):
     * ┌──────────────────────────────────────────────┐
     * │ {                                            │
     * │   "nombreCargo": "Administrador Senior"      │
     * │ }                                            │
     * └──────────────────────────────────────────────┘
     *
     */
    @Test
    @DisplayName("Debería actualizar un cargo")
    void deberiaActualizar() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        CargoRequestDTO request = CargoRequestDTO.builder()
                .nombreCargo("Administrador Senior")
                .build();

        Cargo actualizada = Cargo.builder()
                .idCargo(1)
                .nombreCargo("Administrador Senior")
                .build();

        when(cargoService.actualizarCargo(eq(1), any(CargoRequestDTO.class))).thenReturn(actualizada);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(put("/api/usuarios/cargos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCargo", is(1)))
                .andExpect(jsonPath("$.nombreCargo", is("Administrador Senior")));

        verify(cargoService, times(1)).actualizarCargo(eq(1), any(CargoRequestDTO.class));
    }

    /**
     * deberiaEliminar:
     * Verifies that DELETE request to /api/usuarios/cargos/{id} successfully deletes cargo and returns 204 No Content.
     */
    @Test
    @DisplayName("Debería eliminar un cargo")
    void deberiaEliminar() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        doNothing().when(cargoService).eliminarCargo(1);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(delete("/api/usuarios/cargos/1"))
                .andExpect(status().isNoContent());

        verify(cargoService, times(1)).eliminarCargo(1);
    }
}
