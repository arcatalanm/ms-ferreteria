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

/**
 * ┌──────────────────────────────────────────────────────────┐
 * │              UnidadMedidaServiceTest                     │
 * ├──────────────────────────────────────────────────────────┤
 * │  Tests UnidadMedidaService using Mockito.                │
 * │                                                          │
 * │              [UnidadMedidaService]                       │
 * │               /                  \                       │
 * │  (calls repository)         (uses mapper)                │
 * │             ▼                      ▼                     │
 * │   [UnidadMedidaRepository]   [UnidadMedidaMapper]        │
 * │            (Mock)                  (Spy)                 │
 * └──────────────────────────────────────────────────────────┘
 */
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
         *  │ ACT: crearUnidad()      │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ASSERT: DTO returned &  │
         *  │ Verify Mock interactions│
         *  └─────────────────────────┘
         */

        // === ARRANGE ===
        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Unidad")
                .build();

        when(unidadMedidaRepository.existsByNombreUnidadMedida("Unidad")).thenReturn(false);
        when(unidadMedidaRepository.save(any(UnidadMedida.class))).thenReturn(unidadUnidad);

        // === ACT ===
        UnidadMedidaResponseDTO result = unidadMedidaService.crearUnidadMedida(request);

        // === ASSERT ===
        assertNotNull(result);
        assertEquals(1, result.getIdUnidadMedida());
        assertEquals("Unidad", result.getNombreUnidadMedida());
        verify(unidadMedidaRepository, times(1)).existsByNombreUnidadMedida("Unidad");
        verify(unidadMedidaRepository, times(1)).save(any(UnidadMedida.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al crear una unidad con nombre duplicado")
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
        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Unidad")
                .build();

        when(unidadMedidaRepository.existsByNombreUnidadMedida("Unidad")).thenReturn(true);

        // === ACT & ASSERT ===
        assertThrows(BadRequestException.class, () ->
                unidadMedidaService.crearUnidadMedida(request)
        );

        verify(unidadMedidaRepository, times(1)).existsByNombreUnidadMedida("Unidad");
        verify(unidadMedidaRepository, never()).save(any(UnidadMedida.class));
    }

    @Test
    @DisplayName("Debería listar todas las unidades de medida")
    void deberiaListarTodas() {
        /*
         *  FLOW CHART:
         *  ┌─────────────────────────┐
         *  │ ARRANGE:                │
         *  │ Mock repository.findAll │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────────┐
         *  │ ACT:                        │
         *  │ buscarTodasUnidadesMedida() │
         *  └────────────┬────────────┘
         *               │
         *               ▼
         *  ┌─────────────────────────┐
         *  │ ASSERT:                 │
         *  │ List size and values    │
         *  └─────────────────────────┘
         */

        // === ARRANGE ===
        when(unidadMedidaRepository.findAll()).thenReturn(List.of(unidadUnidad, unidadMetros));

        // === ACT ===
        List<UnidadMedidaResponseDTO> result = unidadMedidaService.buscarTodasUnidadesMedida();

        // === ASSERT ===
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Unidad", result.get(0).getNombreUnidadMedida());
        assertEquals("Metros", result.get(1).getNombreUnidadMedida());
        verify(unidadMedidaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener una unidad de medida por ID")
    void deberiaObtenerPorId() {
        /*
         *  FLOW CHART:
         *  ┌──────────────────────────┐
         *  │ ARRANGE:                 │
         *  │ Mock repository.findById │
         *  └────────────┬─────────────┘
         *               │
         *               ▼
         *  ┌───────────────────────────┐
         *  │ ACT:                      │
         *  │ buscarUnidadMedidaPorId(1)│
         *  └────────────┬───────────┘
         *               │
         *               ▼
         *  ┌──────────────────────────┐
         *  │ ASSERT:                  │
         *  │ Correct ID and name      │
         *  └──────────────────────────┘
         */

        // === ARRANGE ===
        when(unidadMedidaRepository.findById(1)).thenReturn(Optional.of(unidadUnidad));

        // === ACT ===
        UnidadMedidaResponseDTO result = unidadMedidaService.buscarUnidadMedidaPorId(1);

        // === ASSERT ===
        assertNotNull(result);
        assertEquals(1, result.getIdUnidadMedida());
        assertEquals("Unidad", result.getNombreUnidadMedida());
        verify(unidadMedidaRepository, times(1)).findById(1);
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
        when(unidadMedidaRepository.findById(99)).thenReturn(Optional.empty());

        // === ACT & ASSERT ===
        assertThrows(ResourceNotFoundException.class, () ->
                unidadMedidaService.buscarUnidadMedidaPorId(99)
        );

        verify(unidadMedidaRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debería actualizar una unidad de medida existente")
    void deberiaActualizarUnidadMedida() {
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
         *  │ actualizarUnidadMedida()           │
         *  └─────────────────┬──────────────────┘
         *                    │
         *                    ▼
         *  ┌────────────────────────────────────┐
         *  │ ASSERT:                            │
         *  │ Result matches updated name        │
         *  └────────────────────────────────────┘
         */

        // === ARRANGE ===
        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Unidades")
                .build();

        when(unidadMedidaRepository.findById(1)).thenReturn(Optional.of(unidadUnidad));
        when(unidadMedidaRepository.existsByNombreUnidadMedida("Unidades")).thenReturn(false);
        when(unidadMedidaRepository.save(any(UnidadMedida.class))).thenAnswer(i -> i.getArgument(0));

        // === ACT ===
        UnidadMedidaResponseDTO result = unidadMedidaService.actualizarUnidadMedida(1, request);

        // === ASSERT ===
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
        UnidadMedidaRequestDTO request = UnidadMedidaRequestDTO.builder()
                .nombreUnidadMedida("Metros")
                .build();

        when(unidadMedidaRepository.findById(1)).thenReturn(Optional.of(unidadUnidad));
        when(unidadMedidaRepository.existsByNombreUnidadMedida("Metros")).thenReturn(true);

        // === ACT & ASSERT ===
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
         *  │ eliminarUnidadMedida(1)   │
         *  └─────────────┬─────────────┘
         *                │
         *                ▼
         *  ┌───────────────────────────┐
         *  │ ASSERT: Verify delete call│
         *  └───────────────────────────┘
         */

        // === ARRANGE ===
        when(unidadMedidaRepository.existsById(1)).thenReturn(true);
        doNothing().when(unidadMedidaRepository).deleteById(1);

        // === ACT ===
        unidadMedidaService.eliminarUnidadMedida(1);

        // === ASSERT ===
        verify(unidadMedidaRepository, times(1)).existsById(1);
        verify(unidadMedidaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar unidad inexistente")
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
        when(unidadMedidaRepository.existsById(99)).thenReturn(false);

        // === ACT & ASSERT ===
        assertThrows(ResourceNotFoundException.class, () ->
                unidadMedidaService.eliminarUnidadMedida(99)
        );

        verify(unidadMedidaRepository, times(1)).existsById(99);
        verify(unidadMedidaRepository, never()).deleteById(anyInt());
    }
}
