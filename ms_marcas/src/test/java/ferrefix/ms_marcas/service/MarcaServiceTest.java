package ferrefix.ms_marcas.service;

import ferrefix.ms_marcas.dto.MarcaRequestDTO;
import ferrefix.ms_marcas.dto.MarcaResponseDTO;
import ferrefix.ms_marcas.exception.ResourceNotFoundException;
import ferrefix.ms_marcas.mapper.MarcaMapper;
import ferrefix.ms_marcas.model.Marca;
import ferrefix.ms_marcas.repository.MarcaRepository;
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

// Habilita Mockito para crear simuladores automáticos en esta clase
/**
 * MarcaServiceTest
 * Pruebas unitarias para el servicio {@link MarcaService}.
 *
 * Flujo de Decisiones en Procesos:
 *
 *   [Servicio Marcas]
 *          │
 *          ├─── listarTodas() ──────────► marcaRepository.findAll()
 *          │
 *          ├─── obtenerPorId(id) ──────► marcaRepository.findById(id)
 *          │                                  └─── [No Existe] ──► (Lanza ResourceNotFoundException)
 *          │
 *          ├─── crear(dto) ─────────────► marcaRepository.save(entity)
 *          │
 *          ├─── actualizar(id, dto)
 *          │         └─── marcaRepository.findById(id)
 *          │                   ├─── [No Existe] ──► (Lanza ResourceNotFoundException)
 *          │                   └─── [Existe] ─────► marcaRepository.save(entity)
 *          │
 *          └─── eliminar(id)
 *                    └─── marcaRepository.existsById(id)
 *                              ├─── [No Existe] ──► (Lanza ResourceNotFoundException)
 *                              └─── [Existe] ─────► marcaRepository.deleteById(id)
 *
 */
@ExtendWith(MockitoExtension.class)
class MarcaServiceTest {

    // @Mock crea un "simulador" (clon de mentira) de la dependencia (BD o cliente externo)
    @Mock
    private MarcaRepository marcaRepository;

    // @Spy crea un simulador parcial (objeto real pero vigilado o modificado)
    @Spy
    private MarcaMapper marcaMapper = new MarcaMapper();

    // Inyecta automáticamente los simuladores anteriores dentro del servicio que vamos a probar
    @InjectMocks
    private MarcaService marcaService;

    private Marca marcaBosch;
    private Marca marcaStanley;

    // Se ejecuta ANTES de cada test para preparar o resetear los datos ficticios de prueba
    @BeforeEach
    void setUp() {
        // Arrange - Configuración de objetos mock
        marcaBosch = Marca.builder()
                .idMarca(1)
                .nombreMarca("Bosch")
                .build();

        marcaStanley = Marca.builder()
                .idMarca(2)
                .nombreMarca("Stanley")
                .build();
    }

    /**
     * Test: deberiaListarTodasLasMarcas
     *
     *   Arrange: Simular listado retornando Bosch y Stanley.
     *   Act: Llamar al método listarTodas().
     *   Assert: Comprobar el tamaño y el contenido del listado retornado.
     *
     */
    @Test
    @DisplayName("Debería listar todas las marcas")
    void deberiaListarTodasLasMarcas() {
        // 1. PREPARACIÓN (Arrange)
        when(marcaRepository.findAll()).thenReturn(List.of(marcaBosch, marcaStanley));

        // 2. ACCIÓN (Act)
        List<MarcaResponseDTO> resultado = marcaService.listarTodas();

        // 3. VERIFICACIÓN (Assert)
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Bosch", resultado.get(0).getNombreMarca());
        assertEquals("Stanley", resultado.get(1).getNombreMarca());
        verify(marcaRepository, times(1)).findAll();
    }

    /**
     * Test: deberiaObtenerMarcaPorId
     *
     *   Arrange: Simular retorno de la marca Bosch por su ID.
     *   Act: Llamar a obtenerPorId(1).
     *   Assert: Verificar correspondencia de ID y nombre.
     *
     */
    @Test
    @DisplayName("Debería obtener una marca por su ID")
    void deberiaObtenerMarcaPorId() {
        // 1. PREPARACIÓN (Arrange)
        when(marcaRepository.findById(1)).thenReturn(Optional.of(marcaBosch));

        // 2. ACCIÓN (Act)
        MarcaResponseDTO resultado = marcaService.obtenerPorId(1);

        // 3. VERIFICACIÓN (Assert)
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdMarca());
        assertEquals("Bosch", resultado.getNombreMarca());
        verify(marcaRepository, times(1)).findById(1);
    }

    /**
     * Test: deberiaFallarAlObtenerMarcaInexistente
     *
     *   Arrange: Simular que la marca no existe.
     *   Act & Assert: Intentar obtener y comprobar ResourceNotFoundException.
     *
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar marca inexistente")
    void deberiaFallarAlObtenerMarcaInexistente() {
        // 1. PREPARACIÓN (Arrange)
        when(marcaRepository.findById(99)).thenReturn(Optional.empty());

        // 2. ACCIÓN y VERIFICACIÓN (Act & Assert)
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                marcaService.obtenerPorId(99)
        );

        assertEquals("Marca no encontrada con ID: 99", ex.getMessage());
        verify(marcaRepository, times(1)).findById(99);
    }

    /**
     * Test: deberiaCrearMarca
     *
     *   Arrange: Crear DTO de solicitud de creación y configurar mock.
     *   Act: Llamar a crear().
     *   Assert: Verificar ID de marca recién creada y llamado a BD.
     *
     */
    @Test
    @DisplayName("Debería crear una nueva marca")
    void deberiaCrearMarca() {
        // 1. PREPARACIÓN (Arrange)
        MarcaRequestDTO request = MarcaRequestDTO.builder()
                .nombreMarca("Makita")
                .build();

        Marca guardada = Marca.builder()
                .idMarca(3)
                .nombreMarca("Makita")
                .build();

        when(marcaRepository.save(any(Marca.class))).thenReturn(guardada);

        // 2. ACCIÓN (Act)
        MarcaResponseDTO resultado = marcaService.crear(request);

        // 3. VERIFICACIÓN (Assert)
        assertNotNull(resultado);
        assertEquals(3, resultado.getIdMarca());
        assertEquals("Makita", resultado.getNombreMarca());
        verify(marcaRepository, times(1)).save(any(Marca.class));
    }

    /**
     * Test: deberiaActualizarMarca
     *
     *   Arrange: Simular búsqueda exitosa de marca y guardado del DTO modificado.
     *   Act: Llamar a actualizar().
     *   Assert: Verificar nombre actualizado y confirmaciones de mocks.
     *
     */
    @Test
    @DisplayName("Debería actualizar una marca existente")
    void deberiaActualizarMarca() {
        // 1. PREPARACIÓN (Arrange)
        MarcaRequestDTO request = MarcaRequestDTO.builder()
                .nombreMarca("Bosch Professional")
                .build();

        when(marcaRepository.findById(1)).thenReturn(Optional.of(marcaBosch));
        when(marcaRepository.save(any(Marca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. ACCIÓN (Act)
        MarcaResponseDTO resultado = marcaService.actualizar(1, request);

        // 3. VERIFICACIÓN (Assert)
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdMarca());
        assertEquals("Bosch Professional", resultado.getNombreMarca());
        verify(marcaRepository, times(1)).findById(1);
        verify(marcaRepository, times(1)).save(any(Marca.class));
    }

    /**
     * Test: deberiaFallarAlActualizarMarcaInexistente
     *
     *   Arrange: Simular marca no encontrada.
     *   Act & Assert: Intentar actualizar y comprobar ResourceNotFoundException.
     *
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al actualizar marca inexistente")
    void deberiaFallarAlActualizarMarcaInexistente() {
        // 1. PREPARACIÓN (Arrange)
        MarcaRequestDTO request = MarcaRequestDTO.builder()
                .nombreMarca("Bosch Professional")
                .build();

        when(marcaRepository.findById(99)).thenReturn(Optional.empty());

        // 2. ACCIÓN y VERIFICACIÓN (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () ->
                marcaService.actualizar(99, request)
        );

        verify(marcaRepository, times(1)).findById(99);
        verify(marcaRepository, never()).save(any(Marca.class));
    }

    /**
     * Test: deberiaEliminarMarca
     *
     *   Arrange: Simular que el ID de la marca existe.
     *   Act: Llamar a eliminar().
     *   Assert: Verificar deleteById y existsById en mock.
     *
     */
    @Test
    @DisplayName("Debería eliminar una marca existente")
    void deberiaEliminarMarca() {
        // 1. PREPARACIÓN (Arrange)
        when(marcaRepository.existsById(1)).thenReturn(true);
        doNothing().when(marcaRepository).deleteById(1);

        // 2. ACCIÓN (Act)
        marcaService.eliminar(1);

        // 3. VERIFICACIÓN (Assert)
        verify(marcaRepository, times(1)).existsById(1);
        verify(marcaRepository, times(1)).deleteById(1);
    }

    /**
     * Test: deberiaFallarAlEliminarMarcaInexistente
     *
     *   Arrange: Simular que el ID a eliminar no existe.
     *   Act & Assert: Intentar eliminar y validar ResourceNotFoundException.
     *
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar marca inexistente")
    void deberiaFallarAlEliminarMarcaInexistente() {
        // 1. PREPARACIÓN (Arrange)
        when(marcaRepository.existsById(99)).thenReturn(false);

        // 2. ACCIÓN y VERIFICACIÓN (Act & Assert)
        assertThrows(ResourceNotFoundException.class, () ->
                marcaService.eliminar(99)
        );

        verify(marcaRepository, times(1)).existsById(99);
        verify(marcaRepository, never()).deleteById(anyInt());
    }
}

