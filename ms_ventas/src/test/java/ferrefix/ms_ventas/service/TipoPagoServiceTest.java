package ferrefix.ms_ventas.service;

import ferrefix.ms_ventas.dto.TipoPagoRequestDTO;
import ferrefix.ms_ventas.dto.TipoPagoResponseDTO;
import ferrefix.ms_ventas.exception.BadRequestException;
import ferrefix.ms_ventas.exception.ResourceNotFoundException;
import ferrefix.ms_ventas.mapper.TipoPagoMapper;
import ferrefix.ms_ventas.model.TipoPago;
import ferrefix.ms_ventas.repository.TipoPagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TipoPagoServiceTest
 *
 * Visual Flowchart of the Service Unit Testing Architecture:
 *
 *   [Test Case] --------(Invokes Method)--------> [TipoPagoService]
 *                                                       |
 *                                          ┌────────────┴────────────┐
 *                                          v (Mock)                  v (Mock)
 *                                    [TipoPagoMapper]        [TipoPagoRepository]
 *                                                                    |
 *                                                           (Returns Mock Entity)
 *                                                                    v
 *   [Assert Result] <-------(Asserts / Exceptions)-------------------┘
 *
 */
@ExtendWith(MockitoExtension.class)
class TipoPagoServiceTest {

    @Mock
    private TipoPagoRepository tipoPagoRepository;

    @Mock
    private TipoPagoMapper tipoPagoMapper;

    @InjectMocks
    private TipoPagoService tipoPagoService;

    private TipoPago tipoPago;
    private TipoPagoRequestDTO requestDTO;
    private TipoPagoResponseDTO responseDTO;

    // ==========================================
    // SETUP & INITIALIZATION
    // ==========================================
    @BeforeEach
    void setUp() {
        tipoPago = TipoPago.builder()
                .idTipoPago(1)
                .nombreTipoPago("Efectivo")
                .build();

        requestDTO = TipoPagoRequestDTO.builder()
                .nombreTipoPago("Efectivo")
                .build();

        responseDTO = TipoPagoResponseDTO.builder()
                .idTipoPago(1)
                .nombreTipoPago("Efectivo")
                .build();
    }

    /**
     * deberiaListarTodos:
     * Verifies listing all payment types maps entity objects to response DTOs.
     */
    @Test
    @DisplayName("Debería listar todos los tipos de pago")
    void deberiaListarTodos() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.findAll()).thenReturn(List.of(tipoPago));
        when(tipoPagoMapper.toResponseDTO(tipoPago)).thenReturn(responseDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        List<TipoPagoResponseDTO> result = tipoPagoService.obtenerTodos();

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Efectivo", result.get(0).getNombreTipoPago());
        verify(tipoPagoRepository, times(1)).findAll();
    }

    /**
     * deberiaObtenerPorId:
     * Verifies finding a payment type by ID maps the found entity to response DTO.
     */
    @Test
    @DisplayName("Debería obtener tipo de pago por ID")
    void deberiaObtenerPorId() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        when(tipoPagoMapper.toResponseDTO(tipoPago)).thenReturn(responseDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        TipoPagoResponseDTO result = tipoPagoService.obtenerPorId(1);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals("Efectivo", result.getNombreTipoPago());
        verify(tipoPagoRepository, times(1)).findById(1);
    }

    /**
     * deberiaLanzarExcepcionAlObtenerInexistente:
     * Verifies ResourceNotFoundException when querying a non-existent ID.
     */
    @Test
    @DisplayName("Debería lanzar excepción al obtener ID inexistente")
    void deberiaLanzarExcepcionAlObtenerInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.findById(99)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () -> tipoPagoService.obtenerPorId(99));
        verify(tipoPagoRepository, times(1)).findById(99);
    }

    /**
     * deberiaCrearExitosamente:
     * Verifies successful registration of a new unique payment type.
     */
    @Test
    @DisplayName("Debería crear un tipo de pago exitosamente")
    void deberiaCrearExitosamente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.existsByNombreTipoPago("Efectivo")).thenReturn(false);
        when(tipoPagoMapper.toEntity(requestDTO)).thenReturn(tipoPago);
        when(tipoPagoRepository.save(tipoPago)).thenReturn(tipoPago);
        when(tipoPagoMapper.toResponseDTO(tipoPago)).thenReturn(responseDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        TipoPagoResponseDTO result = tipoPagoService.crear(requestDTO);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals("Efectivo", result.getNombreTipoPago());
        verify(tipoPagoRepository, times(1)).existsByNombreTipoPago("Efectivo");
        verify(tipoPagoRepository, times(1)).save(tipoPago);
    }

    /**
     * deberiaLanzarExcepcionAlCrearNombreExistente:
     * Verifies BadRequestException when creating a duplicate payment type name.
     */
    @Test
    @DisplayName("Debería lanzar excepción si el nombre de tipo de pago ya existe al crear")
    void deberiaLanzarExcepcionAlCrearNombreExistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.existsByNombreTipoPago("Efectivo")).thenReturn(true);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () -> tipoPagoService.crear(requestDTO));
        verify(tipoPagoRepository, times(1)).existsByNombreTipoPago("Efectivo");
        verify(tipoPagoRepository, never()).save(any());
    }

    /**
     * deberiaActualizarExitosamente:
     * Verifies successful update of a payment type's name attribute.
     */
    @Test
    @DisplayName("Debería actualizar un tipo de pago exitosamente")
    void deberiaActualizarExitosamente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        TipoPago existente = TipoPago.builder().idTipoPago(1).nombreTipoPago("Efectivo").build();
        TipoPagoRequestDTO requestActualizar = TipoPagoRequestDTO.builder().nombreTipoPago("Tarjeta").build();
        TipoPago actualizado = TipoPago.builder().idTipoPago(1).nombreTipoPago("Tarjeta").build();
        TipoPagoResponseDTO responseActualizado = TipoPagoResponseDTO.builder().idTipoPago(1).nombreTipoPago("Tarjeta").build();

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(existente));
        when(tipoPagoRepository.findByNombreTipoPago("Tarjeta")).thenReturn(Optional.empty());
        when(tipoPagoRepository.save(existente)).thenReturn(actualizado);
        when(tipoPagoMapper.toResponseDTO(actualizado)).thenReturn(responseActualizado);

        // ==========================================
        // 2. ACT
        // ==========================================
        TipoPagoResponseDTO result = tipoPagoService.actualizar(1, requestActualizar);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals("Tarjeta", result.getNombreTipoPago());
        verify(tipoPagoRepository, times(1)).findById(1);
        verify(tipoPagoRepository, times(1)).findByNombreTipoPago("Tarjeta");
        verify(tipoPagoRepository, times(1)).save(existente);
    }

    /**
     * deberiaLanzarExcepcionAlActualizarInexistente:
     * Verifies ResourceNotFoundException when updating a non-existent payment type.
     */
    @Test
    @DisplayName("Debería lanzar excepción si el tipo de pago a actualizar no existe")
    void deberiaLanzarExcepcionAlActualizarInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.findById(99)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () -> tipoPagoService.actualizar(99, requestDTO));
        verify(tipoPagoRepository, times(1)).findById(99);
        verify(tipoPagoRepository, never()).save(any());
    }

    /**
     * deberiaLanzarExcepcionAlActualizarNombreExistenteEnOtro:
     * Verifies BadRequestException when updating a payment type's name to a name owned by another payment type.
     */
    @Test
    @DisplayName("Debería lanzar excepción si el nombre de tipo de pago ya existe en otro registro al actualizar")
    void deberiaLanzarExcepcionAlActualizarNombreExistenteEnOtro() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        TipoPago existente = TipoPago.builder().idTipoPago(1).nombreTipoPago("Efectivo").build();
        TipoPago otro = TipoPago.builder().idTipoPago(2).nombreTipoPago("Tarjeta").build();
        TipoPagoRequestDTO requestActualizar = TipoPagoRequestDTO.builder().nombreTipoPago("Tarjeta").build();

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(existente));
        when(tipoPagoRepository.findByNombreTipoPago("Tarjeta")).thenReturn(Optional.of(otro));

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () -> tipoPagoService.actualizar(1, requestActualizar));
        verify(tipoPagoRepository, times(1)).findById(1);
        verify(tipoPagoRepository, times(1)).findByNombreTipoPago("Tarjeta");
        verify(tipoPagoRepository, never()).save(any());
    }

    /**
     * deberiaEliminarExitosamente:
     * Verifies successful deletion of an existing payment type.
     */
    @Test
    @DisplayName("Debería eliminar un tipo de pago exitosamente")
    void deberiaEliminarExitosamente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        doNothing().when(tipoPagoRepository).delete(tipoPago);

        // ==========================================
        // 2. ACT
        // ==========================================
        tipoPagoService.eliminar(1);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        verify(tipoPagoRepository, times(1)).findById(1);
        verify(tipoPagoRepository, times(1)).delete(tipoPago);
    }

    /**
     * deberiaLanzarExcepcionAlEliminarInexistente:
     * Verifies ResourceNotFoundException when trying to delete a non-existent ID.
     */
    @Test
    @DisplayName("Debería lanzar excepción al eliminar un tipo de pago inexistente")
    void deberiaLanzarExcepcionAlEliminarInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(tipoPagoRepository.findById(99)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () -> tipoPagoService.eliminar(99));
        verify(tipoPagoRepository, times(1)).findById(99);
        verify(tipoPagoRepository, never()).delete(any());
    }
}
