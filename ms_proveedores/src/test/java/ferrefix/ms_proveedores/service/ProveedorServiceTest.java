package ferrefix.ms_proveedores.service;

import feign.FeignException;
import ferrefix.ms_proveedores.client.DireccionClient;
import ferrefix.ms_proveedores.dto.DireccionDTO;
import ferrefix.ms_proveedores.dto.ProveedorRequestDTO;
import ferrefix.ms_proveedores.dto.ProveedorResponseDTO;
import ferrefix.ms_proveedores.exception.BadRequestException;
import ferrefix.ms_proveedores.exception.ResourceNotFoundException;
import ferrefix.ms_proveedores.mapper.ProveedorMapper;
import ferrefix.ms_proveedores.model.Proveedor;
import ferrefix.ms_proveedores.repository.ProveedorRepository;
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
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @Spy
    private ProveedorMapper proveedorMapper = new ProveedorMapper();

    @Mock
    private DireccionClient direccionClient;

    @InjectMocks
    private ProveedorService proveedorService;

    private Proveedor proveedor1;
    private Proveedor proveedor2;
    private DireccionDTO direccionDTO;

    @BeforeEach
    void setUp() {
        proveedor1 = Proveedor.builder()
                .idProveedor(1)
                .rutProveedor(12345678)
                .dvProveedor('5')
                .nombreProveedor("Proveedor Uno")
                .giroProveedor("Ferretería")
                .direccionProveedor(10L)
                .telefonoProveedor("987654321")
                .correoProveedor("uno@proveedor.com")
                .build();

        proveedor2 = Proveedor.builder()
                .idProveedor(2)
                .rutProveedor(87654321)
                .dvProveedor('0')
                .nombreProveedor("Proveedor Dos")
                .giroProveedor("Construcción")
                .direccionProveedor(20L)
                .telefonoProveedor("123456789")
                .correoProveedor("dos@proveedor.com")
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
    @DisplayName("Debería guardar un nuevo proveedor exitosamente")
    void deberiaGuardarProveedor() {

        ProveedorRequestDTO request = ProveedorRequestDTO.builder()
                .rutProveedor("12.345.678-5")
                .nombreProveedor("Proveedor Uno")
                .giroProveedor("Ferretería")
                .direccionProveedor(10L)
                .telefonoProveedor("987654321")
                .correoProveedor("uno@proveedor.com")
                .build();

        when(proveedorRepository.findByRutProveedor(12345678)).thenReturn(Optional.empty());
        when(proveedorRepository.save(any(Proveedor.class))).thenReturn(proveedor1);
        when(direccionClient.obtenerDireccionPorId(10L)).thenReturn(direccionDTO);

        ProveedorResponseDTO result = proveedorService.guardar(request);

        assertNotNull(result);
        assertEquals(1, result.getIdProveedor());
        assertEquals("12345678-5", result.getRutProveedor());
        verify(proveedorRepository, times(1)).findByRutProveedor(12345678);
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
        verify(direccionClient, times(1)).obtenerDireccionPorId(10L);
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al guardar con un RUT inválido")
    void deberiaFallarAlGuardarRutInvalido() {

        ProveedorRequestDTO request = ProveedorRequestDTO.builder()
                .rutProveedor("12.345-K")
                .build();

        assertThrows(BadRequestException.class, () ->
                proveedorService.guardar(request)
        );

        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al guardar con un RUT duplicado")
    void deberiaFallarAlGuardarRutDuplicado() {

        ProveedorRequestDTO request = ProveedorRequestDTO.builder()
                .rutProveedor("12.345.678-5")
                .build();

        when(proveedorRepository.findByRutProveedor(12345678)).thenReturn(Optional.of(proveedor1));

        assertThrows(BadRequestException.class, () ->
                proveedorService.guardar(request)
        );

        verify(proveedorRepository, times(1)).findByRutProveedor(12345678);
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("Debería listar todos los proveedores")
    void deberiaListarTodos() {

        when(proveedorRepository.findAll()).thenReturn(List.of(proveedor1, proveedor2));
        when(direccionClient.obtenerDireccionPorId(10L)).thenReturn(direccionDTO);

        List<ProveedorResponseDTO> result = proveedorService.listarTodos();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getIdProveedor());
        assertEquals(2, result.get(1).getIdProveedor());
        verify(proveedorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería buscar proveedor por ID")
    void deberiaBuscarPorId() {

        when(proveedorRepository.findById(1)).thenReturn(Optional.of(proveedor1));
        when(direccionClient.obtenerDireccionPorId(10L)).thenReturn(direccionDTO);

        ProveedorResponseDTO result = proveedorService.buscarPorId(1);

        assertNotNull(result);
        assertEquals(1, result.getIdProveedor());
        verify(proveedorRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar ID inexistente")
    void deberiaFallarAlBuscarInexistente() {

        when(proveedorRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                proveedorService.buscarPorId(99)
        );

        verify(proveedorRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debería actualizar proveedor existente exitosamente")
    void deberiaActualizarProveedor() {

        ProveedorRequestDTO request = ProveedorRequestDTO.builder()
                .rutProveedor("12.345.678-5")
                .nombreProveedor("Proveedor Modificado")
                .giroProveedor("Ferretería")
                .direccionProveedor(10L)
                .telefonoProveedor("987654321")
                .correoProveedor("uno@proveedor.com")
                .build();

        when(proveedorRepository.findById(1)).thenReturn(Optional.of(proveedor1));
        when(proveedorRepository.findByRutProveedor(12345678)).thenReturn(Optional.empty());
        when(proveedorRepository.save(any(Proveedor.class))).thenAnswer(i -> i.getArgument(0));
        when(direccionClient.obtenerDireccionPorId(10L)).thenReturn(direccionDTO);

        ProveedorResponseDTO result = proveedorService.actualizar(1, request);

        assertNotNull(result);
        assertEquals(1, result.getIdProveedor());
        assertEquals("Proveedor Modificado", result.getNombreProveedor());
        verify(proveedorRepository, times(1)).findById(1);
        verify(proveedorRepository, times(1)).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al actualizar con un RUT ya registrado por otro proveedor")
    void deberiaFallarAlActualizarRutOcupado() {

        ProveedorRequestDTO request = ProveedorRequestDTO.builder()
                .rutProveedor("87.654.321-4")
                .build();

        when(proveedorRepository.findById(1)).thenReturn(Optional.of(proveedor1));
        when(proveedorRepository.findByRutProveedor(87654321)).thenReturn(Optional.of(proveedor2));

        assertThrows(BadRequestException.class, () ->
                proveedorService.actualizar(1, request)
        );

        verify(proveedorRepository, times(1)).findById(1);
        verify(proveedorRepository, times(1)).findByRutProveedor(87654321);
        verify(proveedorRepository, never()).save(any(Proveedor.class));
    }

    @Test
    @DisplayName("Debería eliminar proveedor existente")
    void deberiaEliminarProveedor() {

        when(proveedorRepository.existsById(1)).thenReturn(true);
        doNothing().when(proveedorRepository).deleteById(1);

        proveedorService.eliminar(1);

        verify(proveedorRepository, times(1)).existsById(1);
        verify(proveedorRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar proveedor inexistente")
    void deberiaFallarAlEliminarInexistente() {

        when(proveedorRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                proveedorService.eliminar(99)
        );

        verify(proveedorRepository, times(1)).existsById(99);
        verify(proveedorRepository, never()).deleteById(anyInt());
    }
}
