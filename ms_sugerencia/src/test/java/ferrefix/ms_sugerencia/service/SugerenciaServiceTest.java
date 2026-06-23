package ferrefix.ms_sugerencia.service;

import ferrefix.ms_sugerencia.dto.SugerenciaRequestDTO;
import ferrefix.ms_sugerencia.dto.SugerenciaResponseDTO;
import ferrefix.ms_sugerencia.exception.ResourceNotFoundException;
import ferrefix.ms_sugerencia.mapper.SugerenciaMapper;
import ferrefix.ms_sugerencia.model.Sugerencia;
import ferrefix.ms_sugerencia.repository.SugerenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SugerenciaServiceTest {

    @Mock
    private SugerenciaRepository sugerenciaRepository;

    @Spy
    private SugerenciaMapper sugerenciaMapper = new SugerenciaMapper();

    @InjectMocks
    private SugerenciaService sugerenciaService;

    private Sugerencia sugerencia1;
    private Sugerencia sugerencia2;

    @BeforeEach
    void setUp() {
        sugerencia1 = Sugerencia.builder()
                .idSugerencia(1L)
                .contenidoMensaje("Excelente atención al cliente.")
                .fechaIngreso(LocalDateTime.now().minusDays(1))
                .build();

        sugerencia2 = Sugerencia.builder()
                .idSugerencia(2L)
                .contenidoMensaje("Falta variedad en pinturas.")
                .fechaIngreso(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Debería listar todas las sugerencias por orden cronológico descendente")
    void deberiaListarTodas() {

        when(sugerenciaRepository.findAll(any(Sort.class))).thenReturn(List.of(sugerencia2, sugerencia1));

        List<SugerenciaResponseDTO> result = sugerenciaService.listarTodas();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Falta variedad en pinturas.", result.get(0).getContenidoMensaje());
        assertEquals("Excelente atención al cliente.", result.get(1).getContenidoMensaje());
        verify(sugerenciaRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("Debería crear una sugerencia exitosamente")
    void deberiaCrearSugerencia() {

        SugerenciaRequestDTO request = SugerenciaRequestDTO.builder()
                .contenidoMensaje("Excelente atención al cliente.")
                .build();

        when(sugerenciaRepository.save(any(Sugerencia.class))).thenReturn(sugerencia1);

        SugerenciaResponseDTO result = sugerenciaService.crear(request);

        assertNotNull(result);
        assertEquals(1L, result.getIdSugerencia());
        assertEquals("Excelente atención al cliente.", result.getContenidoMensaje());
        verify(sugerenciaRepository, times(1)).save(any(Sugerencia.class));
    }

    @Test
    @DisplayName("Debería eliminar una sugerencia existente")
    void deberiaEliminarSugerencia() {

        when(sugerenciaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(sugerenciaRepository).deleteById(1L);

        sugerenciaService.eliminar(1L);

        verify(sugerenciaRepository, times(1)).existsById(1L);
        verify(sugerenciaRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar una sugerencia inexistente")
    void deberiaFallarAlEliminarInexistente() {

        when(sugerenciaRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                sugerenciaService.eliminar(99L)
        );

        verify(sugerenciaRepository, times(1)).existsById(99L);
        verify(sugerenciaRepository, never()).deleteById(anyLong());
    }
}
