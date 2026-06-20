package ferrefix.ms_compras.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_compras.dto.CompraRequestDTO;
import ferrefix.ms_compras.dto.CompraResponseDTO;
import ferrefix.ms_compras.dto.DetalleCompraRequestDTO;
import ferrefix.ms_compras.dto.DetalleCompraResponseDTO;
import ferrefix.ms_compras.exception.GlobalExceptionHandler;
import ferrefix.ms_compras.service.CompraService;
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
 * CompraControllerTest
 * Pruebas unitarias para el controlador {@link CompraController}.
 *
 * Flujo de Endpoints:
 *
 *   [Cliente HTTP]
 *         │
 *         ├─── GET  /api/compras ────────────────► [listarTodas()] ─────────► Service.listarTodas()
 *         │
 *         ├─── GET  /api/compras/{id} ───────────► [obtenerPorId(id)] ─────► Service.obtenerPorId(id)
 *         │
 *         ├─── POST /api/compras ────────────────► [crearOrden()] ─────────► Service.crearOrdenCompra(dto)
 *         │
 *         └─── PUT  /api/compras/{id}/recibir ──► [recibirMercancia()] ──► Service.procesarRecepcionMercancia(id)
 *
 * Estructura JSON de Respuesta (CompraResponseDTO):
 *
 * {
 *   "idCompra": 1,
 *   "idProveedor": 10,
 *   "fechaCompra": "2026-06-20T02:49:15",
 *   "totalCompra": 50000,
 *   "estado": "SOLICITADO",             // o "RECIBIDO"
 *   "detalles": [
 *     {
 *       "idDetalleCompra": 1,
 *       "idProducto": 101,
 *       "cantidad": 5,
 *       "precioCompraUnitario": 10000
 *     }
 *   ]
 * }
 *
 */
@ExtendWith(MockitoExtension.class)
class CompraControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CompraService compraService;

    @InjectMocks
    private CompraController compraController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CompraResponseDTO compra1;
    private CompraResponseDTO compra2;

    @BeforeEach
    void setUp() {
        // Arrange - Configuración del MockMvc y Datos de Prueba
        mockMvc = MockMvcBuilders.standaloneSetup(compraController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        compra1 = CompraResponseDTO.builder()
                .idCompra(1L)
                .idProveedor(10)
                .fechaCompra(LocalDateTime.now())
                .totalCompra(50000)
                .estado("SOLICITADO")
                .detalles(List.of(
                        DetalleCompraResponseDTO.builder()
                                .idDetalleCompra(1L)
                                .idProducto(101L)
                                .cantidad(5)
                                .precioCompraUnitario(10000)
                                .build()
                ))
                .build();

        compra2 = CompraResponseDTO.builder()
                .idCompra(2L)
                .idProveedor(20)
                .fechaCompra(LocalDateTime.now())
                .totalCompra(30000)
                .estado("RECIBIDO")
                .detalles(List.of(
                        DetalleCompraResponseDTO.builder()
                                .idDetalleCompra(2L)
                                .idProducto(102L)
                                .cantidad(3)
                                .precioCompraUnitario(10000)
                                .build()
                ))
                .build();
    }

    /**
     * Test: deberiaListarTodas
     *
     *   Arrange: Simular listado retornando dos compras (Solicitado y Recibido).
     *   Act: Realizar GET a /api/compras.
     *   Assert: Verificar HTTP status 200 (OK), tamaño del content (2) e IDs/estados.
     *
     */
    @Test
    @DisplayName("Debería listar todas las compras")
    void deberiaListarTodas() throws Exception {
        // 1. Arrange
        when(compraService.listarTodas()).thenReturn(List.of(compra1, compra2));

        // 2. Act & 3. Assert
        mockMvc.perform(get("/api/compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idCompra", is(1)))
                .andExpect(jsonPath("$.content[0].estado", is("SOLICITADO")))
                .andExpect(jsonPath("$.content[1].idCompra", is(2)))
                .andExpect(jsonPath("$.content[1].estado", is("RECIBIDO")));

        // Verification
        verify(compraService, times(1)).listarTodas();
    }

    /**
     * Test: deberiaObtenerPorId
     *
     *   Arrange: Simular obtención de compra con ID 1.
     *   Act: Realizar GET a /api/compras/1.
     *   Assert: Verificar HTTP status 200 (OK), ID de compra 1, estado "SOLICITADO" y total.
     *
     */
    @Test
    @DisplayName("Debería obtener compra por ID")
    void deberiaObtenerPorId() throws Exception {
        // 1. Arrange
        when(compraService.obtenerPorId(1L)).thenReturn(compra1);

        // 2. Act & 3. Assert
        mockMvc.perform(get("/api/compras/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompra", is(1)))
                .andExpect(jsonPath("$.estado", is("SOLICITADO")))
                .andExpect(jsonPath("$.totalCompra", is(50000)));

        // Verification
        verify(compraService, times(1)).obtenerPorId(1L);
    }

    /**
     * Test: deberiaCrearOrden
     *
     *   Arrange: Crear CompraRequestDTO con sus detalles correspondientes y simular el servicio de creación.
     *   Act: Realizar POST a /api/compras enviando el DTO serializado.
     *   Assert: Verificar HTTP status 201 (Created), ID generado 1 y estado inicial "SOLICITADO".
     *
     */
    @Test
    @DisplayName("Debería crear una orden de compra")
    void deberiaCrearOrden() throws Exception {
        // 1. Arrange
        CompraRequestDTO request = CompraRequestDTO.builder()
                .idProveedor(10)
                .detalles(List.of(
                        DetalleCompraRequestDTO.builder()
                                .idProducto(101L)
                                .cantidad(5)
                                .precioCompraUnitario(10000)
                                .build()
                ))
                .build();

        when(compraService.crearOrdenCompra(any(CompraRequestDTO.class))).thenReturn(compra1);

        // 2. Act & 3. Assert
        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCompra", is(1)))
                .andExpect(jsonPath("$.estado", is("SOLICITADO")));

        // Verification
        verify(compraService, times(1)).crearOrdenCompra(any(CompraRequestDTO.class));
    }

    /**
     * Test: deberiaRecibirMercancia
     *
     *   Arrange: Preparar respuesta simulada con estado "RECIBIDO" y configurar el mock del servicio.
     *   Act: Realizar PUT a /api/compras/1/recibir.
     *   Assert: Verificar HTTP status 200 (OK), ID de compra 1 y estado actualizado a "RECIBIDO".
     *
     */
    @Test
    @DisplayName("Debería recibir mercancía (procesar recepción)")
    void deberiaRecibirMercancia() throws Exception {
        // 1. Arrange
        CompraResponseDTO recibida = CompraResponseDTO.builder()
                .idCompra(1L)
                .idProveedor(10)
                .fechaCompra(LocalDateTime.now())
                .totalCompra(50000)
                .estado("RECIBIDO")
                .detalles(List.of(
                        DetalleCompraResponseDTO.builder()
                                .idDetalleCompra(1L)
                                .idProducto(101L)
                                .cantidad(5)
                                .precioCompraUnitario(10000)
                                .build()
                ))
                .build();

        when(compraService.procesarRecepcionMercancia(1L)).thenReturn(recibida);

        // 2. Act & 3. Assert
        mockMvc.perform(put("/api/compras/1/recibir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompra", is(1)))
                .andExpect(jsonPath("$.estado", is("RECIBIDO")));

        // Verification
        verify(compraService, times(1)).procesarRecepcionMercancia(1L);
    }
}


