package ferrefix.ms_usuarios.service;

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

    @Test
    @DisplayName("Debería crear un cliente exitosamente")
    void deberiaCrearCliente() {

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

        ClienteResponseDTO result = clienteService.crearCliente(request);

        assertNotNull(result);
        assertEquals("12345678-5", result.getRunClienteCompleto());
        assertEquals("Juan Carlos Pérez González", result.getNombreClienteCompleto());
        verify(clienteRepository, times(1)).existsById(12345678);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al crear con RUT inválido")
    void deberiaFallarAlCrearRutInvalido() {

        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("12.345-K")
                .build();

        assertThrows(BadRequestException.class, () ->
                clienteService.crearCliente(request)
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al crear con un RUN duplicado")
    void deberiaFallarAlCrearRunDuplicado() {

        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("12.345.678-5")
                .build();

        when(clienteRepository.existsById(12345678)).thenReturn(true);

        assertThrows(BadRequestException.class, () ->
                clienteService.crearCliente(request)
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al crear con un Email duplicado")
    void deberiaFallarAlCrearEmailDuplicado() {

        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("12.345.678-5")
                .emailCliente("ana.silva@email.com")
                .build();

        when(clienteRepository.existsById(12345678)).thenReturn(false);
        when(clienteRepository.findByEmailCliente("ana.silva@email.com")).thenReturn(cliente2);

        assertThrows(BadRequestException.class, () ->
                clienteService.crearCliente(request)
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debería listar todos los clientes")
    void deberiaListarTodos() {

        when(clienteRepository.findAll()).thenReturn(List.of(cliente1, cliente2));
        when(direccionClient.obtenerDireccionPorId(10L)).thenReturn(direccionDTO);

        List<ClienteResponseDTO> result = clienteService.buscarTodosClientes();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería buscar cliente por RUN")
    void deberiaBuscarPorRun() {

        when(clienteRepository.findById(12345678)).thenReturn(Optional.of(cliente1));
        when(direccionClient.obtenerDireccionPorId(10L)).thenReturn(direccionDTO);

        ClienteResponseDTO result = clienteService.buscarClientePorRun(12345678);

        assertNotNull(result);
        assertEquals("12345678-5", result.getRunClienteCompleto());
        verify(clienteRepository, times(1)).findById(12345678);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar RUN inexistente")
    void deberiaFallarAlBuscarInexistente() {

        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                clienteService.buscarClientePorRun(99)
        );

        verify(clienteRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debería actualizar cliente existente exitosamente")
    void deberiaActualizarCliente() {

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

        ClienteResponseDTO result = clienteService.actualizarCliente(12345678, request);

        assertNotNull(result);
        assertEquals("Juan Modificado Pérez González", result.getNombreClienteCompleto());
        verify(clienteRepository, times(1)).findById(12345678);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al actualizar con RUN URL diferente al RUN Body")
    void deberiaFallarAlActualizarMismatchedRun() {

        ClienteRequestDTO request = ClienteRequestDTO.builder()
                .runCliente("87.654.321-0")
                .build();

        when(clienteRepository.findById(12345678)).thenReturn(Optional.of(cliente1));

        assertThrows(BadRequestException.class, () ->
                clienteService.actualizarCliente(12345678, request)
        );

        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debería eliminar cliente existente")
    void deberiaEliminarCliente() {

        when(clienteRepository.existsById(12345678)).thenReturn(true);
        doNothing().when(clienteRepository).deleteAllByRunCliente(12345678);

        clienteService.eliminarClientePorRun(12345678);

        verify(clienteRepository, times(1)).existsById(12345678);
        verify(clienteRepository, times(1)).deleteAllByRunCliente(12345678);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar cliente inexistente")
    void deberiaFallarAlEliminarInexistente() {

        when(clienteRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                clienteService.eliminarClientePorRun(99)
        );

        verify(clienteRepository, times(1)).existsById(99);
        verify(clienteRepository, never()).deleteAllByRunCliente(anyInt());
    }
}
