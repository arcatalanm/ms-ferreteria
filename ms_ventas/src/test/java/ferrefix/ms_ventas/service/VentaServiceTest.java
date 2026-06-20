package ferrefix.ms_ventas.service;

import feign.FeignException;
import ferrefix.ms_ventas.client.InventarioClient;
import ferrefix.ms_ventas.client.UsuariosClient;
import ferrefix.ms_ventas.dto.*;
import ferrefix.ms_ventas.exception.BadRequestException;
import ferrefix.ms_ventas.exception.ResourceNotFoundException;
import ferrefix.ms_ventas.mapper.VentaMapper;
import ferrefix.ms_ventas.model.DetalleVenta;
import ferrefix.ms_ventas.model.TipoPago;
import ferrefix.ms_ventas.model.Venta;
import ferrefix.ms_ventas.repository.DetalleVentaRepository;
import ferrefix.ms_ventas.repository.TipoPagoRepository;
import ferrefix.ms_ventas.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * VentaServiceTest
 *
 * Visual Flowchart of the Venta Service & Dependencies Architecture:
 *
 *                       [VentaService]
 *                             │
 *         ┌───────────────────┼───────────────────┬───────────────────┐
 *         v (Feign Mock)      v (Feign Mock)      v (DB Mock)         v (DB Mock)
 *   [UsuariosClient]   [InventarioClient]  [VentaRepository]  [DetalleVentaRepository]
 *         │                   │
 *         │                   ├─> descontarStock()
 *         │                   └─> obtenerProductoPorId()
 *         v
 *   obtenerClientePorRun()
 *   obtenerEmpleadoPorRun()
 *
 */
@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private TipoPagoRepository tipoPagoRepository;

    @Mock
    private UsuariosClient usuariosClient;

    @Mock
    private InventarioClient inventarioClient;

    @Spy
    private VentaMapper ventaMapper = new VentaMapper();

    @InjectMocks
    private VentaService ventaService;

    private TipoPago tipoPago;
    private Venta venta;
    private DetalleVenta detalleVenta;
    private VentaRequestDTO requestDTO;
    private ProductoDTO productoDTO;

    // ==========================================
    // SETUP & INITIALIZATION
    // ==========================================
    @BeforeEach
    void setUp() {
        tipoPago = TipoPago.builder()
                .idTipoPago(1)
                .nombreTipoPago("Efectivo")
                .build();

        venta = Venta.builder()
                .idVenta(1L)
                .fechaVenta(LocalDateTime.now())
                .totalVenta(20000)
                .runCliente(12345678)
                .runEmpleado(87654321)
                .tipoPago(tipoPago)
                .build();

        detalleVenta = DetalleVenta.builder()
                .idDetalle(10L)
                .idProducto(101L)
                .cantidad(2)
                .precioUnitario(10000)
                .venta(venta)
                .build();

        requestDTO = VentaRequestDTO.builder()
                .runCliente("12.345.678-5")
                .runEmpleado("87.654.321-0")
                .idTipoPago(1)
                .detalles(List.of(DetalleVentaRequestDTO.builder()
                        .idProducto(101L)
                        .cantidad(2)
                        .build()))
                .build();

        productoDTO = ProductoDTO.builder()
                .id(101L)
                .nombre("Martillo")
                .precioVenta(10000)
                .stock(50)
                .build();
    }

    /**
     * deberiaGuardarVentaExitosamente:
     * Verifies successful creation of a sale by verifying clients, employees, inventory stock, and persisting details.
     */
    @Test
    @DisplayName("Debería guardar una venta exitosamente")
    void deberiaGuardarVentaExitosamente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        when(usuariosClient.obtenerClientePorRun("12.345.678-5")).thenReturn(new ClienteInfoDTO());
        when(usuariosClient.obtenerEmpleadoPorRun("87.654.321-0")).thenReturn(new EmpleadoInfoDTO());
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);
        doNothing().when(inventarioClient).descontarStock(101L, 2);
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(detalleVenta);
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));

        // ==========================================
        // 2. ACT
        // ==========================================
        VentaResponseDTO result = ventaService.guardar(requestDTO);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(1L, result.getIdVenta());
        assertEquals("12345678", result.getRunCliente());
        assertEquals("87654321", result.getRunEmpleado());
        assertEquals("Efectivo", result.getNombreTipoPago());
        assertEquals(1, result.getDetalles().size());
        assertEquals("Martillo", result.getDetalles().get(0).getNombreProducto());
        assertEquals(20000, result.getTotalVenta());

        verify(tipoPagoRepository, times(1)).findById(1);
        verify(usuariosClient, times(1)).obtenerClientePorRun("12.345.678-5");
        verify(usuariosClient, times(1)).obtenerEmpleadoPorRun("87.654.321-0");
        verify(ventaRepository, times(2)).save(any(Venta.class));
        verify(inventarioClient, times(2)).obtenerProductoPorId(101L); // Una en guardar y una en mapear detalle
        verify(inventarioClient, times(1)).descontarStock(101L, 2);
        verify(detalleVentaRepository, times(1)).save(any(DetalleVenta.class));
    }

    /**
     * deberiaFallarAlGuardarSiTipoPagoNoExiste:
     * Verifies ResourceNotFoundException when referencing an unregistered TipoPago.
     */
    @Test
    @DisplayName("Debería fallar al guardar si el tipo de pago no existe")
    void deberiaFallarAlGuardarSiTipoPagoNoExiste() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.findById(1)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () -> ventaService.guardar(requestDTO));

        verify(tipoPagoRepository, times(1)).findById(1);
        verifyNoInteractions(usuariosClient, inventarioClient, ventaRepository);
    }

    /**
     * deberiaFallarAlGuardarSiClienteOEmpleadoNoExiste:
     * Verifies BadRequestException when external user microservice lookup returns 404/not found.
     */
    @Test
    @DisplayName("Debería fallar al guardar si el cliente o empleado no existe")
    void deberiaFallarAlGuardarSiClienteOEmpleadoNoExiste() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        FeignException.NotFound feignEx = mock(FeignException.NotFound.class);
        doThrow(feignEx).when(usuariosClient).obtenerClientePorRun("12.345.678-5");

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        BadRequestException ex = assertThrows(BadRequestException.class, () -> ventaService.guardar(requestDTO));
        assertTrue(ex.getMessage().contains("El cliente o empleado no existe."));

        verify(tipoPagoRepository, times(1)).findById(1);
        verify(usuariosClient, times(1)).obtenerClientePorRun("12.345.678-5");
        verifyNoInteractions(inventarioClient, ventaRepository);
    }

    /**
     * deberiaFallarAlGuardarSiProductoNoExiste:
     * Verifies BadRequestException when referenced product does not exist in inventory microservice.
     */
    @Test
    @DisplayName("Debería fallar al guardar si el producto no existe en el inventario")
    void deberiaFallarAlGuardarSiProductoNoExiste() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        when(usuariosClient.obtenerClientePorRun("12.345.678-5")).thenReturn(new ClienteInfoDTO());
        when(usuariosClient.obtenerEmpleadoPorRun("87.654.321-0")).thenReturn(new EmpleadoInfoDTO());
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        FeignException.NotFound feignEx = mock(FeignException.NotFound.class);
        doThrow(feignEx).when(inventarioClient).obtenerProductoPorId(101L);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        BadRequestException ex = assertThrows(BadRequestException.class, () -> ventaService.guardar(requestDTO));
        assertTrue(ex.getMessage().contains("Producto ID 101 no existe"));

        verify(ventaRepository, times(1)).save(any(Venta.class));
        verify(inventarioClient, times(1)).obtenerProductoPorId(101L);
        verifyNoMoreInteractions(ventaRepository, inventarioClient);
    }

    /**
     * deberiaFallarAlGuardarSiStockInsuficiente:
     * Verifies BadRequestException when product stock level is lower than the requested purchase quantity.
     */
    @Test
    @DisplayName("Debería fallar al guardar si hay stock insuficiente del producto")
    void deberiaFallarAlGuardarSiStockInsuficiente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        when(usuariosClient.obtenerClientePorRun("12.345.678-5")).thenReturn(new ClienteInfoDTO());
        when(usuariosClient.obtenerEmpleadoPorRun("87.654.321-0")).thenReturn(new EmpleadoInfoDTO());
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        ProductoDTO sinStock = ProductoDTO.builder().id(101L).nombre("Martillo").precioVenta(10000).stock(1).build();
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(sinStock);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        BadRequestException ex = assertThrows(BadRequestException.class, () -> ventaService.guardar(requestDTO));
        assertTrue(ex.getMessage().contains("Stock insuficiente para ID 101"));

        verify(ventaRepository, times(1)).save(any(Venta.class));
        verify(inventarioClient, times(1)).obtenerProductoPorId(101L);
        verifyNoMoreInteractions(ventaRepository, inventarioClient);
    }

    /**
     * deberiaListarTodasLasVentas:
     * Verifies retrieving all sales and listing details with product mappings.
     */
    @Test
    @DisplayName("Debería listar todas las ventas")
    void deberiaListarTodasLasVentas() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findAll()).thenReturn(List.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        List<VentaResponseDTO> result = ventaService.listarVentas();

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getIdVenta());
        assertEquals("Martillo", result.get(0).getDetalles().get(0).getNombreProducto());
        verify(ventaRepository, times(1)).findAll();
    }

    /**
     * deberiaObtenerVentaPorIdExitosamente:
     * Verifies lookup by sale ID retrieves sale details mapped with product details.
     */
    @Test
    @DisplayName("Debería obtener venta por ID exitosamente")
    void deberiaObtenerVentaPorIdExitosamente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        VentaResponseDTO result = ventaService.obtenerVentaPorId(1L);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(1L, result.getIdVenta());
        assertEquals("Martillo", result.getDetalles().get(0).getNombreProducto());
        verify(ventaRepository, times(1)).findById(1L);
    }

    /**
     * deberiaFallarAlObtenerVentaInexistente:
     * Verifies ResourceNotFoundException when searching for an invalid sale ID.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al obtener venta inexistente")
    void deberiaFallarAlObtenerVentaInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () -> ventaService.obtenerVentaPorId(99L));
        verify(ventaRepository, times(1)).findById(99L);
    }

    /**
     * deberiaBuscarVentasPorRunClienteExitosamente:
     * Verifies finding a history of sales transactions using client's RUN.
     */
    @Test
    @DisplayName("Debería buscar ventas por RUN de cliente exitosamente")
    void deberiaBuscarVentasPorRunClienteExitosamente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findByRunCliente(12345678)).thenReturn(List.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        List<VentaResponseDTO> result = ventaService.buscarVentasPorRunCliente("12.345.678-5");

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getIdVenta());
        verify(ventaRepository, times(1)).findByRunCliente(12345678);
    }

    /**
     * deberiaFallarAlBuscarVentasPorRunClienteInexistente:
     * Verifies ResourceNotFoundException when searching history for a client RUN with no sales.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si no hay ventas para el RUN de cliente")
    void deberiaFallarAlBuscarVentasPorRunClienteInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findByRunCliente(99999999)).thenReturn(Collections.emptyList());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () -> ventaService.buscarVentasPorRunCliente("99.999.999-9"));
        verify(ventaRepository, times(1)).findByRunCliente(99999999);
    }

    /**
     * deberiaManejarProductoDescontinuado:
     * Verifies that product mapping correctly outputs placeholder text "Producto Descontinuado" if Feign client returns 404.
     */
    @Test
    @DisplayName("Debería manejar producto descontinuado en mapDetalleToDTO")
    void deberiaManejarProductoDescontinuado() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        FeignException.NotFound feignEx = mock(FeignException.NotFound.class);
        doThrow(feignEx).when(inventarioClient).obtenerProductoPorId(101L);

        // ==========================================
        // 2. ACT
        // ==========================================
        VentaResponseDTO result = ventaService.obtenerVentaPorId(1L);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals("Producto Descontinuado", result.getDetalles().get(0).getNombreProducto());
        verify(inventarioClient, times(1)).obtenerProductoPorId(101L);
    }

    /**
     * deberiaEliminarVentaExitosamente:
     * Verifies deletion of sale and details concurrently.
     */
    @Test
    @DisplayName("Debería eliminar venta y sus detalles exitosamente")
    void deberiaEliminarVentaExitosamente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        doNothing().when(detalleVentaRepository).deleteAll(any());
        doNothing().when(ventaRepository).delete(venta);

        // ==========================================
        // 2. ACT
        // ==========================================
        ventaService.eliminarVenta(1L);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        verify(ventaRepository, times(1)).findById(1L);
        verify(detalleVentaRepository, times(1)).findByVenta_IdVenta(1L);
        verify(detalleVentaRepository, times(1)).deleteAll(any());
        verify(ventaRepository, times(1)).delete(venta);
    }

    /**
     * deberiaFallarAlEliminarVentaInexistente:
     * Verifies ResourceNotFoundException when trying to delete a non-existent sale.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar venta inexistente")
    void deberiaFallarAlEliminarVentaInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () -> ventaService.eliminarVenta(99L));
        verify(ventaRepository, times(1)).findById(99L);
        verify(ventaRepository, never()).delete(any());
    }

    /**
     * deberiaBuscarDetallesPorVentaExitosamente:
     * Verifies lookup list of details from a given sale ID.
     */
    @Test
    @DisplayName("Debería buscar detalles por venta exitosamente")
    void deberiaBuscarDetallesPorVentaExitosamente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        List<DetalleVentaResponseDTO> result = ventaService.buscarDetallesPorVenta(1L);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Martillo", result.get(0).getNombreProducto());
        verify(ventaRepository, times(1)).findById(1L);
        verify(detalleVentaRepository, times(1)).findByVenta_IdVenta(1L);
    }

    /**
     * deberiaFallarAlBuscarDetallesVentaInexistente:
     * Verifies ResourceNotFoundException when querying details of a non-existent sale ID.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar detalles de venta inexistente")
    void deberiaFallarAlBuscarDetallesVentaInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () -> ventaService.buscarDetallesPorVenta(99L));
    }

    /**
     * deberiaBuscarDetallePorIdExitosamente:
     * Verifies lookup of specific detail line item from a sale transaction.
     */
    @Test
    @DisplayName("Debería buscar un detalle específico exitosamente")
    void deberiaBuscarDetallePorIdExitosamente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findById(10L)).thenReturn(Optional.of(detalleVenta));
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        DetalleVentaResponseDTO result = ventaService.buscarDetallePorId(1L, 10L);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(101L, result.getIdProducto());
        assertEquals("Martillo", result.getNombreProducto());
        verify(ventaRepository, times(1)).findById(1L);
        verify(detalleVentaRepository, times(1)).findById(10L);
    }

    /**
     * deberiaFallarAlBuscarDetalleVentaInexistente:
     * Verifies ResourceNotFoundException when querying detail line of non-existent sale ID.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar detalle de venta inexistente")
    void deberiaFallarAlBuscarDetalleVentaInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () -> ventaService.buscarDetallePorId(99L, 10L));
    }

    /**
     * deberiaFallarAlBuscarDetalleInexistente:
     * Verifies ResourceNotFoundException when querying non-existent detail ID.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar detalle inexistente")
    void deberiaFallarAlBuscarDetalleInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findById(99L)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () -> ventaService.buscarDetallePorId(1L, 99L));
    }

    /**
     * deberiaFallarSiDetalleNoPerteneceALaVenta:
     * Verifies BadRequestException when targeted detail belongs to a different sale.
     */
    @Test
    @DisplayName("Debería lanzar BadRequestException si el detalle no pertenece a la venta")
    void deberiaFallarSiDetalleNoPerteneceALaVenta() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        Venta otraVenta = Venta.builder().idVenta(2L).build();
        DetalleVenta detalleAjeno = DetalleVenta.builder().idDetalle(10L).venta(otraVenta).build();
        when(detalleVentaRepository.findById(10L)).thenReturn(Optional.of(detalleAjeno));

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        BadRequestException ex = assertThrows(BadRequestException.class, () -> ventaService.buscarDetallePorId(1L, 10L));
        assertTrue(ex.getMessage().contains("no pertenece a la venta"));
    }
}
