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

@ExtendWith(MockitoExtension.class)
class MarcaServiceTest {

    @Mock
    private MarcaRepository marcaRepository;

    @Spy
    private MarcaMapper marcaMapper = new MarcaMapper();

    @InjectMocks
    private MarcaService marcaService;

    private Marca marcaBosch;
    private Marca marcaStanley;

    @BeforeEach
    void setUp() {

        marcaBosch = Marca.builder()
                .idMarca(1)
                .nombreMarca("Bosch")
                .build();

        marcaStanley = Marca.builder()
                .idMarca(2)
                .nombreMarca("Stanley")
                .build();
    }

    @Test
    @DisplayName("Debería listar todas las marcas")
    void deberiaListarTodasLasMarcas() {

        when(marcaRepository.findAll()).thenReturn(List.of(marcaBosch, marcaStanley));

        List<MarcaResponseDTO> resultado = marcaService.listarTodas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Bosch", resultado.get(0).getNombreMarca());
        assertEquals("Stanley", resultado.get(1).getNombreMarca());
        verify(marcaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener una marca por su ID")
    void deberiaObtenerMarcaPorId() {

        when(marcaRepository.findById(1)).thenReturn(Optional.of(marcaBosch));

        MarcaResponseDTO resultado = marcaService.obtenerPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdMarca());
        assertEquals("Bosch", resultado.getNombreMarca());
        verify(marcaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar marca inexistente")
    void deberiaFallarAlObtenerMarcaInexistente() {

        when(marcaRepository.findById(99)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                marcaService.obtenerPorId(99)
        );

        assertEquals("Marca no encontrada con ID: 99", ex.getMessage());
        verify(marcaRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debería crear una nueva marca")
    void deberiaCrearMarca() {

        MarcaRequestDTO request = MarcaRequestDTO.builder()
                .nombreMarca("Makita")
                .build();

        Marca guardada = Marca.builder()
                .idMarca(3)
                .nombreMarca("Makita")
                .build();

        when(marcaRepository.save(any(Marca.class))).thenReturn(guardada);

        MarcaResponseDTO resultado = marcaService.crear(request);

        assertNotNull(resultado);
        assertEquals(3, resultado.getIdMarca());
        assertEquals("Makita", resultado.getNombreMarca());
        verify(marcaRepository, times(1)).save(any(Marca.class));
    }

    @Test
    @DisplayName("Debería actualizar una marca existente")
    void deberiaActualizarMarca() {

        MarcaRequestDTO request = MarcaRequestDTO.builder()
                .nombreMarca("Bosch Professional")
                .build();

        when(marcaRepository.findById(1)).thenReturn(Optional.of(marcaBosch));
        when(marcaRepository.save(any(Marca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MarcaResponseDTO resultado = marcaService.actualizar(1, request);

        assertNotNull(resultado);
        assertEquals(1, resultado.getIdMarca());
        assertEquals("Bosch Professional", resultado.getNombreMarca());
        verify(marcaRepository, times(1)).findById(1);
        verify(marcaRepository, times(1)).save(any(Marca.class));
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al actualizar marca inexistente")
    void deberiaFallarAlActualizarMarcaInexistente() {

        MarcaRequestDTO request = MarcaRequestDTO.builder()
                .nombreMarca("Bosch Professional")
                .build();

        when(marcaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                marcaService.actualizar(99, request)
        );

        verify(marcaRepository, times(1)).findById(99);
        verify(marcaRepository, never()).save(any(Marca.class));
    }

    @Test
    @DisplayName("Debería eliminar una marca existente")
    void deberiaEliminarMarca() {

        when(marcaRepository.existsById(1)).thenReturn(true);
        doNothing().when(marcaRepository).deleteById(1);

        marcaService.eliminar(1);

        verify(marcaRepository, times(1)).existsById(1);
        verify(marcaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar marca inexistente")
    void deberiaFallarAlEliminarMarcaInexistente() {

        when(marcaRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                marcaService.eliminar(99)
        );

        verify(marcaRepository, times(1)).existsById(99);
        verify(marcaRepository, never()).deleteById(anyInt());
    }
}
