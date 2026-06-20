package ferrefix.ms_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ferrefix.ms_usuarios.dto.ClienteRequestDTO;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import ferrefix.ms_usuarios.exception.GlobalExceptionHandler;
import ferrefix.ms_usuarios.service.ClienteService;
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

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ClienteControllerTest
 *
 * Visual Flowchart of the Integration/MockMvc Testing Architecture:
 *
 *   [Test Client] --------(HTTP Request)--------> [MockMvc Engine]
 *                                                        |
 *                                                        v
 *                                               [ClienteController]
 *                                                        |
 *                                          (Delegates to Mocked Service)
 *                                                        v
 *                                             [ClienteService (Mock)]
 *                                                        |
 *                                               (Returns Mock Entity)
 *                                                        v
 *   [Test Assertions] <--(Status & JSON Path)-- [MockMvc Response]
 *
 */
@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ClienteService clienteService;

    @InjectMocks
    private ClienteController clienteController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private ClienteResponseDTO response1;
    private ClienteResponseDTO response2;

    // ==========================================
    // SETUP & INITIALIZATION
    // ==========================================
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        response1 = ClienteResponseDTO.builder()
                .runClienteCompleto("12345678-5")
                .nombreClienteCompleto("Juan Carlos Pérez González")
                .emailCliente("juan.perez@email.com")
                .telefonoCliente("987654321")
                .direccionCliente("Av. Providencia 1234, Providencia, Santiago")
                .build();

        response2 = ClienteResponseDTO.builder()
                .runClienteCompleto("87654321-0")
                .nombreClienteCompleto("Ana Silva Rojas")
                .emailCliente("ana.silva@email.com")
                .telefonoCliente("912345678")
                .direccionCliente("Alameda 345, Santiago, Santiago")
                .build();
    }

    /**
     * deberiaRegistrar:
     * Verifies that a POST request to /api/usuarios/clientes successfully registers a new client.
     *
     * Input Payload Tree (JSON):
     * ┌──────────────────────────────────────────────┐
     * │ {                                            │
     * │   "runCliente": "12.345.678-5",              │
     * │   "pnombreCliente": "Juan",                  │
     * │   "snombreCliente": "Carlos",                │
     * │   "appaternoCliente": "Pérez",               │
     * │   "apmaternoCliente": "González",            │
     * │   "fechaNacimientoCliente": "1990-05-10",    │
     * │   "emailCliente": "juan.perez@email.com",    │
     * │   "contrasenaCliente": "password123",        │
     * │   "telefonoCliente": "987654321",            │
     * │   "idDireccion": 10                          │
     * │ }                                            │
     * └──────────────────────────────────────────────┘
     *
     */
    @Test
    @DisplayName("Debería registrar un cliente")
    void deberiaRegistrar() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("12.345.678-5")
                .pnombreCliente("Juan")
                .snombreCliente("Carlos")
                .appaternoCliente("Pérez")
                .apmaternoCliente("González")
                .fechaNacimientoCliente(LocalDate.of(1990, 5, 10))
                .emailCliente("juan.perez@email.com")
                .contrasenaCliente("password123")
                .telefonoCliente("987654321")
                .idDireccion(10L)
                .build();

        when(clienteService.crearCliente(any(ClienteRequestDTO.class))).thenReturn(response1);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(post("/api/usuarios/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.runClienteCompleto", is("12345678-5")))
                .andExpect(jsonPath("$.nombreClienteCompleto", is("Juan Carlos Pérez González")));

        verify(clienteService, times(1)).crearCliente(any(ClienteRequestDTO.class));
    }

    /**
     * deberiaListarTodos:
     * Verifies that GET to /api/usuarios/clientes returns a complete list of clients.
     *
     * Output List Structure:
     * ┌────────────────────────────────────────────────────────┐
     * │ [                                                      │
     * │   { "runClienteCompleto": "12345678-5", ... },         │
     * │   { "runClienteCompleto": "87654321-0", ... }          │
     * │ ]                                                      │
     * └────────────────────────────────────────────────────────┘
     *
     */
    @Test
    @DisplayName("Debería listar todos los clientes")
    void deberiaListarTodos() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(clienteService.buscarTodosClientes()).thenReturn(List.of(response1, response2));

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(get("/api/usuarios/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].runClienteCompleto", is("12345678-5")))
                .andExpect(jsonPath("$.content[0].nombreClienteCompleto", is("Juan Carlos Pérez González")))
                .andExpect(jsonPath("$.content[1].runClienteCompleto", is("87654321-0")))
                .andExpect(jsonPath("$.content[1].nombreClienteCompleto", is("Ana Silva Rojas")));

        verify(clienteService, times(1)).buscarTodosClientes();
    }

    /**
     * deberiaObtenerPorRun:
     * Verifies that fetching a client by RUN returns the expected details.
     *
     * Target path: /api/usuarios/clientes/run/12.345.678-5
     * Path Variable: 12.345.678-5 -> parsed to RUN: 12345678
     *
     */
    @Test
    @DisplayName("Debería obtener un cliente por RUN")
    void deberiaObtenerPorRun() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(clienteService.buscarClientePorRun(12345678)).thenReturn(response1);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(get("/api/usuarios/clientes/run/12.345.678-5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.runClienteCompleto", is("12345678-5")))
                .andExpect(jsonPath("$.nombreClienteCompleto", is("Juan Carlos Pérez González")));

        verify(clienteService, times(1)).buscarClientePorRun(12345678);
    }

    /**
     * deberiaActualizar:
     * Verifies that PUT update request with JSON payload updates matching client.
     *
     * Input Payload Tree (JSON):
     * ┌──────────────────────────────────────────────┐
     * │ {                                            │
     * │   "runCliente": "12.345.678-5",              │
     * │   "pnombreCliente": "Juan Modificado",       │
     * │   "appaternoCliente": "Pérez",               │
     * │   "apmaternoCliente": "González",            │
     * │   ...                                        │
     * │ }                                            │
     * └──────────────────────────────────────────────┘
     *
     */
    @Test
    @DisplayName("Debería actualizar un cliente")
    void deberiaActualizar() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("12.345.678-5")
                .pnombreCliente("Juan Modificado")
                .appaternoCliente("Pérez")
                .apmaternoCliente("González")
                .fechaNacimientoCliente(LocalDate.of(1990, 5, 10))
                .emailCliente("juan.perez@email.com")
                .contrasenaCliente("password123")
                .telefonoCliente("987654321")
                .idDireccion(10L)
                .build();

        ClienteResponseDTO actualizada = ClienteResponseDTO.builder()
                .runClienteCompleto("12345678-5")
                .nombreClienteCompleto("Juan Modificado Pérez González")
                .emailCliente("juan.perez@email.com")
                .telefonoCliente("987654321")
                .direccionCliente("Av. Providencia 1234, Providencia, Santiago")
                .build();

        when(clienteService.actualizarCliente(eq(12345678), any(ClienteRequestDTO.class))).thenReturn(actualizada);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(put("/api/usuarios/clientes/run/12.345.678-5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.runClienteCompleto", is("12345678-5")))
                .andExpect(jsonPath("$.nombreClienteCompleto", is("Juan Modificado Pérez González")));

        verify(clienteService, times(1)).actualizarCliente(eq(12345678), any(ClienteRequestDTO.class));
    }

    /**
     * deberiaEliminar:
     * Verifies client deletion endpoint (204 No Content).
     */
    @Test
    @DisplayName("Debería eliminar un cliente")
    void deberiaEliminar() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        doNothing().when(clienteService).eliminarClientePorRun(12345678);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(delete("/api/usuarios/clientes/run/12.345.678-5"))
                .andExpect(status().isNoContent());

        verify(clienteService, times(1)).eliminarClientePorRun(12345678);
    }
}
