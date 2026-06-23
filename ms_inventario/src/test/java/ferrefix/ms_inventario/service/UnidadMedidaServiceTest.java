package ferrefix.ms_inventario.service;

import ferrefix.ms_inventario.dto.UnidadMedidaRequestDTO;
import ferrefix.ms_inventario.dto.UnidadMedidaResponseDTO;
import ferrefix.ms_inventario.exception.BadRequestException;
import ferrefix.ms_inventario.exception.ResourceNotFoundException;
import ferrefix.ms_inventario.mapper.UnidadMedidaMapper;
import ferrefix.ms_inventario.model.UnidadMedida;
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

@ExtendWith(MockitoExtension.class)
class UnidadMedidaServiceTest {

    @Mock
    private UnidadMedidaRepository unidadMedidaRepository;

    @Spy
    private UnidadMedidaMapper unidadMedidaMapper = new UnidadMedidaMapper();

    @InjectMocks
    private UnidadMedidaService unidadMedidaService;

    private UnidadMedida unidadUnidad;
    private UnidadMedida unidadMetros;

    @BeforeEach
    void setUp() {
        unidadUnidad = UnidadMedida.builder()
                .idUnidadMedida(1)
                .nombreUnidadMedida("Unidad")
                .build();

        unidadMetros = UnidadMedida.builder()
                .idUnidadMedida(2)
                .nombreUnidadMedida("Metros")
                .build();
    }

    @Test
    @DisplayName("Debería crear una nueva unidad de medida")
    void deberiaCrearUnidadMedida() {

        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Unidad")
                .build();

        when(unidadMedidaRepository.existsByNombreUnidadMedida("Unidad")).thenReturn(false);
        when(unidadMedidaRepository.save(any(UnidadMedida.class))).thenReturn(unidadUnidad);

        UnidadMedidaResponseDTO result = unidadMedidaService.crearUnidadMedida(request);

        assertNotNull(result);
        assertEquals(1, result.getIdUnidadMedida());
        assertEquals("Unidad", result.getNombreUnidadMedida());
        verify(unidadMedidaRepository, times(1)).existsByNombreUnidadMedida("Unidad");
        verify(unidadMedidaRepository, times(1)).save(any(UnidadMedida.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al crear una unidad con nombre duplicado")
    void deberiaFallarAlCrearNombreDuplicado() {

        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Unidad")
                .build();

        when(unidadMedidaRepository.existsByNombreUnidadMedida("Unidad")).thenReturn(true);

        assertThrows(BadRequestException.class, () ->
                unidadMedidaService.crearUnidadMedida(request)
        );

        verify(unidadMedidaRepository, times(1)).existsByNombreUnidadMedida("Unidad");
        verify(unidadMedidaRepository, never()).save(any(UnidadMedida.class));
    }

    @Test
    @DisplayName("Debería listar todas las unidades de medida")
    void deberiaListarTodas() {

        when(unidadMedidaRepository.findAll()).thenReturn(List.of(unidadUnidad, unidadMetros));

        List<UnidadMedidaResponseDTO> result = unidadMedidaService.buscarTodasUnidadesMedida();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Unidad", result.get(0).getNombreUnidadMedida());
        assertEquals("Metros", result.get(1).getNombreUnidadMedida());
        verify(unidadMedidaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener una unidad de medida por ID")
    void deberiaObtenerPorId() {

        when(unidadMedidaRepository.findById(1)).thenReturn(Optional.of(unidadUnidad));

        UnidadMedidaResponseDTO result = unidadMedidaService.buscarUnidadMedidaPorId(1);

        assertNotNull(result);
        assertEquals(1, result.getIdUnidadMedida());
        assertEquals("Unidad", result.getNombreUnidadMedida());
        verify(unidadMedidaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar ID inexistente")
    void deberiaFallarAlObtenerInexistente() {

        when(unidadMedidaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                unidadMedidaService.buscarUnidadMedidaPorId(99)
        );

        verify(unidadMedidaRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debería actualizar una unidad de medida existente")
    void deberiaActualizarUnidadMedida() {

        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Unidades")
                .build();

        when(unidadMedidaRepository.findById(1)).thenReturn(Optional.of(unidadUnidad));
        when(unidadMedidaRepository.existsByNombreUnidadMedida("Unidades")).thenReturn(false);
        when(unidadMedidaRepository.save(any(UnidadMedida.class))).thenAnswer(i -> i.getArgument(0));

        UnidadMedidaResponseDTO result = unidadMedidaService.actualizarUnidadMedida(1, request);

        assertNotNull(result);
        assertEquals(1, result.getIdUnidadMedida());
        assertEquals("Unidades", result.getNombreUnidadMedida());
        verify(unidadMedidaRepository, times(1)).findById(1);
        verify(unidadMedidaRepository, times(1)).existsByNombreUnidadMedida("Unidades");
        verify(unidadMedidaRepository, times(1)).save(any(UnidadMedida.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al actualizar con nombre ya ocupado por otra unidad")
    void deberiaFallarAlActualizarNombreOcupado() {

        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Metros")
                .build();

        when(unidadMedidaRepository.findById(1)).thenReturn(Optional.of(unidadUnidad));
        when(unidadMedidaRepository.existsByNombreUnidadMedida("Metros")).thenReturn(true);

        assertThrows(BadRequestException.class, () ->
                unidadMedidaService.actualizarUnidadMedida(1, request)
        );

        verify(unidadMedidaRepository, times(1)).findById(1);
        verify(unidadMedidaRepository, times(1)).existsByNombreUnidadMedida("Metros");
        verify(unidadMedidaRepository, never()).save(any(UnidadMedida.class));
    }

    @Test
    @DisplayName("Debería eliminar una unidad de medida existente")
    void deberiaEliminarUnidadMedida() {

        when(unidadMedidaRepository.existsById(1)).thenReturn(true);
        doNothing().when(unidadMedidaRepository).deleteById(1);

        unidadMedidaService.eliminarUnidadMedida(1);

        verify(unidadMedidaRepository, times(1)).existsById(1);
        verify(unidadMedidaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar unidad inexistente")
    void deberiaFallarAlEliminarInexistente() {

        when(unidadMedidaRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                unidadMedidaService.eliminarUnidadMedida(99)
        );

        verify(unidadMedidaRepository, times(1)).existsById(99);
        verify(unidadMedidaRepository, never()).deleteById(anyInt());
    }
}
