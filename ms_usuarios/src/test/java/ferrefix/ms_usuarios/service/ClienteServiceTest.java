package ferrefix.ms_usuarios.service;

import feign.FeignException;
import ferrefix.ms_usuarios.client.DireccionClient;
import ferrefix.ms_usuarios.dto.ClienteRequestDTO;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import ferrefix.ms_usuarios.dto.DireccionDTO;
import ferrefix.ms_usuarios.exception.BadRequestException;
import ferrefix.ms_usuarios.exception.ResourceNotFoundException;
import ferrefix.ms_usuarios.mapper.ClienteMapper;
import ferrefix.ms_usuarios.model.Cliente;
import ferrefix.ms_usuarios.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ClienteServiceTest
 *
 * Visual Flowchart of the Service Unit Testing Architecture:
 *
 *   [Test Case] ────────────────────────(Invokes Method)───────────────────────> [ClienteService]
 *                                                                                      |
 *                                           ┌──────────────────────┬───────────────────┤
 *                                           v (Spy)                v (Mock)            v (Mock)
 *                                     [ClienteMapper]      [ClienteRepository]   [DireccionClient]
 *                                                                                      |
 *                                                                             (Feign Microservice Client)
 *                                                                                      v
 *   [Assert Result] <─────────────────(Asserts / Exceptions)───────────────────────────┘
 *
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Spy
    private ClienteMapper clienteMapper = new ClienteMapper();

    @Mock
    private DireccionClient direccionClient;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente1;
    private Cliente cliente2;
    private DireccionDTO direccionDTO;

    // ==========================================
    // SETUP & INITIALIZATION
    // ==========================================
    @BeforeEach
    void setUp() {
        cliente1 = Cliente.builder()
                .runCliente(12345678)
                .dvCliente('5')
                .pnombreCliente("Juan")
                .snombreCliente("Carlos")
                .appaternoCliente("Pérez")
                .apmaternoCliente("González")
                .fechaNacimientoCliente(LocalDate.of(1990, 5, 10))
                .emailCliente("juan.perez@email.com")
                .contrasenaCliente("password123")
                .telefonoCliente("987654321")
                .idDireccion(10L)
                .fechaRegistroCliente(LocalDate.now())
                .build();

        cliente2 = Cliente.builder()
                .runCliente(87654321)
                .dvCliente('0')
                .pnombreCliente("Ana")
                .appaternoCliente("Silva")
                .apmaternoCliente("Rojas")
                .fechaNacimientoCliente(LocalDate.of(1995, 8, 20))
                .emailCliente("ana.silva@email.com")
                .contrasenaCliente("password456")
                .telefonoCliente("912345678")
                .idDireccion(20L)
                .fechaRegistroCliente(LocalDate.now())
                .build();

        direccionDTO = DireccionDTO.builder()
                .idDireccion(10L)
                .calle("Av. Providencia")
                .numero(1234)
                .comuna("Providencia")
                .ciudad("Santiago")
                .build();
    }

    /**
     * deberiaCrearCliente:
     * Verifies successful creation of a client with unique RUN and email, mapping address details via Feign client.
     */
    @Test
    @DisplayName("Debería crear un cliente exitosamente")
    void deberiaCrearCliente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("12.345.678-5")
                .pnombreCliente("Juan")
                .snombreCliente("Carlos")
                .appaternoCliente("Pérez")
                .apmaternoCliente("González")
                .fechaNacimientoCliente(LocalDate.of(1990, 5, 10))
                .emailCliente("juan.perez@email.com")
                .contrasenaCliente("password123")
                .telefonoCliente("987654321")
                .idDireccion(10L)
                .build();

        when(clienteRepository.existsById(12345678)).thenReturn(false);
        when(clienteRepository.findByEmailCliente("juan.perez@email.com")).thenReturn(null);
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente1);
        when(direccionClient.obtenerDireccionPorId(10L)).thenReturn(direccionDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        ClienteResponseDTO result = clienteService.crearCliente(request);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals("12345678-5", result.getRunClienteCompleto());
        assertEquals("Juan Carlos Pérez González", result.getNombreClienteCompleto());
        verify(clienteRepository, times(1)).existsById(12345678);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    /**
     * deberiaFallarAlCrearRutInvalido:
     * Verifies that creating a client with an invalid RUT format throws BadRequestException.
     */
    @Test
    @DisplayName("Debería lanzar BadRequestException al crear con RUT inválido")
    void deberiaFallarAlCrearRutInvalido() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("12.345-K")
                .build();

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () ->
                clienteService.crearCliente(request)
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /**
     * deberiaFallarAlCrearRunDuplicado:
     * Verifies BadRequestException when trying to register a duplicate client RUN.
     */
    @Test
    @DisplayName("Debería lanzar BadRequestException al crear con un RUN duplicado")
    void deberiaFallarAlCrearRunDuplicado() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("12.345.678-5")
                .build();

        when(clienteRepository.existsById(12345678)).thenReturn(true);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () ->
                clienteService.crearCliente(request)
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /**
     * deberiaFallarAlCrearEmailDuplicado:
     * Verifies BadRequestException when registering a client with a pre-existing email address.
     */
    @Test
    @DisplayName("Debería lanzar BadRequestException al crear con un Email duplicado")
    void deberiaFallarAlCrearEmailDuplicado() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("12.345.678-5")
                .emailCliente("ana.silva@email.com")
                .build();

        when(clienteRepository.existsById(12345678)).thenReturn(false);
        when(clienteRepository.findByEmailCliente("ana.silva@email.com")).thenReturn(cliente2);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () ->
                clienteService.crearCliente(request)
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /**
     * deberiaListarTodos:
     * Verifies finding all clients fetches details and combines with mapped address info from DireccionClient.
     */
    @Test
    @DisplayName("Debería listar todos los clientes")
    void deberiaListarTodos() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(clienteRepository.findAll()).thenReturn(List.of(cliente1, cliente2));
        when(direccionClient.obtenerDireccionPorId(10L)).thenReturn(direccionDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        List<ClienteResponseDTO> result = clienteService.buscarTodosClientes();

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(clienteRepository, times(1)).findAll();
    }

    /**
     * deberiaBuscarPorRun:
     * Verifies searching for an existing client by RUN successfully fetches details.
     */
    @Test
    @DisplayName("Debería buscar cliente por RUN")
    void deberiaBuscarPorRun() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(clienteRepository.findById(12345678)).thenReturn(Optional.of(cliente1));
        when(direccionClient.obtenerDireccionPorId(10L)).thenReturn(direccionDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        ClienteResponseDTO result = clienteService.buscarClientePorRun(12345678);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals("12345678-5", result.getRunClienteCompleto());
        verify(clienteRepository, times(1)).findById(12345678);
    }

    /**
     * deberiaFallarAlBuscarInexistente:
     * Verifies ResourceNotFoundException when looking up a client by an unregistered RUN.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar RUN inexistente")
    void deberiaFallarAlBuscarInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () ->
                clienteService.buscarClientePorRun(99)
        );

        verify(clienteRepository, times(1)).findById(99);
    }

    /**
     * deberiaActualizarCliente:
     * Verifies that updating an existing client works correctly and calls database save.
     */
    @Test
    @DisplayName("Debería actualizar cliente existente exitosamente")
    void deberiaActualizarCliente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("12.345.678-5")
                .pnombreCliente("Juan Modificado")
                .appaternoCliente("Pérez")
                .apmaternoCliente("González")
                .fechaNacimientoCliente(LocalDate.of(1990, 5, 10))
                .emailCliente("juan.perez@email.com")
                .contrasenaCliente("password123")
                .telefonoCliente("987654321")
                .idDireccion(10L)
                .build();

        when(clienteRepository.findById(12345678)).thenReturn(Optional.of(cliente1));
        when(clienteRepository.findByEmailCliente("juan.perez@email.com")).thenReturn(null);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));
        when(direccionClient.obtenerDireccionPorId(10L)).thenReturn(direccionDTO);

        // ==========================================
        // 2. ACT
        // ==========================================
        ClienteResponseDTO result = clienteService.actualizarCliente(12345678, request);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals("Juan Modificado Pérez González", result.getNombreClienteCompleto());
        verify(clienteRepository, times(1)).findById(12345678);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    /**
     * deberiaFallarAlActualizarMismatchedRun:
     * Verifies BadRequestException when updating a client where the URL RUN does not match the Request body RUN.
     */
    @Test
    @DisplayName("Debería lanzar BadRequestException al actualizar con RUN URL diferente al RUN Body")
    void deberiaFallarAlActualizarMismatchedRun() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("87.654.321-0") // RUN body: 87654321
                .build();

        when(clienteRepository.findById(12345678)).thenReturn(Optional.of(cliente1));

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () ->
                clienteService.actualizarCliente(12345678, request) // URL run: 12345678
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    /**
     * deberiaEliminarCliente:
     * Verifies deletion of an existing client record.
     */
    @Test
    @DisplayName("Debería eliminar cliente existente")
    void deberiaEliminarCliente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(clienteRepository.existsById(12345678)).thenReturn(true);
        doNothing().when(clienteRepository).deleteAllByRunCliente(12345678);

        // ==========================================
        // 2. ACT
        // ==========================================
        clienteService.eliminarClientePorRun(12345678);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        verify(clienteRepository, times(1)).existsById(12345678);
        verify(clienteRepository, times(1)).deleteAllByRunCliente(12345678);
    }

    /**
     * deberiaFallarAlEliminarInexistente:
     * Verifies ResourceNotFoundException when attempting to delete a client with a non-existent RUN.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar cliente inexistente")
    void deberiaFallarAlEliminarInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(clienteRepository.existsById(99)).thenReturn(false);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () ->
                clienteService.eliminarClientePorRun(99)
        );

        verify(clienteRepository, times(1)).existsById(99);
        verify(clienteRepository, never()).deleteAllByRunCliente(anyInt());
    }
}
