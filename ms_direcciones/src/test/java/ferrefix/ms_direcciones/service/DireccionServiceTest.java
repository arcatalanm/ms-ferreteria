package ferrefix.ms_direcciones.service;

import ferrefix.ms_direcciones.dto.DireccionRequestDTO;
import ferrefix.ms_direcciones.dto.DireccionResponseDTO;
import ferrefix.ms_direcciones.exception.ResourceNotFoundException;
import ferrefix.ms_direcciones.mapper.DireccionMapper;
import ferrefix.ms_direcciones.model.Direccion;
import ferrefix.ms_direcciones.repository.DireccionRepository;
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
 * DireccionServiceTest
 * Pruebas unitarias para el servicio {@link DireccionService}.
 *
 * Flujo de Decisiones en Procesos:
 *
 *   [Servicio Direcciones]
 *          │
 *          ├─── crearDireccion(dto) ────► direccionRepository.save(entity)
 *          │
 *          ├─── buscarTodas() ──────────► direccionRepository.findAll()
 *          │
 *          ├─── buscarPorId(id) ────────► direccionRepository.findById(id)
 *          │                                  └─── [No Existe] ──► (Lanza ResourceNotFoundException)
 *          │
 *          ├─── actualizarDireccion(id, dto)
 *          │         └─── direccionRepository.findById(id)
 *          │                   ├─── [No Existe] ──► (Lanza ResourceNotFoundException)
 *          │                   └─── [Existe] ─────► direccionRepository.save(entity)
 *          │
 *          └─── eliminarDireccion(id)
 *                    └─── direccionRepository.existsById(id)
 *                              ├─── [No Existe] ──► (Lanza ResourceNotFoundException)
 *                              └─── [Existe] ─────► direccionRepository.deleteById(id)
 *
 */
@ExtendWith(MockitoExtension.class)
class DireccionServiceTest {

    @Mock
    private DireccionRepository direccionRepository;

    @Spy
    private DireccionMapper direccionMapper = new DireccionMapper();

    @InjectMocks
    private DireccionService direccionService;

    private Direccion direccion1;
    private Direccion direccion2;

    @BeforeEach
    void setUp() {
        // Arrange - Inicialización de entidades mockeadas
        direccion1 = Direccion.builder()
                .idDireccion(1L)
                .calle("Av. Providencia")
                .numero(1234)
                .departamento("501")
                .comuna("Providencia")
                .ciudad("Santiago")
                .build();

        direccion2 = Direccion.builder()
                .idDireccion(2L)
                .calle("Alameda")
                .numero(345)
                .comuna("Santiago")
                .ciudad("Santiago")
                .build();
    }

    /**
     * Test: deberiaCrearDireccion
     *
     *   Arrange: Crear DTO de petición y configurar save en el repositorio.
     *   Act: Invocar crearDireccion().
     *   Assert: Verificar ID de la dirección y formato de dirección completa concatenado.
     *
     */
    @Test
    @DisplayName("Debería crear una nueva dirección")
    void deberiaCrearDireccion() {
        // 1. Arrange
        DireccionRequestDTO request = DireccionRequestDTO.builder()
                .calle("Av. Providencia")
                .numero(1234)
                .departamento("501")
                .comuna("Providencia")
                .ciudad("Santiago")
                .build();

        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccion1);

        // 2. Act
        DireccionResponseDTO result = direccionService.crearDireccion(request);

        // 3. Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdDireccion());
        assertEquals("Av. Providencia 1234, Depto 501, Providencia, Santiago", result.getDireccionCompleta());
        verify(direccionRepository, times(1)).save(any(Direccion.class));
    }

    /**
     * Test: deberiaListarTodas
     *
     *   Arrange: Configurar findAll en el repositorio retornando la lista mock.
     *   Act: Invocar buscarTodas().
     *   Assert: Validar tamaño 2 y correspondencia de las calles en la lista.
     *
     */
    @Test
    @DisplayName("Debería listar todas las direcciones")
    void deberiaListarTodas() {
        // 1. Arrange
        when(direccionRepository.findAll()).thenReturn(List.of(direccion1, direccion2));

        // 2. Act
        List<DireccionResponseDTO> result = direccionService.buscarTodas();

        // 3. Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Av. Providencia", result.get(0).getCalle());
        assertEquals("Alameda", result.get(1).getCalle());
        verify(direccionRepository, times(1)).findAll();
    }

    /**
     * Test: deberiaObtenerPorId
     *
     *   Arrange: Configurar findById en el repositorio retornando direccion1.
     *   Act: Invocar buscarPorId(1).
     *   Assert: Verificar ID e información del DTO.
     *
     */
    @Test
    @DisplayName("Debería obtener dirección por ID")
    void deberiaObtenerPorId() {
        // 1. Arrange
        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion1));

        // 2. Act
        DireccionResponseDTO result = direccionService.buscarPorId(1L);

        // 3. Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdDireccion());
        assertEquals("Av. Providencia 1234, Depto 501, Providencia, Santiago", result.getDireccionCompleta());
        verify(direccionRepository, times(1)).findById(1L);
    }

    /**
     * Test: deberiaFallarAlObtenerPorIdInexistente
     *
     *   Arrange: Simular ID no encontrado (Optional.empty()).
     *   Act & Assert: Intentar obtener y comprobar ResourceNotFoundException.
     *
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar ID inexistente")
    void deberiaFallarAlObtenerPorIdInexistente() {
        // 1. Arrange
        when(direccionRepository.findById(99L)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        assertThrows(ResourceNotFoundException.class, () -> direccionService.buscarPorId(99L));
        verify(direccionRepository, times(1)).findById(99L);
    }

    /**
     * Test: deberiaActualizarDireccion
     *
     *   Arrange: Simular búsqueda exitosa de dirección y guardado del DTO modificado.
     *   Act: Invocar actualizarDireccion().
     *   Assert: Verificar calle modificada y departamento nuevo en DTO retornado.
     *
     */
    @Test
    @DisplayName("Debería actualizar una dirección existente")
    void deberiaActualizarDireccion() {
        // 1. Arrange
        DireccionRequestDTO request = DireccionRequestDTO.builder()
                .calle("Av. Providencia Modificada")
                .numero(1234)
                .departamento("502")
                .comuna("Providencia")
                .ciudad("Santiago")
                .build();

        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion1));
        when(direccionRepository.save(any(Direccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act
        DireccionResponseDTO result = direccionService.actualizarDireccion(1L, request);

        // 3. Assert
        assertNotNull(result);
        assertEquals(1L, result.getIdDireccion());
        assertEquals("Av. Providencia Modificada", result.getCalle());
        assertEquals("502", result.getDepartamento());
        verify(direccionRepository, times(1)).findById(1L);
        verify(direccionRepository, times(1)).save(any(Direccion.class));
    }

    /**
     * Test: deberiaFallarAlActualizarInexistente
     *
     *   Arrange: Simular ID no encontrado al actualizar.
     *   Act & Assert: Intentar actualizar y validar ResourceNotFoundException.
     *
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al actualizar dirección inexistente")
    void deberiaFallarAlActualizarInexistente() {
        // 1. Arrange
        DireccionRequestDTO request = DireccionRequestDTO.builder()
                .calle("Calle Inexistente")
                .numero(100)
                .comuna("Comuna")
                .ciudad("Ciudad")
                .build();

        when(direccionRepository.findById(99L)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        assertThrows(ResourceNotFoundException.class, () -> direccionService.actualizarDireccion(99L, request));
        verify(direccionRepository, times(1)).findById(99L);
        verify(direccionRepository, never()).save(any(Direccion.class));
    }

    /**
     * Test: deberiaEliminarDireccion
     *
     *   Arrange: Simular que existe ID y configurar eliminación void.
     *   Act: Invocar eliminarDireccion().
     *   Assert: Verificar llamadas a existsById y deleteById.
     *
     */
    @Test
    @DisplayName("Debería eliminar una dirección existente")
    void deberiaEliminarDireccion() {
        // 1. Arrange
        when(direccionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(direccionRepository).deleteById(1L);

        // 2. Act
        direccionService.eliminarDireccion(1L);

        // 3. Assert
        verify(direccionRepository, times(1)).existsById(1L);
        verify(direccionRepository, times(1)).deleteById(1L);
    }

    /**
     * Test: deberiaFallarAlEliminarInexistente
     *
     *   Arrange: Simular que el ID a eliminar no existe en BD.
     *   Act & Assert: Intentar eliminar y validar ResourceNotFoundException.
     *
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar dirección inexistente")
    void deberiaFallarAlEliminarInexistente() {
        // 1. Arrange
        when(direccionRepository.existsById(99L)).thenReturn(false);

        // 2. Act & 3. Assert
        assertThrows(ResourceNotFoundException.class, () -> direccionService.eliminarDireccion(99L));
        verify(direccionRepository, times(1)).existsById(99L);
        verify(direccionRepository, never()).deleteById(anyLong());
    }
}

