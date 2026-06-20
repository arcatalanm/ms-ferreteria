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

/**
 * CargoServiceTest
 *
 * Visual Flowchart of the Service Unit Testing Architecture:
 *
 *   [Test Case] --------(Invokes Method)--------> [CargoService]
 *                                                       |
 *                                          ┌────────────┴────────────┐
 *                                          v (Spy)                   v (Mock)
 *                                    [CargoMapper]           [CargoRepository]
 *                                                                    |
 *                                                           (Returns Mock Entity)
 *                                                                    v
 *   [Assert Result] <-------(Asserts / Exceptions)-------------------┘
 *
 */
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

    // ==========================================
    // SETUP & INITIALIZATION
    // ==========================================
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

    /**
     * deberiaCrearCargo:
     * Verifies successful creation of a unique Cargo.
     *
     * Flow:
     * Request -> check existsByNombreCargo (false) -> Repository.save() -> Success
     *
     */
    @Test
    @DisplayName("Debería crear un nuevo cargo exitosamente")
    void deberiaCrearCargo() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        CargoRequestDTO request = CargoRequestDTO.builder()
                .nombreCargo("Administrador")
                .build();

        when(cargoRepository.existsByNombreCargo("Administrador")).thenReturn(false);
        when(cargoRepository.save(any(Cargo.class))).thenReturn(cargoAdmin);

        // ==========================================
        // 2. ACT
        // ==========================================
        Cargo result = cargoService.crearCargo(request);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(1, result.getIdCargo());
        assertEquals("Administrador", result.getNombreCargo());
        verify(cargoRepository, times(1)).existsByNombreCargo("Administrador");
        verify(cargoRepository, times(1)).save(any(Cargo.class));
    }

    /**
     * deberiaFallarAlCrearCargoDuplicado:
     * Verifies BadRequestException when trying to create a Cargo with a duplicated name.
     *
     * Flow:
     * Request -> check existsByNombreCargo (true) -> Throw BadRequestException -> Abort Save
     *
     */
    @Test
    @DisplayName("Debería lanzar BadRequestException al crear un cargo duplicado")
    void deberiaFallarAlCrearCargoDuplicado() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        CargoRequestDTO request = CargoRequestDTO.builder()
                .nombreCargo("Administrador")
                .build();

        when(cargoRepository.existsByNombreCargo("Administrador")).thenReturn(true);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () ->
                cargoService.crearCargo(request)
        );

        verify(cargoRepository, times(1)).existsByNombreCargo("Administrador");
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    /**
     * deberiaListarTodos:
     * Verifies fetching all cargos maps database entities to DTO responses.
     */
    @Test
    @DisplayName("Debería listar todos los cargos")
    void deberiaListarTodos() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(cargoRepository.findAll()).thenReturn(List.of(cargoAdmin, cargoVendedor));

        // ==========================================
        // 2. ACT
        // ==========================================
        List<CargoResponseDTO> result = cargoService.buscarTodos();

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Administrador", result.get(0).getNombreCargo());
        assertEquals("Vendedor", result.get(1).getNombreCargo());
        verify(cargoRepository, times(1)).findAll();
    }

    /**
     * deberiaBuscarPorId:
     * Verifies retrieving a single cargo by ID correctly translates it to response DTO.
     */
    @Test
    @DisplayName("Debería buscar cargo por ID")
    void deberiaBuscarPorId() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(cargoRepository.findById(1)).thenReturn(Optional.of(cargoAdmin));

        // ==========================================
        // 2. ACT
        // ==========================================
        CargoResponseDTO result = cargoService.buscarCargoPorId(1);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(1, result.getIdCargo());
        assertEquals("Administrador", result.getNombreCargo());
        verify(cargoRepository, times(1)).findById(1);
    }

    /**
     * deberiaFallarAlBuscarInexistente:
     * Verifies ResourceNotFoundException is thrown when searching for an invalid cargo ID.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar ID inexistente")
    void deberiaFallarAlBuscarInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(cargoRepository.findById(99)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () ->
                cargoService.buscarCargoPorId(99)
        );

        verify(cargoRepository, times(1)).findById(99);
    }

    /**
     * deberiaActualizarCargo:
     * Verifies cargo update updates the entity fields in the database.
     */
    @Test
    @DisplayName("Debería actualizar cargo existente")
    void deberiaActualizarCargo() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        CargoRequestDTO request = CargoRequestDTO.builder()
                .nombreCargo("Administrador Senior")
                .build();

        when(cargoRepository.findById(1)).thenReturn(Optional.of(cargoAdmin));
        when(cargoRepository.existsByNombreCargo("Administrador Senior")).thenReturn(false);
        when(cargoRepository.save(any(Cargo.class))).thenAnswer(i -> i.getArgument(0));

        // ==========================================
        // 2. ACT
        // ==========================================
        Cargo result = cargoService.actualizarCargo(1, request);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(1, result.getIdCargo());
        assertEquals("Administrador Senior", result.getNombreCargo());
        verify(cargoRepository, times(1)).findById(1);
        verify(cargoRepository, times(1)).existsByNombreCargo("Administrador Senior");
        verify(cargoRepository, times(1)).save(any(Cargo.class));
    }

    /**
     * deberiaFallarAlActualizarNombreOcupado:
     * Verifies BadRequestException when updating a cargo with a name that is already used by another cargo.
     */
    @Test
    @DisplayName("Debería lanzar BadRequestException al actualizar con nombre ya registrado por otro cargo")
    void deberiaFallarAlActualizarNombreOcupado() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        CargoRequestDTO request = CargoRequestDTO.builder()
                .nombreCargo("Vendedor")
                .build();

        when(cargoRepository.findById(1)).thenReturn(Optional.of(cargoAdmin));
        when(cargoRepository.existsByNombreCargo("Vendedor")).thenReturn(true);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () ->
                cargoService.actualizarCargo(1, request)
        );

        verify(cargoRepository, times(1)).findById(1);
        verify(cargoRepository, times(1)).existsByNombreCargo("Vendedor");
        verify(cargoRepository, never()).save(any(Cargo.class));
    }

    /**
     * deberiaEliminarCargo:
     * Verifies deletion of a cargo that exists.
     */
    @Test
    @DisplayName("Debería eliminar cargo existente")
    void deberiaEliminarCargo() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(cargoRepository.existsById(1)).thenReturn(true);
        doNothing().when(cargoRepository).deleteById(1);

        // ==========================================
        // 2. ACT
        // ==========================================
        cargoService.eliminarCargo(1);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        verify(cargoRepository, times(1)).existsById(1);
        verify(cargoRepository, times(1)).deleteById(1);
    }

    /**
     * deberiaFallarAlEliminarInexistente:
     * Verifies ResourceNotFoundException is thrown when trying to delete an invalid cargo ID.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al eliminar cargo inexistente")
    void deberiaFallarAlEliminarInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(cargoRepository.existsById(99)).thenReturn(false);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () ->
                cargoService.eliminarCargo(99)
        );

        verify(cargoRepository, times(1)).existsById(99);
        verify(cargoRepository, never()).deleteById(anyInt());
    }
}
