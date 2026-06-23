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

    @Test
    @DisplayName("Debería guardar una venta exitosamente")
    void deberiaGuardarVentaExitosamente() {

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        when(usuariosClient.obtenerClientePorRun("12.345.678-5")).thenReturn(new ClienteInfoDTO());
        when(usuariosClient.obtenerEmpleadoPorRun("87.654.321-0")).thenReturn(new EmpleadoInfoDTO());
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);
        doNothing().when(inventarioClient).descontarStock(101L, 2);
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(detalleVenta);
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));

        VentaResponseDTO result = ventaService.guardar(requestDTO);

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
        verify(inventarioClient, times(2)).obtenerProductoPorId(101L);
        verify(inventarioClient, times(1)).descontarStock(101L, 2);
        verify(detalleVentaRepository, times(1)).save(any(DetalleVenta.class));
    }

    @Test
    @DisplayName("Debería fallar al guardar si el tipo de pago no existe")
    void deberiaFallarAlGuardarSiTipoPagoNoExiste() {

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ventaService.guardar(requestDTO));

        verify(tipoPagoRepository, times(1)).findById(1);
        verifyNoInteractions(usuariosClient, inventarioClient, ventaRepository);
    }

    @Test
    @DisplayName("Debería fallar al guardar si el cliente o empleado no existe")
    void deberiaFallarAlGuardarSiClienteOEmpleadoNoExiste() {

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        FeignException.NotFound feignEx = mock(FeignException.NotFound.class);
        doThrow(feignEx).when(usuariosClient).obtenerClientePorRun("12.345.678-5");

        BadRequestException ex = assertThrows(BadRequestException.class, () -> ventaService.guardar(requestDTO));
        assertTrue(ex.getMessage().contains("El cliente o empleado no existe."));

        verify(tipoPagoRepository, times(1)).findById(1);
        verify(usuariosClient, times(1)).obtenerClientePorRun("12.345.678-5");
        verifyNoInteractions(inventarioClient, ventaRepository);
    }

    @Test
    @DisplayName("Debería fallar al guardar si el producto no existe en el inventario")
    void deberiaFallarAlGuardarSiProductoNoExiste() {

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        when(usuariosClient.obtenerClientePorRun("12.345.678-5")).thenReturn(new ClienteInfoDTO());
        when(usuariosClient.obtenerEmpleadoPorRun("87.654.321-0")).thenReturn(new EmpleadoInfoDTO());
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        FeignException.NotFound feignEx = mock(FeignException.NotFound.class);
        doThrow(feignEx).when(inventarioClient).obtenerProductoPorId(101L);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> ventaService.guardar(requestDTO));
        assertTrue(ex.getMessage().contains("Producto ID 101 no existe"));

        verify(ventaRepository, times(1)).save(any(Venta.class));
        verify(inventarioClient, times(1)).obtenerProductoPorId(101L);
        verifyNoMoreInteractions(ventaRepository, inventarioClient);
    }

    @Test
    @DisplayName("Debería fallar al guardar si hay stock insuficiente del producto")
    void deberiaFallarAlGuardarSiStockInsuficiente() {

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        when(usuariosClient.obtenerClientePorRun("12.345.678-5")).thenReturn(new ClienteInfoDTO());
        when(usuariosClient.obtenerEmpleadoPorRun("87.654.321-0")).thenReturn(new EmpleadoInfoDTO());
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        ProductoDTO sinStock = ProductoDTO.builder().id(101L).nombre("Martillo").precioVenta(10000).stock(1).build();
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(sinStock);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> ventaService.guardar(requestDTO));
        assertTrue(ex.getMessage().contains("Stock insuficiente para ID 101"));

        verify(ventaRepository, times(1)).save(any(Venta.class));
        verify(inventarioClient, times(1)).obtenerProductoPorId(101L);
        verifyNoMoreInteractions(ventaRepository, inventarioClient);
    }

    @Test
    @DisplayName("Debería listar todas las ventas")
    void deberiaListarTodasLasVentas() {

        when(ventaRepository.findAll()).thenReturn(List.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);

        List<VentaResponseDTO> result = ventaService.listarVentas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getIdVenta());
        assertEquals("Martillo", result.get(0).getDetalles().get(0).getNombreProducto());
        verify(ventaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener venta por ID exitosamente")
    void deberiaObtenerVentaPorIdExitosamente() {

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);

        VentaResponseDTO result = ventaService.obtenerVentaPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdVenta());
        assertEquals("Martillo", result.getDetalles().get(0).getNombreProducto());
        verify(ventaRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al obtener venta inexistente")
    void deberiaFallarAlObtenerVentaInexistente() {

        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ventaService.obtenerVentaPorId(99L));
        verify(ventaRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Debería buscar ventas por RUN de cliente exitosamente")
    void deberiaBuscarVentasPorRunClienteExitosamente() {

        when(ventaRepository.findByRunCliente(12345678)).thenReturn(List.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);

        List<VentaResponseDTO> result = ventaService.buscarVentasPorRunCliente("12.345.678-5");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getIdVenta());
        verify(ventaRepository, times(1)).findByRunCliente(12345678);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException si no hay ventas para el RUN de cliente")
    void deberiaFallarAlBuscarVentasPorRunClienteInexistente() {

        when(ventaRepository.findByRunCliente(99999999)).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> ventaService.buscarVentasPorRunCliente("99.999.999-9"));
        verify(ventaRepository, times(1)).findByRunCliente(99999999);
    }

    @Test
    @DisplayName("Debería manejar producto descontinuado en mapDetalleToDTO")
    void deberiaManejarProductoDescontinuado() {

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        FeignException.NotFound feignEx = mock(FeignException.NotFound.class);
        doThrow(feignEx).when(inventarioClient).obtenerProductoPorId(101L);

        VentaResponseDTO result = ventaService.obtenerVentaPorId(1L);

        assertNotNull(result);
        assertEquals("Producto Descontinuado", result.getDetalles().get(0).getNombreProducto());
        verify(inventarioClient, times(1)).obtenerProductoPorId(101L);
    }

    @Test
    @DisplayName("Debería eliminar venta y sus detalles exitosamente")
    void deberiaEliminarVentaExitosamente() {

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        doNothing().when(detalleVentaRepository).deleteAll(any());
        doNothing().when(ventaRepository).delete(venta);

        ventaService.eliminarVenta(1L);

        verify(ventaRepository, times(1)).findById(1L);
        verify(detalleVentaRepository, times(1)).findByVenta_IdVenta(1L);
        verify(detalleVentaRepository, times(1)).deleteAll(any());
        verify(ventaRepository, times(1)).delete(venta);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar venta inexistente")
    void deberiaFallarAlEliminarVentaInexistente() {

        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ventaService.eliminarVenta(99L));
        verify(ventaRepository, times(1)).findById(99L);
        verify(ventaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debería buscar detalles por venta exitosamente")
    void deberiaBuscarDetallesPorVentaExitosamente() {

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findByVenta_IdVenta(1L)).thenReturn(List.of(detalleVenta));
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);

        List<DetalleVentaResponseDTO> result = ventaService.buscarDetallesPorVenta(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Martillo", result.get(0).getNombreProducto());
        verify(ventaRepository, times(1)).findById(1L);
        verify(detalleVentaRepository, times(1)).findByVenta_IdVenta(1L);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar detalles de venta inexistente")
    void deberiaFallarAlBuscarDetallesVentaInexistente() {

        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ventaService.buscarDetallesPorVenta(99L));
    }

    @Test
    @DisplayName("Debería buscar un detalle específico exitosamente")
    void deberiaBuscarDetallePorIdExitosamente() {

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findById(10L)).thenReturn(Optional.of(detalleVenta));
        when(inventarioClient.obtenerProductoPorId(101L)).thenReturn(productoDTO);

        DetalleVentaResponseDTO result = ventaService.buscarDetallePorId(1L, 10L);

        assertNotNull(result);
        assertEquals(101L, result.getIdProducto());
        assertEquals("Martillo", result.getNombreProducto());
        verify(ventaRepository, times(1)).findById(1L);
        verify(detalleVentaRepository, times(1)).findById(10L);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar detalle de venta inexistente")
    void deberiaFallarAlBuscarDetalleVentaInexistente() {

        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ventaService.buscarDetallePorId(99L, 10L));
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar detalle inexistente")
    void deberiaFallarAlBuscarDetalleInexistente() {

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ventaService.buscarDetallePorId(1L, 99L));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException si el detalle no pertenece a la venta")
    void deberiaFallarSiDetalleNoPerteneceALaVenta() {

        when(ventaRepository.findById(1L)).thenReturn(Optional.of(venta));
        Venta otraVenta = Venta.builder().idVenta(2L).build();
        DetalleVenta detalleAjeno = DetalleVenta.builder().idDetalle(10L).venta(otraVenta).build();
        when(detalleVentaRepository.findById(10L)).thenReturn(Optional.of(detalleAjeno));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> ventaService.buscarDetallePorId(1L, 10L));
        assertTrue(ex.getMessage().contains("no pertenece a la venta"));
    }
}
