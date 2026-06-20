package ferrefix.ms_inventario.service;

import ferrefix.ms_inventario.dto.ProductoRequestDTO;
import ferrefix.ms_inventario.dto.ProductoResponseDTO;
import ferrefix.ms_inventario.exception.BadRequestException;
import ferrefix.ms_inventario.exception.ResourceNotFoundException;
import ferrefix.ms_inventario.mapper.ProductoMapper;
import ferrefix.ms_inventario.model.CategoriaProducto;
import ferrefix.ms_inventario.model.Producto;
import ferrefix.ms_inventario.model.UnidadMedida;
import ferrefix.ms_inventario.repository.CategoriaProductoRepository;
import ferrefix.ms_inventario.repository.ProductoRepository;
import ferrefix.ms_inventario.repository.UnidadMedidaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ┌──────────────────────────────────────────────────────────┐
 * │                  ProductoServiceTest                     │
 * ├──────────────────────────────────────────────────────────┤
 * │  Tests ProductoService using Mockito.                    │
 * │                                                          │
 * │                   [ProductoService]                      │
 * │             /             |            \                 │
 * │  (calls repo)       (calls repo)    (calls repo)         │
 * │        ▼                  ▼              ▼               │
 * │ [ProductoRepository] [CategoriaRepo] [UnidadMedidaRepo]  │
 * │      (Mock)             (Mock)          (Mock)           │
 * │                                                          │
 * │        └── (uses) ──► [ProductoMapper] (Spy)             │
 * └──────────────────────────────────────────────────────────┘
 */
@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaProductoRepository categoriaProductoRepository;

    @Mock
    private UnidadMedidaRepository unidadMedidaRepository;

    @Spy
    private ProductoMapper productoMapper = new ProductoMapper();

    @InjectMocks
    private ProductoService productoService;

    private CategoriaProducto catHerramientas;
    private CategoriaProducto catPinturas;
    private UnidadMedida unidadUnidad;
    private Producto producto1;
    private Producto producto2;

    @BeforeEach
    void setUp() {
        catHerramientas = CategoriaProducto.builder()
                .idCategoria(1)
                .nombreCategoria("Herramientas")
                .build();

        catPinturas = CategoriaProducto.builder()
                .idCategoria(2)
                .nombreCategoria("Pinturas")
                .build();

        unidadUnidad = UnidadMedida.builder()
                .idUnidadMedida(1)
                .nombreUnidadMedida("Unidad")
                .build();

        producto1 = Producto.builder()
                .idProducto(101L)
                .categoriaProducto(catHerramientas)
                .unidadMedida(unidadUnidad)
                .codigoBarrasProducto("123456789")
                .nombreProducto("Martillo de Uña")
                .stockProducto(10)
                .precioVentaProducto(5000)
                .build();

        producto2 = Producto.builder()
                .idProducto(102L)
                .categoriaProducto(catHerramientas)
                .unidadMedida(unidadUnidad)
                .codigoBarrasProducto("987654321")
                .nombreProducto("Alicate Universal")
                .stockProducto(15)
                .precioVentaProducto(6000)
                .build();
    }

    @Test
    @DisplayName("Debería crear un producto exitosamente")
    void deberiaCrearProductoExitosamente() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE: Request DTO               │
         *  │ Mock: existsByCodigo -> false      │
         *  │ Mock: CategoriaRepo.findById -> OK │
         *  │ Mock: UnidadMedidaRepo.findById->OK│
         *  │ Mock: save -> return saved entity  │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT: crearProducto()               │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ASSERT: Validate response DTO     │
         *  │ Verify Mock interactions           │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        ProductoRequestDTO request = ProductoRequestDTO.builder()
                .categoria(1)
                .unidadMedida(1)
                .codigoBarras("123456789")
                .nombre("Martillo de Uña")
                .stock(10)
                .precioVenta(5000)
                .build();

        when(productoRepository.existsByCodigoBarrasProducto("123456789")).thenReturn(false);
        when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(catHerramientas));
        when(unidadMedidaRepository.findById(1)).thenReturn(Optional.of(unidadUnidad));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto1);

        // === ACT ===
        ProductoResponseDTO result = productoService.crearProducto(request);

        // === ASSERT ===
        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("Martillo de Uña", result.getNombre());
        verify(productoRepository, times(1)).existsByCodigoBarrasProducto("123456789");
        verify(categoriaProductoRepository, times(1)).findById(1);
        verify(unidadMedidaRepository, times(1)).findById(1);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al crear con código de barras duplicado")
    void deberiaFallarAlCrearCodigoDuplicado() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE: Request DTO               │
         *  │ Mock: existsByCodigo -> true       │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT & ASSERT:                      │
         *  │ assertThrows BadRequestExc         │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ VERIFY: save() never called        │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        ProductoRequestDTO request = ProductoRequestDTO.builder()
                .codigoBarras("123456789")
                .build();

        when(productoRepository.existsByCodigoBarrasProducto("123456789")).thenReturn(true);

        // === ACT & ASSERT ===
        assertThrows(BadRequestException.class, () -> productoService.crearProducto(request));
        verify(productoRepository, times(1)).existsByCodigoBarrasProducto("123456789");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al crear con categoría inexistente")
    void deberiaFallarAlCrearCategoriaInexistente() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE: Request DTO               │
         *  │ Mock: existsByCodigo -> false      │
         *  │ Mock: CategoriaRepo.findById->Empty│
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT & ASSERT:                      │
         *  │ assertThrows NotFoundExc           │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        ProductoRequestDTO request = ProductoRequestDTO.builder()
                .categoria(99)
                .unidadMedida(1)
                .codigoBarras("123456789")
                .build();

        when(productoRepository.existsByCodigoBarrasProducto("123456789")).thenReturn(false);
        when(categoriaProductoRepository.findById(99)).thenReturn(Optional.empty());

        // === ACT & ASSERT ===
        assertThrows(ResourceNotFoundException.class, () -> productoService.crearProducto(request));
        verify(categoriaProductoRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al crear con unidad inexistente")
    void deberiaFallarAlCrearUnidadInexistente() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE: Request DTO               │
         *  │ Mock: existsByCodigo -> false      │
         *  │ Mock: CategoriaRepo.findById->Found│
         *  │ Mock: UnidadMedida.findById ->Empty│
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT & ASSERT:                      │
         *  │ assertThrows NotFoundExc           │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        ProductoRequestDTO request = ProductoRequestDTO.builder()
                .categoria(1)
                .unidadMedida(99)
                .codigoBarras("123456789")
                .build();

        when(productoRepository.existsByCodigoBarrasProducto("123456789")).thenReturn(false);
        when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(catHerramientas));
        when(unidadMedidaRepository.findById(99)).thenReturn(Optional.empty());

        // === ACT & ASSERT ===
        assertThrows(ResourceNotFoundException.class, () -> productoService.crearProducto(request));
        verify(unidadMedidaRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debería listar todos los productos")
    void deberiaListarTodos() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE:                           │
         *  │ Mock: findAll() -> returns list    │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT: buscarTodosProductos()        │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ASSERT: Validate size and items    │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        when(productoRepository.findAll()).thenReturn(List.of(producto1, producto2));

        // === ACT ===
        List<ProductoResponseDTO> result = productoService.buscarTodosProductos();

        // === ASSERT ===
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Martillo de Uña", result.get(0).getNombre());
        assertEquals("Alicate Universal", result.get(1).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un producto por ID")
    void deberiaObtenerPorId() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE:                           │
         *  │ Mock: findById -> returns entity   │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT: buscarProductoPorId(101)      │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ASSERT: Verify fields & repository │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        when(productoRepository.findById(101L)).thenReturn(Optional.of(producto1));

        // === ACT ===
        ProductoResponseDTO result = productoService.buscarProductoPorId(101L);

        // === ASSERT ===
        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("Martillo de Uña", result.getNombre());
        verify(productoRepository, times(1)).findById(101L);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar ID inexistente")
    void deberiaFallarAlObtenerInexistente() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE:                           │
         *  │ Mock: findById -> Empty            │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT & ASSERT:                      │
         *  │ assertThrows NotFoundExc           │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        // === ACT & ASSERT ===
        assertThrows(ResourceNotFoundException.class, () -> productoService.buscarProductoPorId(99L));
        verify(productoRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Debería actualizar un producto existente")
    void deberiaActualizarProducto() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE:                           │
         *  │ Mock: findById -> Found            │
         *  │ Mock: findByCodigo -> Empty        │
         *  │ Mock: CategoriaRepo.findById->Found│
         *  │ Mock: UnidadMedida.findById ->Found│
         *  │ Mock: save -> return saved entity   │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT: actualizarProducto()          │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ASSERT: Validate updated values    │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        ProductoRequestDTO request = ProductoRequestDTO.builder()
                .categoria(2)
                .unidadMedida(1)
                .codigoBarras("123456789-M")
                .nombre("Martillo Modificado")
                .stock(12)
                .precioVenta(5500)
                .build();

        when(productoRepository.findById(101L)).thenReturn(Optional.of(producto1));
        when(productoRepository.findByCodigoBarrasProducto("123456789-M")).thenReturn(Optional.empty());
        when(categoriaProductoRepository.findById(2)).thenReturn(Optional.of(catPinturas));
        when(unidadMedidaRepository.findById(1)).thenReturn(Optional.of(unidadUnidad));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        // === ACT ===
        ProductoResponseDTO result = productoService.actualizarProducto(101L, request);

        // === ASSERT ===
        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("Martillo Modificado", result.getNombre());
        assertEquals(12, result.getStock());
        verify(productoRepository, times(1)).findById(101L);
        verify(productoRepository, times(1)).findByCodigoBarrasProducto("123456789-M");
        verify(categoriaProductoRepository, times(1)).findById(2);
        verify(unidadMedidaRepository, times(1)).findById(1);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al actualizar con código que ya tiene otro producto")
    void deberiaFallarAlActualizarCodigoOcupado() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE:                           │
         *  │ Mock: findById -> Found            │
         *  │ Mock: findByCodigo -> occupies another│
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT & ASSERT:                      │
         *  │ assertThrows BadRequestExc         │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ VERIFY: save() never called        │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        ProductoRequestDTO request = ProductoRequestDTO.builder()
                .categoria(1)
                .unidadMedida(1)
                .codigoBarras("987654321")
                .nombre("Martillo Modificado")
                .build();

        when(productoRepository.findById(101L)).thenReturn(Optional.of(producto1));
        when(productoRepository.findByCodigoBarrasProducto("987654321")).thenReturn(Optional.of(producto2));

        // === ACT & ASSERT ===
        assertThrows(BadRequestException.class, () -> productoService.actualizarProducto(101L, request));
        verify(productoRepository, times(1)).findById(101L);
        verify(productoRepository, times(1)).findByCodigoBarrasProducto("987654321");
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería eliminar un producto existente")
    void deberiaEliminarProducto() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE:                           │
         *  │ Mock: existsById -> true           │
         *  │ Mock: deleteById -> void           │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT: eliminarProducto(101)         │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ASSERT: Verify delete call         │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        when(productoRepository.existsById(101L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(101L);

        // === ACT ===
        productoService.eliminarProducto(101L);

        // === ASSERT ===
        verify(productoRepository, times(1)).existsById(101L);
        verify(productoRepository, times(1)).deleteById(101L);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar producto inexistente")
    void deberiaFallarAlEliminarInexistente() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE:                           │
         *  │ Mock: existsById -> false          │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT & ASSERT:                      │
         *  │ assertThrows NotFoundExc           │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        when(productoRepository.existsById(99L)).thenReturn(false);

        // === ACT & ASSERT ===
        assertThrows(ResourceNotFoundException.class, () -> productoService.eliminarProducto(99L));
        verify(productoRepository, times(1)).existsById(99L);
        verify(productoRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Debería descontar stock exitosamente")
    void deberiaDescontarStockExitosamente() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE:                           │
         *  │ Mock: findById -> returns entity   │
         *  │ Mock: save -> return saved entity   │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT: descontarStock(101, 4)        │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ASSERT: Verify stock (10 - 4 = 6)  │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        when(productoRepository.findById(101L)).thenReturn(Optional.of(producto1));
        when(productoRepository.save(any(Producto.class))).thenAnswer(i -> i.getArgument(0));

        // === ACT ===
        productoService.descontarStock(101L, 4);

        // === ASSERT ===
        assertEquals(6, producto1.getStockProducto());
        verify(productoRepository, times(1)).findById(101L);
        verify(productoRepository, times(1)).save(any(Producto.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al descontar stock con cantidad insuficiente")
    void deberiaFallarAlDescontarStockInsuficiente() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE:                           │
         *  │ Mock: findById -> returns entity   │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT & ASSERT:                      │
         *  │ assertThrows BadRequestExc         │
         *  │ (Requested 12, Stock is 10)        │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ VERIFY: save() never called        │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        when(productoRepository.findById(101L)).thenReturn(Optional.of(producto1));

        // === ACT & ASSERT ===
        assertThrows(BadRequestException.class, () -> productoService.descontarStock(101L, 12));
        verify(productoRepository, times(1)).findById(101L);
        verify(productoRepository, never()).save(any(Producto.class));
    }
}
