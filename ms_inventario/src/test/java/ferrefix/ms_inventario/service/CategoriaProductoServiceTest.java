package ferrefix.ms_inventario.service;

import ferrefix.ms_inventario.dto.CategoriaProductoRequestDTO;
import ferrefix.ms_inventario.dto.CategoriaProductoResponseDTO;
import ferrefix.ms_inventario.exception.BadRequestException;
import ferrefix.ms_inventario.exception.ResourceNotFoundException;
import ferrefix.ms_inventario.mapper.CategoriaProductoMapper;
import ferrefix.ms_inventario.model.CategoriaProducto;
import ferrefix.ms_inventario.repository.CategoriaProductoRepository;
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
 * │            CategoriaProductoServiceTest                  │
 * ├──────────────────────────────────────────────────────────┤
 * │  Tests CategoriaProductoService using Mockito.           │
 * │                                                          │
 * │            [CategoriaProductoService]                    │
 * │             /                      \                     │
 * │  (calls repository)           (uses mapper)              │
 * │           ▼                          ▼                   │
 * │ [CategoriaProductoRepository]   [CategoriaProductoMapper] │
 * │          (Mock)                     (Spy)                │
 * └──────────────────────────────────────────────────────────┘
 */
@ExtendWith(MockitoExtension.class)
class CategoriaProductoServiceTest {

    @Mock
    private CategoriaProductoRepository categoriaProductoRepository;

    @Spy
    private CategoriaProductoMapper categoriaProductoMapper = new CategoriaProductoMapper();

    @InjectMocks
    private CategoriaProductoService categoriaProductoService;

    private CategoriaProducto catHerramientas;
    private CategoriaProducto catPinturas;

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
    }

    @Test
    @DisplayName("Debería crear una nueva categoría de producto")
    void deberiaCrearCategoria() {
        /*
         *  FLOW CHART:
         *  ┌─────────────────────────┐
         *  │ ARRANGE: Request DTO    │
         *  │ Mock: exists=false      │
         *  │ Mock: save=entity       │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ACT: crearCategoria()   │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ASSERT: DTO returned &  │
         *  │ Verify Mock interactions│
         *  └─────────────────────────┘
         */

        // === ARRANGE ===
        CategoriaProductoRequestDTO request = CategoriaProductoRequestDTO.builder()
                .nombreCategoria("Herramientas")
                .build();

        when(categoriaProductoRepository.existsByNombreCategoria("Herramientas")).thenReturn(false);
        when(categoriaProductoRepository.save(any(CategoriaProducto.class))).thenReturn(catHerramientas);

        // === ACT ===
        CategoriaProductoResponseDTO result = categoriaProductoService.crearCategoriaProducto(request);

        // === ASSERT ===
        assertNotNull(result);
        assertEquals(1, result.getIdCategoria());
        assertEquals("Herramientas", result.getNombreCategoria());
        verify(categoriaProductoRepository, times(1)).existsByNombreCategoria("Herramientas");
        verify(categoriaProductoRepository, times(1)).save(any(CategoriaProducto.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al crear una categoría con nombre duplicado")
    void deberiaFallarAlCrearNombreDuplicado() {
        /*
         *  FLOW CHART:
         *  ┌─────────────────────────────┐
         *  │ ARRANGE: Request DTO        │
         *  │ Mock: exists=true           │
         *  └──────────────┬──────────────┘
         *                 │
         *                 ▼
         *  ┌─────────────────────────────┐
         *  │ ACT & ASSERT:               │
         *  │ assertThrows BadRequestExc  │
         *  └──────────────┬──────────────┘
         *                 │
         *                 ▼
         *  ┌─────────────────────────────┐
         *  │ VERIFY: save() never called │
         *  └─────────────────────────────┘
         */

        // === ARRANGE ===
        CategoriaProductoRequestDTO request = CategoriaProductoRequestDTO.builder()
                .nombreCategoria("Herramientas")
                .build();

        when(categoriaProductoRepository.existsByNombreCategoria("Herramientas")).thenReturn(true);

        // === ACT & ASSERT ===
        assertThrows(BadRequestException.class, () ->
                categoriaProductoService.crearCategoriaProducto(request)
        );

        verify(categoriaProductoRepository, times(1)).existsByNombreCategoria("Herramientas");
        verify(categoriaProductoRepository, never()).save(any(CategoriaProducto.class));
    }

    @Test
    @DisplayName("Debería listar todas las categorías de productos")
    void deberiaListarTodas() {
        /*
         *  FLOW CHART:
         *  ┌─────────────────────────┐
         *  │ ARRANGE:                │
         *  │ Mock repository.findAll │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ACT:                    │
         *  │ buscarTodasCategorias() │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ASSERT:                 │
         *  │ List size and values    │
         *  └─────────────────────────┘
         */

        // === ARRANGE ===
        when(categoriaProductoRepository.findAll()).thenReturn(List.of(catHerramientas, catPinturas));

        // === ACT ===
        List<CategoriaProductoResponseDTO> result = categoriaProductoService.buscarTodasCategorias();

        // === ASSERT ===
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Herramientas", result.get(0).getNombreCategoria());
        assertEquals("Pinturas", result.get(1).getNombreCategoria());
        verify(categoriaProductoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener una categoría por ID")
    void deberiaObtenerPorId() {
        /*
         *  FLOW CHART:
         *  ┌──────────────────────────┐
         *  │ ARRANGE:                 │
         *  │ Mock repository.findById │
         *  └────────────┬─────────────┘
         *               │
         *               ▼
         *  ┌──────────────────────────┐
         *  │ ACT:                     │
         *  │ buscarCategoriaPorId(1)  │
         *  └────────────┬─────────────┘
         *               │
         *               ▼
         *  ┌──────────────────────────┐
         *  │ ASSERT:                  │
         *  │ Correct ID and name      │
         *  └──────────────────────────┘
         */

        // === ARRANGE ===
        when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(catHerramientas));

        // === ACT ===
        CategoriaProductoResponseDTO result = categoriaProductoService.buscarCategoriaPorId(1);

        // === ASSERT ===
        assertNotNull(result);
        assertEquals(1, result.getIdCategoria());
        assertEquals("Herramientas", result.getNombreCategoria());
        verify(categoriaProductoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar ID inexistente")
    void deberiaFallarAlObtenerInexistente() {
        /*
         *  FLOW CHART:
         *  ┌──────────────────────────┐
         *  │ ARRANGE:                 │
         *  │ Mock findById -> Empty   │
         *  └────────────┬─────────────┘
         *               │
         *               ▼
         *  ┌──────────────────────────┐
         *  │ ACT & ASSERT:            │
         *  │ assertThrows NotFoundExc │
         *  └──────────────────────────┘
         */

        // === ARRANGE ===
        when(categoriaProductoRepository.findById(99)).thenReturn(Optional.empty());

        // === ACT & ASSERT ===
        assertThrows(ResourceNotFoundException.class, () ->
                categoriaProductoService.buscarCategoriaPorId(99)
        );

        verify(categoriaProductoRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debería actualizar una categoría existente")
    void deberiaActualizarCategoria() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE:                           │
         *  │ Mock findById -> Found             │
         *  │ Mock existsByNombre -> false       │
         *  │ Mock save -> return saved entity   │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ACT:                               │
         *  │ actualizarCategoriaProducto()      │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ASSERT:                            │
         *  │ Result matches updated name        │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        CategoriaProductoRequestDTO request = CategoriaProductoRequestDTO.builder()
                .nombreCategoria("Herramientas Eléctricas")
                .build();

        when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(catHerramientas));
        when(categoriaProductoRepository.existsByNombreCategoria("Herramientas Eléctricas")).thenReturn(false);
        when(categoriaProductoRepository.save(any(CategoriaProducto.class))).thenAnswer(i -> i.getArgument(0));

        // === ACT ===
        CategoriaProductoResponseDTO result = categoriaProductoService.actualizarCategoriaProducto(1, request);

        // === ASSERT ===
        assertNotNull(result);
        assertEquals(1, result.getIdCategoria());
        assertEquals("Herramientas Eléctricas", result.getNombreCategoria());
        verify(categoriaProductoRepository, times(1)).findById(1);
        verify(categoriaProductoRepository, times(1)).existsByNombreCategoria("Herramientas Eléctricas");
        verify(categoriaProductoRepository, times(1)).save(any(CategoriaProducto.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al actualizar con nombre ya ocupado por otra categoría")
    void deberiaFallarAlActualizarNombreOcupado() {
        /*
         *  FLOW CHART:
         *  ┌────────────────────────────────────┐
         *  │ ARRANGE:                           │
         *  │ Mock findById -> Found             │
         *  │ Mock existsByNombre -> true        │
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
        CategoriaProductoRequestDTO request = CategoriaProductoRequestDTO.builder()
                .nombreCategoria("Pinturas")
                .build();

        when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(catHerramientas));
        when(categoriaProductoRepository.existsByNombreCategoria("Pinturas")).thenReturn(true);

        // === ACT & ASSERT ===
        assertThrows(BadRequestException.class, () ->
                categoriaProductoService.actualizarCategoriaProducto(1, request)
        );

        verify(categoriaProductoRepository, times(1)).findById(1);
        verify(categoriaProductoRepository, times(1)).existsByNombreCategoria("Pinturas");
        verify(categoriaProductoRepository, never()).save(any(CategoriaProducto.class));
    }

    @Test
    @DisplayName("Debería eliminar una categoría existente")
    void deberiaEliminarCategoria() {
        /*
         *  FLOW CHART:
         *  ┌───────────────────────────┐
         *  │ ARRANGE:                  │
         *  │ Mock existsById -> true   │
         *  │ Mock deleteById -> void   │
         *  └─────────────┬─────────────┘
         *                │
         *                ▼
         *  ┌───────────────────────────┐
         *  │ ACT:                      │
         *  │ eliminarCategoria(1)      │
         *  └─────────────┬─────────────┘
         *                │
         *                ▼
         *  ┌───────────────────────────┐
         *  │ ASSERT: Verify delete call│
         *  └───────────────────────────┘
         */

        // === ARRANGE ===
        when(categoriaProductoRepository.existsById(1)).thenReturn(true);
        doNothing().when(categoriaProductoRepository).deleteById(1);

        // === ACT ===
        categoriaProductoService.eliminarCategoriaProducto(1);

        // === ASSERT ===
        verify(categoriaProductoRepository, times(1)).existsById(1);
        verify(categoriaProductoRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar categoría inexistente")
    void deberiaFallarAlEliminarInexistente() {
        /*
         *  FLOW CHART:
         *  ┌───────────────────────────┐
         *  │ ARRANGE:                  │
         *  │ Mock existsById -> false  │
         *  └─────────────┬─────────────┘
         *                │
         *                ▼
         *  ┌───────────────────────────┐
         *  │ ACT & ASSERT:             │
         *  │ assertThrows NotFoundExc  │
         *  └─────────────┬─────────────┘
         *                │
         *                ▼
         *  ┌───────────────────────────┐
         *  │ VERIFY: delete never runs │
         *  └───────────────────────────┘
         */

        // === ARRANGE ===
        when(categoriaProductoRepository.existsById(99)).thenReturn(false);

        // === ACT & ASSERT ===
        assertThrows(ResourceNotFoundException.class, () ->
                categoriaProductoService.eliminarCategoriaProducto(99)
        );

        verify(categoriaProductoRepository, times(1)).existsById(99);
        verify(categoriaProductoRepository, never()).deleteById(anyInt());
    }
}
