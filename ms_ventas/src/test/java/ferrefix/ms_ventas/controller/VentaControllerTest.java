package ferrefix.ms_ventas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ferrefix.ms_ventas.dto.DetalleVentaRequestDTO;
import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.dto.VentaRequestDTO;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import ferrefix.ms_ventas.exception.BadRequestException;
import ferrefix.ms_ventas.exception.GlobalExceptionHandler;
import ferrefix.ms_ventas.exception.ResourceNotFoundException;
import ferrefix.ms_ventas.service.VentaService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * VentaControllerTest
 *
 * Visual Flowchart of the Integration/MockMvc Testing Architecture:
 *
 *   [Test Client] --------(HTTP Request)--------> [MockMvc Engine]
 *                                                        |
 *                                                        v
 *                                                [VentaController]
 *                                                        |
 *                                          (Delegates to Mocked Service)
 *                                                        v
 *                                              [VentaService (Mock)]
 *                                                        |
 *                                               (Returns Mock Entity)
 *                                                        v
 *   [Test Assertions] <--(Status & JSON Path)-- [MockMvc Response]
 *
 */
@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private VentaController ventaController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private VentaResponseDTO response1;
    private DetalleVentaResponseDTO detalleResponse;

    // ==========================================
    // SETUP & INITIALIZATION
    // ==========================================
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ventaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        detalleResponse = DetalleVentaResponseDTO.builder()
                .idProducto(101L)
                .nombreProducto("Martillo")
                .cantidad(2)
                .precioUnitario(10000)
                .subtotal(20000)
                .build();

        response1 = VentaResponseDTO.builder()
                .idVenta(1L)
                .runCliente("12345678")
                .runEmpleado("87654321")
                .fechaVenta(LocalDateTime.of(2026, 6, 20, 10, 30, 0))
                .totalVenta(20000)
                .nombreTipoPago("Efectivo")
                .detalles(List.of(detalleResponse))
                .build();
    }

    /**
     * deberiaCrearVenta:
     * Verifies POST /api/ventas successfully completes a transaction and registers a sale.
     *
     * Input Payload Tree (JSON):
     * ┌──────────────────────────────────────────────┐
     * │ {                                            │
     * │   "runCliente": "12.345.678-5",              │
     * │   "runEmpleado": "87.654.321-0",             │
     * │   "idTipoPago": 1,                           │
     * │   "detalles": [                              │
     * │     { "idProducto": 101, "cantidad": 2 }     │
     * │   ]                                          │
     * │ }                                            │
     * └──────────────────────────────────────────────┘
     *
     */
    @Test
    @DisplayName("Debería crear una venta exitosamente (201 Created)")
    void deberiaCrearVenta() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        VentaRequestDTO request = VentaRequestDTO.builder()
                .runCliente("12.345.678-5")
                .runEmpleado("87.654.321-0")
                .idTipoPago(1)
                .detalles(List.of(DetalleVentaRequestDTO.builder()
                        .idProducto(101L)
                        .cantidad(2)
                        .build()))
                .build();

        when(ventaService.guardar(any(VentaRequestDTO.class))).thenReturn(response1);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idVenta", is(1)))
                .andExpect(jsonPath("$.runCliente", is("12345678")))
                .andExpect(jsonPath("$.totalVenta", is(20000)))
                .andExpect(jsonPath("$.detalles", hasSize(1)))
                .andExpect(jsonPath("$.detalles[0].nombreProducto", is("Martillo")));

        verify(ventaService, times(1)).guardar(any(VentaRequestDTO.class));
    }

    /**
     * deberiaFallarAlCrearSiClienteOEmpleadoNoExiste:
     * Verifies 400 Bad Request exception mapping when input profiles (client/employee) are invalid.
     */
    @Test
    @DisplayName("Debería fallar al crear si el cliente o empleado no existe (400 Bad Request)")
    void deberiaFallarAlCrearSiClienteOEmpleadoNoExiste() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        VentaRequestDTO request = VentaRequestDTO.builder()
                .runCliente("12.345.678-5")
                .runEmpleado("87.654.321-0")
                .idTipoPago(1)
                .detalles(List.of(DetalleVentaRequestDTO.builder()
                        .idProducto(101L)
                        .cantidad(2)
                        .build()))
                .build();

        when(ventaService.guardar(any(VentaRequestDTO.class)))
                .thenThrow(new BadRequestException("El cliente o empleado no existe."));

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("El cliente o empleado no existe.")));
    }

    /**
     * deberiaListarVentas:
     * Verifies retrieving all registered sales.
     */
    @Test
    @DisplayName("Debería listar todas las ventas")
    void deberiaListarVentas() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaService.listarVentas()).thenReturn(List.of(response1));

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].idVenta", is(1)))
                .andExpect(jsonPath("$.content[0].runCliente", is("12345678")));

        verify(ventaService, times(1)).listarVentas();
    }

    /**
     * deberiaObtenerVentaPorId:
     * Verifies lookup by sale ID returns full transaction details.
     */
    @Test
    @DisplayName("Debería obtener venta por ID")
    void deberiaObtenerVentaPorId() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaService.obtenerVentaPorId(1L)).thenReturn(response1);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta", is(1)))
                .andExpect(jsonPath("$.runCliente", is("12345678")));

        verify(ventaService, times(1)).obtenerVentaPorId(1L);
    }

    /**
     * deberiaRetornar404AlBuscarInexistente:
     * Verifies searching for an invalid sale ID yields a 404 response.
     */
    @Test
    @DisplayName("Debería retornar 404 al buscar venta inexistente")
    void deberiaRetornar404AlBuscarInexistente() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaService.obtenerVentaPorId(99L))
                .thenThrow(new ResourceNotFoundException("No se encontró la venta con ID: 99"));

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(get("/api/ventas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No se encontró la venta con ID: 99")));
    }

    /**
     * deberiaObtenerVentasPorRunCliente:
     * Verifies lookup of sales history using client's RUN identifiers.
     */
    @Test
    @DisplayName("Debería obtener ventas por RUN de cliente")
    void deberiaObtenerVentasPorRunCliente() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaService.buscarVentasPorRunCliente("12.345.678-5")).thenReturn(List.of(response1));

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(get("/api/ventas/run/12.345.678-5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].idVenta", is(1)));

        verify(ventaService, times(1)).buscarVentasPorRunCliente("12.345.678-5");
    }

    /**
     * deberiaListarDetalles:
     * Verifies listing only the detail lines of a sale.
     */
    @Test
    @DisplayName("Debería listar detalles de una venta")
    void deberiaListarDetalles() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaService.buscarDetallesPorVenta(1L)).thenReturn(List.of(detalleResponse));

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(get("/api/ventas/1/detalles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].idProducto", is(101)))
                .andExpect(jsonPath("$.content[0].nombreProducto", is("Martillo")));

        verify(ventaService, times(1)).buscarDetallesPorVenta(1L);
    }

    /**
     * deberiaObtenerDetalleEspecifico:
     * Verifies querying a single item detail from a given sale transaction.
     */
    @Test
    @DisplayName("Debería obtener un detalle específico de una venta")
    void deberiaObtenerDetalleEspecifico() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaService.buscarDetallePorId(1L, 10L)).thenReturn(detalleResponse);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(get("/api/ventas/1/detalles/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto", is(101)))
                .andExpect(jsonPath("$.nombreProducto", is("Martillo")));

        verify(ventaService, times(1)).buscarDetallePorId(1L, 10L);
    }

    /**
     * deberiaEliminarVenta:
     * Verifies DELETE deletes sale entity (204 response).
     */
    @Test
    @DisplayName("Debería eliminar una venta")
    void deberiaEliminarVenta() throws Exception {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        doNothing().when(ventaService).eliminarVenta(1L);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        mockMvc.perform(delete("/api/ventas/1"))
                .andExpect(status().isNoContent());

        verify(ventaService, times(1)).eliminarVenta(1L);
    }
}
