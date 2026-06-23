package ferrefix.ms_usuarios.service;

import ferrefix.ms_usuarios.dto.CargoRequestDTO;
import ferrefix.ms_usuarios.dto.CargoResponseDTO;
import ferrefix.ms_usuarios.exception.BadRequestException;
import ferrefix.ms_usuarios.exception.ResourceNotFoundException;
import ferrefix.ms_usuarios.mapper.CargoMapper;
import ferrefix.ms_usuarios.model.Cargo;
import ferrefix.ms_usuarios.repository.CargoRepository;
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
class CargoServiceTest {

    @Mock
    private CargoRepository cargoRepository;

    @Spy
    private CargoMapper cargoMapper = new CargoMapper();

    @InjectMocks
    private CargoService cargoService;

    private Cargo cargoAdmin;
    private Cargo cargoVendedor;

    @BeforeEach
    void setUp() {
        cargoAdmin = Cargo.builder()
                .idCargo(1)
                .nombreCargo("Administrador")
                .build();

        cargoVendedor = Cargo.builder()
                .idCargo(2)
                .nombreCargo("Vendedor")
                .build();
    }

    @Test
    @DisplayName("Debería crear un nuevo cargo exitosamente")
    void deberiaCrearCargo() {

        CargoRequestDTO request = CargoRequestDTO.builder()
                .nombreCargo("Administrador")
                .build();

        when(cargoRepository.existsByNombreCargo("Administrador")).thenReturn(false);
        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargoAdmin);

        Cargo result = cargoService.crearCargo(request);

        assertNotNull(result);
        assertEquals(1, result.getIdCargo());
        assertEquals("Administrador", result.getNombreCargo());
        verify(cargoRepository, times(1)).existsByNombreCargo("Administrador");
        verify(cargoRepository, times(1)).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al crear un cargo duplicado")
    void deberiaFallarAlCrearCargoDuplicado() {

        CargoRequestDTO request = CargoRequestDTO.builder()
                .nombreCargo("Administrador")
                .build();

        when(cargoRepository.existsByNombreCargo("Administrador")).thenReturn(true);

        assertThrows(BadRequestException.class, () ->
                cargoService.crearCargo(request)
        );

        verify(cargoRepository, times(1)).existsByNombreCargo("Administrador");
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Debería listar todos los cargos")
    void deberiaListarTodos() {

        when(cargoRepository.findAll()).thenReturn(List.of(cargoAdmin, cargoVendedor));

        List<CargoResponseDTO> result = cargoService.buscarTodos();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Administrador", result.get(0).getNombreCargo());
        assertEquals("Vendedor", result.get(1).getNombreCargo());
        verify(cargoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería buscar cargo por ID")
    void deberiaBuscarPorId() {

        when(cargoRepository.findById(1)).thenReturn(Optional.of(cargoAdmin));

        CargoResponseDTO result = cargoService.buscarCargoPorId(1);

        assertNotNull(result);
        assertEquals(1, result.getIdCargo());
        assertEquals("Administrador", result.getNombreCargo());
        verify(cargoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar ID inexistente")
    void deberiaFallarAlBuscarInexistente() {

        when(cargoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                cargoService.buscarCargoPorId(99)
        );

        verify(cargoRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debería actualizar cargo existente")
    void deberiaActualizarCargo() {

        CargoRequestDTO request = CargoRequestDTO.builder()
                .nombreCargo("Administrador Senior")
                .build();

        when(cargoRepository.findById(1)).thenReturn(Optional.of(cargoAdmin));
        when(cargoRepository.existsByNombreCargo("Administrador Senior")).thenReturn(false);
        when(cargoRepository.save(any(Cargo.class))).thenAnswer(i -> i.getArgument(0));

        Cargo result = cargoService.actualizarCargo(1, request);

        assertNotNull(result);
        assertEquals(1, result.getIdCargo());
        assertEquals("Administrador Senior", result.getNombreCargo());
        verify(cargoRepository, times(1)).findById(1);
        verify(cargoRepository, times(1)).existsByNombreCargo("Administrador Senior");
        verify(cargoRepository, times(1)).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Debería lanzar BadRequestException al actualizar con nombre ya registrado por otro cargo")
    void deberiaFallarAlActualizarNombreOcupado() {

        CargoRequestDTO request = CargoRequestDTO.builder()
                .nombreCargo("Vendedor")
                .build();

        when(cargoRepository.findById(1)).thenReturn(Optional.of(cargoAdmin));
        when(cargoRepository.existsByNombreCargo("Vendedor")).thenReturn(true);

        assertThrows(BadRequestException.class, () ->
                cargoService.actualizarCargo(1, request)
        );

        verify(cargoRepository, times(1)).findById(1);
        verify(cargoRepository, times(1)).existsByNombreCargo("Vendedor");
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    @Test
    @DisplayName("Debería eliminar cargo existente")
    void deberiaEliminarCargo() {

        when(cargoRepository.existsById(1)).thenReturn(true);
        doNothing().when(cargoRepository).deleteById(1);

        cargoService.eliminarCargo(1);

        verify(cargoRepository, times(1)).existsById(1);
        verify(cargoRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar cargo inexistente")
    void deberiaFallarAlEliminarInexistente() {

        when(cargoRepository.existsById(99)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                cargoService.eliminarCargo(99)
        );

        verify(cargoRepository, times(1)).existsById(99);
        verify(cargoRepository, never()).deleteById(anyInt());
    }
}
