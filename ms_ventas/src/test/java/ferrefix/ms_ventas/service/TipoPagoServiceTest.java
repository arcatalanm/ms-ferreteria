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

    @Test
    @DisplayName("Debería listar todos los tipos de pago")
    void deberiaListarTodos() {

        when(tipoPagoRepository.findAll()).thenReturn(List.of(tipoPago));
        when(tipoPagoMapper.toResponseDTO(tipoPago)).thenReturn(responseDTO);

        List<TipoPagoResponseDTO> result = tipoPagoService.obtenerTodos();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Efectivo", result.get(0).getNombreTipoPago());
        verify(tipoPagoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener tipo de pago por ID")
    void deberiaObtenerPorId() {

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        when(tipoPagoMapper.toResponseDTO(tipoPago)).thenReturn(responseDTO);

        TipoPagoResponseDTO result = tipoPagoService.obtenerPorId(1);

        assertNotNull(result);
        assertEquals("Efectivo", result.getNombreTipoPago());
        verify(tipoPagoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debería lanzar excepción al obtener ID inexistente")
    void deberiaLanzarExcepcionAlObtenerInexistente() {

        when(tipoPagoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tipoPagoService.obtenerPorId(99));
        verify(tipoPagoRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debería crear un tipo de pago exitosamente")
    void deberiaCrearExitosamente() {

        when(tipoPagoRepository.existsByNombreTipoPago("Efectivo")).thenReturn(false);
        when(tipoPagoMapper.toEntity(requestDTO)).thenReturn(tipoPago);
        when(tipoPagoRepository.save(tipoPago)).thenReturn(tipoPago);
        when(tipoPagoMapper.toResponseDTO(tipoPago)).thenReturn(responseDTO);

        TipoPagoResponseDTO result = tipoPagoService.crear(requestDTO);

        assertNotNull(result);
        assertEquals("Efectivo", result.getNombreTipoPago());
        verify(tipoPagoRepository, times(1)).existsByNombreTipoPago("Efectivo");
        verify(tipoPagoRepository, times(1)).save(tipoPago);
    }

    @Test
    @DisplayName("Debería lanzar excepción si el nombre de tipo de pago ya existe al crear")
    void deberiaLanzarExcepcionAlCrearNombreExistente() {

        when(tipoPagoRepository.existsByNombreTipoPago("Efectivo")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> tipoPagoService.crear(requestDTO));
        verify(tipoPagoRepository, times(1)).existsByNombreTipoPago("Efectivo");
        verify(tipoPagoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería actualizar un tipo de pago exitosamente")
    void deberiaActualizarExitosamente() {

        TipoPago existente = TipoPago.builder().idTipoPago(1).nombreTipoPago("Efectivo").build();
        TipoPagoRequestDTO requestActualizar = TipoPagoRequestDTO.builder().nombreTipoPago("Tarjeta").build();
        TipoPago actualizado = TipoPago.builder().idTipoPago(1).nombreTipoPago("Tarjeta").build();
        TipoPagoResponseDTO responseActualizado = TipoPagoResponseDTO.builder().idTipoPago(1).nombreTipoPago("Tarjeta").build();

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(existente));
        when(tipoPagoRepository.findByNombreTipoPago("Tarjeta")).thenReturn(Optional.empty());
        when(tipoPagoRepository.save(existente)).thenReturn(actualizado);
        when(tipoPagoMapper.toResponseDTO(actualizado)).thenReturn(responseActualizado);

        TipoPagoResponseDTO result = tipoPagoService.actualizar(1, requestActualizar);

        assertNotNull(result);
        assertEquals("Tarjeta", result.getNombreTipoPago());
        verify(tipoPagoRepository, times(1)).findById(1);
        verify(tipoPagoRepository, times(1)).findByNombreTipoPago("Tarjeta");
        verify(tipoPagoRepository, times(1)).save(existente);
    }

    @Test
    @DisplayName("Debería lanzar excepción si el tipo de pago a actualizar no existe")
    void deberiaLanzarExcepcionAlActualizarInexistente() {

        when(tipoPagoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tipoPagoService.actualizar(99, requestDTO));
        verify(tipoPagoRepository, times(1)).findById(99);
        verify(tipoPagoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería lanzar excepción si el nombre de tipo de pago ya existe en otro registro al actualizar")
    void deberiaLanzarExcepcionAlActualizarNombreExistenteEnOtro() {

        TipoPago existente = TipoPago.builder().idTipoPago(1).nombreTipoPago("Efectivo").build();
        TipoPago otro = TipoPago.builder().idTipoPago(2).nombreTipoPago("Tarjeta").build();
        TipoPagoRequestDTO requestActualizar = TipoPagoRequestDTO.builder().nombreTipoPago("Tarjeta").build();

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(existente));
        when(tipoPagoRepository.findByNombreTipoPago("Tarjeta")).thenReturn(Optional.of(otro));

        assertThrows(BadRequestException.class, () -> tipoPagoService.actualizar(1, requestActualizar));
        verify(tipoPagoRepository, times(1)).findById(1);
        verify(tipoPagoRepository, times(1)).findByNombreTipoPago("Tarjeta");
        verify(tipoPagoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debería eliminar un tipo de pago exitosamente")
    void deberiaEliminarExitosamente() {

        when(tipoPagoRepository.findById(1)).thenReturn(Optional.of(tipoPago));
        doNothing().when(tipoPagoRepository).delete(tipoPago);

        tipoPagoService.eliminar(1);

        verify(tipoPagoRepository, times(1)).findById(1);
        verify(tipoPagoRepository, times(1)).delete(tipoPago);
    }

    @Test
    @DisplayName("Debería lanzar excepción al eliminar un tipo de pago inexistente")
    void deberiaLanzarExcepcionAlEliminarInexistente() {

        when(tipoPagoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tipoPagoService.eliminar(99));
        verify(tipoPagoRepository, times(1)).findById(99);
        verify(tipoPagoRepository, never()).delete(any());
    }
}
