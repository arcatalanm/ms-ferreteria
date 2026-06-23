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

    @Test
    @DisplayName("Debería crear una nueva dirección")
    void deberiaCrearDireccion() {

        DireccionRequestDTO request = DireccionRequestDTO.builder()
                .calle("Av. Providencia")
                .numero(1234)
                .departamento("501")
                .comuna("Providencia")
                .ciudad("Santiago")
                .build();

        when(direccionRepository.save(any(Direccion.class))).thenReturn(direccion1);

        DireccionResponseDTO result = direccionService.crearDireccion(request);

        assertNotNull(result);
        assertEquals(1L, result.getIdDireccion());
        assertEquals("Av. Providencia 1234, Depto 501, Providencia, Santiago", result.getDireccionCompleta());
        verify(direccionRepository, times(1)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("Debería listar todas las direcciones")
    void deberiaListarTodas() {

        when(direccionRepository.findAll()).thenReturn(List.of(direccion1, direccion2));

        List<DireccionResponseDTO> result = direccionService.buscarTodas();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Av. Providencia", result.get(0).getCalle());
        assertEquals("Alameda", result.get(1).getCalle());
        verify(direccionRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener dirección por ID")
    void deberiaObtenerPorId() {

        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion1));

        DireccionResponseDTO result = direccionService.buscarPorId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getIdDireccion());
        assertEquals("Av. Providencia 1234, Depto 501, Providencia, Santiago", result.getDireccionCompleta());
        verify(direccionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar ID inexistente")
    void deberiaFallarAlObtenerPorIdInexistente() {

        when(direccionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> direccionService.buscarPorId(99L));
        verify(direccionRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Debería actualizar una dirección existente")
    void deberiaActualizarDireccion() {

        DireccionRequestDTO request = DireccionRequestDTO.builder()
                .calle("Av. Providencia Modificada")
                .numero(1234)
                .departamento("502")
                .comuna("Providencia")
                .ciudad("Santiago")
                .build();

        when(direccionRepository.findById(1L)).thenReturn(Optional.of(direccion1));
        when(direccionRepository.save(any(Direccion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        DireccionResponseDTO result = direccionService.actualizarDireccion(1L, request);

        assertNotNull(result);
        assertEquals(1L, result.getIdDireccion());
        assertEquals("Av. Providencia Modificada", result.getCalle());
        assertEquals("502", result.getDepartamento());
        verify(direccionRepository, times(1)).findById(1L);
        verify(direccionRepository, times(1)).save(any(Direccion.class));
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al actualizar dirección inexistente")
    void deberiaFallarAlActualizarInexistente() {

        DireccionRequestDTO request = DireccionRequestDTO.builder()
                .calle("Calle Inexistente")
                .numero(100)
                .comuna("Comuna")
                .ciudad("Ciudad")
                .build();

        when(direccionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> direccionService.actualizarDireccion(99L, request));
        verify(direccionRepository, times(1)).findById(99L);
        verify(direccionRepository, never()).save(any(Direccion.class));
    }

    @Test
    @DisplayName("Debería eliminar una dirección existente")
    void deberiaEliminarDireccion() {

        when(direccionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(direccionRepository).deleteById(1L);

        direccionService.eliminarDireccion(1L);

        verify(direccionRepository, times(1)).existsById(1L);
        verify(direccionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar dirección inexistente")
    void deberiaFallarAlEliminarInexistente() {

        when(direccionRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> direccionService.eliminarDireccion(99L));
        verify(direccionRepository, times(1)).existsById(99L);
        verify(direccionRepository, never()).deleteById(anyLong());
    }
}
