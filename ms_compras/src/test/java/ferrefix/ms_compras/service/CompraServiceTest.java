package ferrefix.ms_compras.service;

import feign.FeignException;
import ferrefix.ms_compras.client.InventarioClient;
import ferrefix.ms_compras.client.ProveedorClient;
import ferrefix.ms_compras.dto.CompraRequestDTO;
import ferrefix.ms_compras.dto.CompraResponseDTO;
import ferrefix.ms_compras.dto.DetalleCompraRequestDTO;
import ferrefix.ms_compras.dto.StockIncrementDTO;
import ferrefix.ms_compras.exception.BadRequestException;
import ferrefix.ms_compras.exception.ResourceNotFoundException;
import ferrefix.ms_compras.mapper.CompraMapper;
import ferrefix.ms_compras.model.Compra;
import ferrefix.ms_compras.model.DetalleCompra;
import ferrefix.ms_compras.repository.CompraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Spy
    private CompraMapper compraMapper = new CompraMapper();

    @Mock
    private ProveedorClient proveedorClient;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private CompraService compraService;

    private Compra compra1;
    private Compra compra2;

    @BeforeEach
    void setUp() {

        compra1 = Compra.builder()
                .idCompra(1L)
                .idProveedor(10)
                .fechaCompra(LocalDateTime.now())
                .estado("SOLICITADO")
                .totalCompra(50000)
                .detalles(new ArrayList<>())
                .build();

        DetalleCompra d1 = DetalleCompra.builder()
                .idDetalleCompra(1L)
                .compra(compra1)
                .idProducto(101L)
                .cantidad(5)
                .precioCompraUnitario(10000)
                .build();
        compra1.getDetalles().add(d1);

        compra2 = Compra.builder()
                .idCompra(2L)
                .idProveedor(20)
                .fechaCompra(LocalDateTime.now())
                .estado("RECIBIDO")
                .totalCompra(30000)
                .detalles(new ArrayList<>())
                .build();

        DetalleCompra d2 = DetalleCompra.builder()
                .idDetalleCompra(2L)
                .compra(compra2)
                .idProducto(102L)
                .cantidad(3)
                .precioCompraUnitario(10000)
                .build();
        compra2.getDetalles().add(d2);
    }

    @Test
    @DisplayName("Debería listar todas las compras")
    void deberiaListarTodas() {

        when(compraRepository.findAll()).thenReturn(List.of(compra1, compra2));

        List<CompraResponseDTO> result = compraService.listarTodas();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10, result.get(0).getIdProveedor());
        assertEquals(20, result.get(1).getIdProveedor());
        verify(compraRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener compra por ID")
    void deberiaObtenerPorId() {

        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra1));

        CompraResponseDTO result = compraService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdCompra());
        assertEquals("SOLICITADO", result.getEstado());
        verify(compraRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al obtener compra inexistente")
    void deberiaFallarAlObtenerInexistente() {

        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> compraService.obtenerPorId(99L));
        verify(compraRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Debería crear una orden de compra exitosamente")
    void deberiaCrearOrdenCompraExitosamente() {

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

        when(proveedorClient.existeProveedor(10)).thenReturn(true);
        when(compraRepository.save(any(Compra.class))).thenReturn(compra1);

        CompraResponseDTO result = compraService.crearOrdenCompra(request);

        assertNotNull(result);
        assertEquals(1L, result.getIdCompra());
        assertEquals(50000, result.getTotalCompra());
        verify(proveedorClient, times(1)).existeProveedor(10);
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    @DisplayName("Debería fallar al crear orden si el proveedor no existe")
    void deberiaFallarAlCrearOrdenProveedorInexistente() {

        CompraRequestDTO request = CompraRequestDTO.builder()
                .idProveedor(99)
                .detalles(List.of(
                        DetalleCompraRequestDTO.builder()
                                .idProducto(101L)
                                .cantidad(5)
                                .precioCompraUnitario(10000)
                                .build()
                ))
                .build();

        when(proveedorClient.existeProveedor(99)).thenReturn(false);

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                compraService.crearOrdenCompra(request)
        );

        assertTrue(ex.getMessage().contains("no existe"));
        verify(proveedorClient, times(1)).existeProveedor(99);
        verify(compraRepository, never()).save(any(Compra.class));
    }

    @Test
    @DisplayName("Debería fallar al crear orden si la llamada Feign lanza excepción")
    void deberiaFallarAlCrearOrdenFeignException() {

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

        FeignException feignEx = mock(FeignException.class);
        when(feignEx.getMessage()).thenReturn("Connection refused");
        doThrow(feignEx).when(proveedorClient).existeProveedor(10);

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                compraService.crearOrdenCompra(request)
        );

        assertTrue(ex.getMessage().contains("Error de comunicación"));
        verify(proveedorClient, times(1)).existeProveedor(10);
        verify(compraRepository, never()).save(any(Compra.class));
    }

    @Test
    @DisplayName("Debería procesar recepción de mercancía exitosamente")
    void deberiaProcesarRecepcionExitosamente() {

        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra1));
        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(inventarioClient).incrementarStock(any(StockIncrementDTO.class));

        CompraResponseDTO result = compraService.procesarRecepcionMercancia(1L);

        assertNotNull(result);
        assertEquals("RECIBIDO", result.getEstado());
        verify(compraRepository, times(1)).findById(1L);
        verify(compraRepository, times(1)).save(any(Compra.class));
        verify(inventarioClient, times(1)).incrementarStock(any(StockIncrementDTO.class));
    }

    @Test
    @DisplayName("Debería fallar recepción si la orden de compra no existe")
    void deberiaFallarRecepcionInexistente() {

        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                compraService.procesarRecepcionMercancia(99L)
        );

        verify(compraRepository, times(1)).findById(99L);
        verify(compraRepository, never()).save(any(Compra.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException si la orden ya está recibida")
    void deberiaFallarRecepcionYaProcesada() {

        when(compraRepository.findById(2L)).thenReturn(Optional.of(compra2));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                compraService.procesarRecepcionMercancia(2L)
        );

        assertTrue(ex.getMessage().contains("Solo se pueden recibir órdenes en estado SOLICITADO"));
        verify(compraRepository, times(1)).findById(2L);
        verify(compraRepository, never()).save(any(Compra.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException si incrementar stock falla vía Feign")
    void deberiaFallarRecepcionFeignException() {

        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra1));
        when(compraRepository.save(any(Compra.class))).thenAnswer(i -> i.getArgument(0));

        FeignException feignEx = mock(FeignException.class);
        when(feignEx.getMessage()).thenReturn("Connection timed out");
        doThrow(feignEx).when(inventarioClient).incrementarStock(any(StockIncrementDTO.class));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                compraService.procesarRecepcionMercancia(1L)
        );

        assertTrue(ex.getMessage().contains("Falla en la comunicación con el inventario"));
        verify(compraRepository, times(1)).findById(1L);
        verify(compraRepository, times(1)).save(any(Compra.class));
        verify(inventarioClient, times(1)).incrementarStock(any(StockIncrementDTO.class));
    }
}
