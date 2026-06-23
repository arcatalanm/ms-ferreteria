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

    void setUp() {

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

    @Test
    @DisplayName("Debería listar todas las máquinas de arriendo")
    void deberiaListarTodasLasMaquinas() {

        when(maquinaRepository.findAll()).thenReturn(List.of(maquinaDisponible, maquinaArrendada));

        List<MaquinaResponseDTO> resultado = maquinaArriendoService.listarTodas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("MAQ-001", resultado.get(0).getCodigoInterno());
        assertEquals("MAQ-002", resultado.get(1).getCodigoInterno());
        verify(maquinaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería registrar una nueva máquina de arriendo")
    void deberiaRegistrarMaquina() {

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

        MaquinaResponseDTO resultado = maquinaArriendoService.registrar(request);

        assertNotNull(resultado);
        assertEquals(3, resultado.getIdEquipo());
        assertEquals("DISPONIBLE", resultado.getEstado());
        verify(maquinaRepository, times(1)).save(any(MaquinaArriendo.class));
    }

    @Test
    @DisplayName("Debería procesar arriendo de máquina disponible y cliente válido")
    void deberiaProcesarArriendoExitosamente() {

        ProcesarArriendoRequestDTO request = ProcesarArriendoRequestDTO.builder()
                .runCliente(12345678)
                .diasArriendo(3)
                .build();

        doNothing().when(usuarioClient).validarCliente(12345678);
        when(maquinaRepository.findById(1)).thenReturn(Optional.of(maquinaDisponible));
        when(maquinaRepository.save(any(MaquinaArriendo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MaquinaResponseDTO resultado = maquinaArriendoService.procesarArriendo(1, request);

        assertNotNull(resultado);
        assertEquals("ARRENDADO", resultado.getEstado());
        assertEquals(12345678, resultado.getRunCliente());
        assertEquals(LocalDate.now().plusDays(3), resultado.getFechaDevolucionPactada());
        verify(usuarioClient, times(1)).validarCliente(12345678);
        verify(maquinaRepository, times(1)).findById(1);
        verify(maquinaRepository, times(1)).save(any(MaquinaArriendo.class));
    }

    @Test
    @DisplayName("Debería fallar el arriendo si el cliente no puede ser validado (FeignException)")
    void deberiaFallarArriendoClienteNoValido() {

        ProcesarArriendoRequestDTO request = ProcesarArriendoRequestDTO.builder()
                .runCliente(99999999)
                .diasArriendo(3)
                .build();

        FeignException feignException = mock(FeignException.class);
        when(feignException.getMessage()).thenReturn("Not Found");
        when(feignException.status()).thenReturn(404);

        doThrow(feignException).when(usuarioClient).validarCliente(99999999);

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                maquinaArriendoService.procesarArriendo(1, request)
        );

        assertTrue(ex.getMessage().contains("No se pudo validar el cliente"));
        verify(usuarioClient, times(1)).validarCliente(99999999);
        verify(maquinaRepository, never()).findById(anyInt());
        verify(maquinaRepository, never()).save(any(MaquinaArriendo.class));
    }

    @Test
    @DisplayName("Debería fallar el arriendo si la máquina no existe")
    void deberiaFallarArriendoMaquinaNoExiste() {

        ProcesarArriendoRequestDTO request = ProcesarArriendoRequestDTO.builder()
                .runCliente(12345678)
                .diasArriendo(3)
                .build();

        doNothing().when(usuarioClient).validarCliente(12345678);
        when(maquinaRepository.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                maquinaArriendoService.procesarArriendo(99, request)
        );

        assertEquals("Máquina no encontrada con ID: 99", ex.getMessage());
        verify(maquinaRepository, times(1)).findById(99);
        verify(maquinaRepository, never()).save(any(MaquinaArriendo.class));
    }

    @Test
    @DisplayName("Debería fallar el arriendo si la máquina no está disponible")
    void deberiaFallarArriendoMaquinaNoDisponible() {

        ProcesarArriendoRequestDTO request = ProcesarArriendoRequestDTO.builder()
                .runCliente(12345678)
                .diasArriendo(3)
                .build();

        doNothing().when(usuarioClient).validarCliente(12345678);
        when(maquinaRepository.findById(2)).thenReturn(Optional.of(maquinaArrendada));

        BadRequestException ex = assertThrows(BadRequestException.class, () ->
                maquinaArriendoService.procesarArriendo(2, request)
        );

        assertTrue(ex.getMessage().contains("La máquina no está disponible para arriendo"));
        verify(maquinaRepository, times(1)).findById(2);
        verify(maquinaRepository, never()).save(any(MaquinaArriendo.class));
    }

    @Test
    @DisplayName("Debería procesar devolución de máquina arrendada exitosamente")
    void deberiaProcesarDevolucionExitosamente() {

        when(maquinaRepository.findById(2)).thenReturn(Optional.of(maquinaArrendada));
        when(maquinaRepository.save(any(MaquinaArriendo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MaquinaResponseDTO resultado = maquinaArriendoService.procesarDevolucion(2);

        assertNotNull(resultado);
        assertEquals("DISPONIBLE", resultado.getEstado());
        assertNull(resultado.getRunCliente());
        assertNull(resultado.getFechaDevolucionPactada());
        verify(maquinaRepository, times(1)).findById(2);
        verify(maquinaRepository, times(1)).save(any(MaquinaArriendo.class));
    }

    @Test
    @DisplayName("Debería procesar devolución con retraso exitosamente")
    void deberiaProcesarDevolucionConRetrasoExitosamente() {

        maquinaArrendada.setFechaDevolucionPactada(LocalDate.now().minusDays(2));

        when(maquinaRepository.findById(2)).thenReturn(Optional.of(maquinaArrendada));
        when(maquinaRepository.save(any(MaquinaArriendo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MaquinaResponseDTO resultado = maquinaArriendoService.procesarDevolucion(2);

        assertNotNull(resultado);
        assertEquals("DISPONIBLE", resultado.getEstado());
        verify(maquinaRepository, times(1)).findById(2);
        verify(maquinaRepository, times(1)).save(any(MaquinaArriendo.class));
    }

    @Test
    @DisplayName("Debería fallar la devolución si la máquina no existe")
    void deberiaFallarDevolucionMaquinaNoExiste() {

        when(maquinaRepository.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                maquinaArriendoService.procesarDevolucion(99)
        );

        assertEquals("Máquina no encontrada con ID: 99", ex.getMessage());
        verify(maquinaRepository, times(1)).findById(99);
    }
}
