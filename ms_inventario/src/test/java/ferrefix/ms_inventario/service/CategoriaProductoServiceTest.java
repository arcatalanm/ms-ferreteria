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

        CategoriaProductoRequestDTO request = CategoriaProductoRequestDTO.builder()
                .nombreCategoria("Herramientas")
                .build();

        when(categoriaProductoRepository.existsByNombreCategoria("Herramientas")).thenReturn(false);
        when(categoriaProductoRepository.save(any(CategoriaProducto.class))).thenReturn(catHerramientas);

        CategoriaProductoResponseDTO result = categoriaProductoService.crearCategoriaProducto(request);

        assertNotNull(result);
        assertEquals(1, result.getIdCategoria());
        assertEquals("Herramientas", result.getNombreCategoria());
        verify(categoriaProductoRepository, times(1)).existsByNombreCategoria("Herramientas");
        verify(categoriaProductoRepository, times(1)).save(any(CategoriaProducto.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al crear una categoría con nombre duplicado")
    void deberiaFallarAlCrearNombreDuplicado() {

        CategoriaProductoRequestDTO request = CategoriaProductoRequestDTO.builder()
                .nombreCategoria("Herramientas")
                .build();

        when(categoriaProductoRepository.existsByNombreCategoria("Herramientas")).thenReturn(true);

        assertThrows(BadRequestException.class, () ->
                categoriaProductoService.crearCategoriaProducto(request)
        );

        verify(categoriaProductoRepository, times(1)).existsByNombreCategoria("Herramientas");
        verify(categoriaProductoRepository, never()).save(any(CategoriaProducto.class));
    }

    @Test
    @DisplayName("Debería listar todas las categorías de productos")
    void deberiaListarTodas() {

        when(categoriaProductoRepository.findAll()).thenReturn(List.of(catHerramientas, catPinturas));

        List<CategoriaProductoResponseDTO> result = categoriaProductoService.buscarTodasCategorias();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Herramientas", result.get(0).getNombreCategoria());
        assertEquals("Pinturas", result.get(1).getNombreCategoria());
        verify(categoriaProductoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener una categoría por ID")
    void deberiaObtenerPorId() {

        when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(catHerramientas));

        CategoriaProductoResponseDTO result = categoriaProductoService.buscarCategoriaPorId(1);

        assertNotNull(result);
        assertEquals(1, result.getIdCategoria());
        assertEquals("Herramientas", result.getNombreCategoria());
        verify(categoriaProductoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar ID inexistente")
    void deberiaFallarAlObtenerInexistente() {

        when(categoriaProductoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                categoriaProductoService.buscarCategoriaPorId(99)
        );

        verify(categoriaProductoRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debería actualizar una categoría existente")
    void deberiaActualizarCategoria() {

        CategoriaProductoRequestDTO request = CategoriaProductoRequestDTO.builder()
                .nombreCategoria("Herramientas Eléctricas")
                .build();

        when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(catHerramientas));
        when(categoriaProductoRepository.existsByNombreCategoria("Herramientas Eléctricas")).thenReturn(false);
        when(categoriaProductoRepository.save(any(CategoriaProducto.class))).thenAnswer(i -> i.getArgument(0));

        CategoriaProductoResponseDTO result = categoriaProductoService.actualizarCategoriaProducto(1, request);

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

        CategoriaProductoRequestDTO request = CategoriaProductoRequestDTO.builder()
                .nombreCategoria("Pinturas")
                .build();

        when(categoriaProductoRepository.findById(1)).thenReturn(Optional.of(catHerramientas));
        when(categoriaProductoRepository.existsByNombreCategoria("Pinturas")).thenReturn(true);

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

        when(categoriaProductoRepository.existsById(1)).thenReturn(true);
        doNothing().when(categoriaProductoRepository).deleteById(1);

        categoriaProductoService.eliminarCategoriaProducto(1);

        verify(categoriaProductoRepository, times(1)).existsById(1);
        verify(categoriaProductoRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar categoría inexistente")
    void deberiaFallarAlEliminarInexistente() {

        when(categoriaProductoRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                categoriaProductoService.eliminarCategoriaProducto(99)
        );

        verify(categoriaProductoRepository, times(1)).existsById(99);
        verify(categoriaProductoRepository, never()).deleteById(anyInt());
    }
}
