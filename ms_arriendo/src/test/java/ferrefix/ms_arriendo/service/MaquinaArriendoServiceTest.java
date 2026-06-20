package ferrefix.ms_arriendo.service;

import feign.FeignException;
import ferrefix.ms_arriendo.client.UsuarioClient;
import ferrefix.ms_arriendo.dto.MaquinaRequestDTO;
import ferrefix.ms_arriendo.dto.MaquinaResponseDTO;
import ferrefix.ms_arriendo.dto.ProcesarArriendoRequestDTO;
import ferrefix.ms_arriendo.exception.BadRequestException;
import ferrefix.ms_arriendo.exception.ResourceNotFoundException;
import ferrefix.ms_arriendo.model.MaquinaArriendo;
import ferrefix.ms_arriendo.repository.MaquinaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * MaquinaArriendoServiceTest
 * Pruebas unitarias para el servicio {@link MaquinaArriendoService}.
 *
 * Flujo de Operaciones y Validación:
 *
 *   [Servicio Arriendo]
 *          │
 *          ├─── listarTodas() ──────► maquinaRepository.findAll()
 *          │
 *          ├─── registrar() ────────► maquinaRepository.save(maquina)
 *          │
 *          ├─── procesarArriendo(id, request)
 *          │         │
 *          │         ├─── usuarioClient.validarCliente(run)
 *          │         │         └─── [Lanza FeignException] ──► (Lanza BadRequestException)
 *          │         │
 *          │         └─── maquinaRepository.findById(id)
 *          │                   ├─── [No Existe] ──────────────► (Lanza ResourceNotFoundException)
 *          │                   └─── [Existe]
 *          │                            └─── [NO DISPONIBLE] ──► (Lanza BadRequestException)
 *          │                            └─── [DISPONIBLE] ────► guardar() -> Estado: ARRENDADO
 *          │
 *          └─── procesarDevolucion(id)
 *                    └─── maquinaRepository.findById(id)
 *                              ├─── [No Existe] ──────────────► (Lanza ResourceNotFoundException)
 *                              └─── [Existe] ────────────────► guardar() -> Estado: DISPONIBLE
 *
 */
@ExtendWith(MockitoExtension.class)
class MaquinaArriendoServiceTest {

    @Mock
    private MaquinaRepository maquinaRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private MaquinaArriendoService maquinaArriendoService;

    private MaquinaArriendo maquinaDisponible;
    private MaquinaArriendo maquinaArrendada;

    @BeforeEach
    // Ejecutar esta prueba antes que cualquiera
    void setUp() {
        // Arrange - Construcción con Patron BUILDER de Lombok
        maquinaDisponible = MaquinaArriendo.builder()
                .idEquipo(1)
                .codigoInterno("MAQ-001")
                .nombreMaquina("Taladro Percutor")
                .precioPorDia(15000)
                .estado("DISPONIBLE")
                .build();

        maquinaArrendada = MaquinaArriendo.builder()
                .idEquipo(2)
                .codigoInterno("MAQ-002")
                .nombreMaquina("Esmeril Angular")
                .precioPorDia(12000)
                .estado("ARRENDADO")
                .runCliente(12345678)
                .fechaDevolucionPactada(LocalDate.now().plusDays(5))
                .build();
    }

    /**
     * Test: deberiaListarTodasLasMaquinas
     *
     *   Arrange: Simular que el repositorio retorna las dos máquinas inicializadas.
     *   Act: Llamar a listarTodas().
     *   Assert: Verificar que el listado no sea nulo, contenga 2 elementos y mantenga los códigos internos correspondientes.
     *
     */
    @Test
    @DisplayName("Debería listar todas las máquinas de arriendo")
    void deberiaListarTodasLasMaquinas() {
        // 1. Arrange
        when(maquinaRepository.findAll()).thenReturn(List.of(maquinaDisponible, maquinaArrendada));

        // 2. Act
        List<MaquinaResponseDTO> resultado = maquinaArriendoService.listarTodas();

        // 3. Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("MAQ-001", resultado.get(0).getCodigoInterno());
        assertEquals("MAQ-002", resultado.get(1).getCodigoInterno());
        verify(maquinaRepository, times(1)).findAll();
    }

    /**
     * Test: deberiaRegistrarMaquina
     *
     *   Arrange: Crear petición para registrar "Sierra Circular" y simular guardado.
     *   Act: Llamar a registrar().
     *   Assert: Verificar ID autogenerado, estado inicial "DISPONIBLE" y llamado a BD.
     *
     */
    @Test
    @DisplayName("Debería registrar una nueva máquina de arriendo")
    void deberiaRegistrarMaquina() {
        // 1. Arrange
        MaquinaRequestDTO request = MaquinaRequestDTO.builder()
                .codigoInterno("MAQ-003")
                .nombreMaquina("Sierra Circular")
                .precioPorDia(18000)
                .build();

        MaquinaArriendo guardada = MaquinaArriendo.builder()
                .idEquipo(3)
                .codigoInterno("MAQ-003")
                .nombreMaquina("Sierra Circular")
                .precioPorDia(18000)
                .estado("DISPONIBLE")
                .build();

        when(maquinaRepository.save(any(MaquinaArriendo.class))).thenReturn(guardada);

        // 2. Act
        MaquinaResponseDTO resultado = maquinaArriendoService.registrar(request);

        // 3. Assert
        assertNotNull(resultado);
        assertEquals(3, resultado.getIdEquipo());
        assertEquals("DISPONIBLE", resultado.getEstado());
        verify(maquinaRepository, times(1)).save(any(MaquinaArriendo.class));
    }

    /**
     * Test: deberiaProcesarArriendoExitosamente
     *
     *   Arrange: Definir cliente válido, simular validación de usuario sin errores,
     *            máquina encontrada en estado disponible, y guardar máquina modificada.
     *   Act: Llamar a procesarArriendo().
     *   Assert: Verificar estado final "ARRENDADO", cliente asignado y fecha de devolución pactada calculada.
     *
     */
    @Test
    @DisplayName("Debería procesar arriendo de máquina disponible y cliente válido")
    void deberiaProcesarArriendoExitosamente() {
        // 1. Arrange
        ProcesarArriendoRequestDTO request = ProcesarArriendoRequestDTO.builder()
                .runCliente(12345678)
                .diasArriendo(3)
                .build();

        doNothing().when(usuarioClient).validarCliente(12345678);
        when(maquinaRepository.findById(1)).thenReturn(Optional.of(maquinaDisponible));
        when(maquinaRepository.save(any(MaquinaArriendo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act
        MaquinaResponseDTO resultado = maquinaArriendoService.procesarArriendo(1, request);

        // 3. Assert
        assertNotNull(resultado);
        assertEquals("ARRENDADO", resultado.getEstado());
        assertEquals(12345678, resultado.getRunCliente());
        assertEquals(LocalDate.now().plusDays(3), resultado.getFechaDevolucionPactada());
        verify(usuarioClient, times(1)).validarCliente(12345678);
        verify(maquinaRepository, times(1)).findById(1);
        verify(maquinaRepository, times(1)).save(any(MaquinaArriendo.class));
    }

    /**
     * Test: deberiaFallarArriendoClienteNoValido
     *
     *   Arrange: Cliente inválido que causa FeignException al validarse.
     *   Act & Assert: Intentar procesar arriendo y validar BadRequestException esperada con el mensaje correcto.
     *
     */
    @Test
    @DisplayName("Debería fallar el arriendo si el cliente no puede ser validado (FeignException)")
    void deberiaFallarArriendoClienteNoValido() {
        // 1. Arrange
        ProcesarArriendoRequestDTO request = ProcesarArriendoRequestDTO.builder()
                .runCliente(99999999)
                .diasArriendo(3)
                .build();

        FeignException feignException = mock(FeignException.class);
        when(feignException.getMessage()).thenReturn("Not Found");
        when(feignException.status()).thenReturn(404);

        doThrow(feignException).when(usuarioClient).validarCliente(99999999);

        // 2. Act & 3. Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                maquinaArriendoService.procesarArriendo(1, request)
        );

        assertTrue(ex.getMessage().contains("No se pudo validar el cliente"));
        verify(usuarioClient, times(1)).validarCliente(99999999);
        verify(maquinaRepository, never()).findById(anyInt());
        verify(maquinaRepository, never()).save(any(MaquinaArriendo.class));
    }

    /**
     * Test: deberiaFallarArriendoMaquinaNoExiste
     *
     *   Arrange: Simular cliente válido pero máquina no encontrada (ID 99) en base de datos.
     *   Act & Assert: Comprobar que se lanza ResourceNotFoundException.
     *
     */
    @Test
    @DisplayName("Debería fallar el arriendo si la máquina no existe")
    void deberiaFallarArriendoMaquinaNoExiste() {
        // 1. Arrange
        ProcesarArriendoRequestDTO request = ProcesarArriendoRequestDTO.builder()
                .runCliente(12345678)
                .diasArriendo(3)
                .build();

        doNothing().when(usuarioClient).validarCliente(12345678);
        when(maquinaRepository.findById(99)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                maquinaArriendoService.procesarArriendo(99, request)
        );

        assertEquals("Máquina no encontrada con ID: 99", ex.getMessage());
        verify(maquinaRepository, times(1)).findById(99);
        verify(maquinaRepository, never()).save(any(MaquinaArriendo.class));
    }

    /**
     * Test: deberiaFallarArriendoMaquinaNoDisponible
     *
     *   Arrange: Simular máquina encontrada pero ya en estado "ARRENDADO".
     *   Act & Assert: Validar lanzamiento de BadRequestException.
     *
     */
    @Test
    @DisplayName("Debería fallar el arriendo si la máquina no está disponible")
    void deberiaFallarArriendoMaquinaNoDisponible() {
        // 1. Arrange
        ProcesarArriendoRequestDTO request = ProcesarArriendoRequestDTO.builder()
                .runCliente(12345678)
                .diasArriendo(3)
                .build();

        doNothing().when(usuarioClient).validarCliente(12345678);
        when(maquinaRepository.findById(2)).thenReturn(Optional.of(maquinaArrendada));

        // 2. Act & 3. Assert
        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                maquinaArriendoService.procesarArriendo(2, request)
        );

        assertTrue(ex.getMessage().contains("La máquina no está disponible para arriendo"));
        verify(maquinaRepository, times(1)).findById(2);
        verify(maquinaRepository, never()).save(any(MaquinaArriendo.class));
    }

    /**
     * Test: deberiaProcesarDevolucionExitosamente
     *
     *   Arrange: Simular máquina arrendada encontrada en BD y guardado de devolución.
     *   Act: Llamar a procesarDevolucion().
     *   Assert: Verificar estado "DISPONIBLE", datos del arriendo removidos (nulos) y guardados en repositorio.
     *
     */
    @Test
    @DisplayName("Debería procesar devolución de máquina arrendada exitosamente")
    void deberiaProcesarDevolucionExitosamente() {
        // 1. Arrange
        when(maquinaRepository.findById(2)).thenReturn(Optional.of(maquinaArrendada));
        when(maquinaRepository.save(any(MaquinaArriendo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act
        MaquinaResponseDTO resultado = maquinaArriendoService.procesarDevolucion(2);

        // 3. Assert
        assertNotNull(resultado);
        assertEquals("DISPONIBLE", resultado.getEstado());
        assertNull(resultado.getRunCliente());
        assertNull(resultado.getFechaDevolucionPactada());
        verify(maquinaRepository, times(1)).findById(2);
        verify(maquinaRepository, times(1)).save(any(MaquinaArriendo.class));
    }

    /**
     * Test: deberiaProcesarDevolucionConRetrasoExitosamente
     *
     *   Arrange: Simular retraso estableciendo la fecha pactada en el pasado.
     *   Act: Ejecutar devolución.
     *   Assert: Validar que cambie de igual forma a "DISPONIBLE" y limpie datos del cliente.
     *
     */
    @Test
    @DisplayName("Debería procesar devolución con retraso exitosamente")
    void deberiaProcesarDevolucionConRetrasoExitosamente() {
        // 1. Arrange
        // Establecer fecha de devolución en el pasado para simular retraso
        maquinaArrendada.setFechaDevolucionPactada(LocalDate.now().minusDays(2));

        when(maquinaRepository.findById(2)).thenReturn(Optional.of(maquinaArrendada));
        when(maquinaRepository.save(any(MaquinaArriendo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. Act
        MaquinaResponseDTO resultado = maquinaArriendoService.procesarDevolucion(2);

        // 3. Assert
        assertNotNull(resultado);
        assertEquals("DISPONIBLE", resultado.getEstado());
        verify(maquinaRepository, times(1)).findById(2);
        verify(maquinaRepository, times(1)).save(any(MaquinaArriendo.class));
    }

    /**
     * Test: deberiaFallarDevolucionMaquinaNoExiste
     *
     *   Arrange: Simular máquina no encontrada en BD.
     *   Act & Assert: Intentar devolución y verificar ResourceNotFoundException.
     *
     */
    @Test
    @DisplayName("Debería fallar la devolución si la máquina no existe")
    void deberiaFallarDevolucionMaquinaNoExiste() {
        // 1. Arrange
        when(maquinaRepository.findById(99)).thenReturn(Optional.empty());

        // 2. Act & 3. Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                maquinaArriendoService.procesarDevolucion(99)
        );

        assertEquals("Máquina no encontrada con ID: 99", ex.getMessage());
        verify(maquinaRepository, times(1)).findById(99);
    }
}

