package ferrefix.ms_usuarios.service;

import ferrefix.ms_usuarios.dto.EmpleadoRequestDTO;
import ferrefix.ms_usuarios.dto.EmpleadoResponseDTO;
import ferrefix.ms_usuarios.exception.BadRequestException;
import ferrefix.ms_usuarios.exception.ResourceNotFoundException;
import ferrefix.ms_usuarios.mapper.EmpleadoMapper;
import ferrefix.ms_usuarios.model.Cargo;
import ferrefix.ms_usuarios.model.Empleado;
import ferrefix.ms_usuarios.repository.CargoRepository;
import ferrefix.ms_usuarios.repository.EmpleadoRepository;
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
 * EmpleadoServiceTest
 *
 * Visual Flowchart of the Service Unit Testing Architecture:
 *
 *   [Test Case] ────────────────────────(Invokes Method)───────────────────────> [EmpleadoService]
 *                                                                                      |
 *                                           ┌──────────────────────┬───────────────────┤
 *                                           v (Spy)                v (Mock)            v (Mock)
 *                                     [EmpleadoMapper]      [EmpleadoRepository]   [CargoRepository]
 *                                                                                      |
 *                                                                             (Validates Cargo Existence)
 *                                                                                      v
 *   [Assert Result] <─────────────────(Asserts / Exceptions)───────────────────────────┘
 *
 */
@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Spy
    private EmpleadoMapper empleadoMapper = new EmpleadoMapper();

    @InjectMocks
    private EmpleadoService empleadoService;

    private Cargo cargoAdmin;
    private Empleado empleado1;
    private Empleado empleado2;

    // ==========================================
    // SETUP & INITIALIZATION
    // ==========================================
    @BeforeEach
    void setUp() {
        cargoAdmin = Cargo.builder()
                .idCargo(1)
                .nombreCargo("Administrador")
                .build();

        empleado1 = Empleado.builder()
                .runEmpleado(12345678)
                .dvEmpleado('5')
                .pnombreEmpleado("Juan")
                .snombreEmpleado("Carlos")
                .appaternoEmpleado("Pérez")
                .apmaternoEmpleado("González")
                .emailEmpleado("juan.perez@empresa.com")
                .contrasenaEmpleado("password123")
                .sueldoBaseEmpleado(800000)
                .fechaContratacionEmpleado(LocalDate.of(2020, 1, 15))
                .telefonoEmpleado("987654321")
                .activoEmpleado(true)
                .cargo(cargoAdmin)
                .build();

        empleado2 = Empleado.builder()
                .runEmpleado(87654321)
                .dvEmpleado('0')
                .pnombreEmpleado("Ana")
                .appaternoEmpleado("Silva")
                .apmaternoEmpleado("Rojas")
                .emailEmpleado("ana.silva@empresa.com")
                .contrasenaEmpleado("password456")
                .sueldoBaseEmpleado(750000)
                .fechaContratacionEmpleado(LocalDate.of(2021, 6, 1))
                .telefonoEmpleado("912345678")
                .activoEmpleado(true)
                .cargo(cargoAdmin)
                .build();
    }

    /**
     * deberiaCrearEmpleado:
     * Verifies successful creation of a unique Empleado mapped with a valid Cargo reference.
     */
    @Test
    @DisplayName("Debería crear un empleado exitosamente")
    void deberiaCrearEmpleado() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        EmpleadoRequestDTO request = EmpleadoRequestDTO.builder()
                .rutEmpleado("12.345.678-5")
                .pnombreEmpleado("Juan")
                .snombreEmpleado("Carlos")
                .appaternoEmpleado("Pérez")
                .apmaternoEmpleado("González")
                .emailEmpleado("juan.perez@empresa.com")
                .contrasenaEmpleado("password123")
                .sueldoBaseEmpleado(800000)
                .fechaContratacionEmpleado(LocalDate.of(2020, 1, 15))
                .telefonoEmpleado("987654321")
                .idCargo(1)
                .build();

        when(empleadoRepository.existsById(12345678)).thenReturn(false);
        when(empleadoRepository.findByEmailEmpleado("juan.perez@empresa.com")).thenReturn(null);
        when(cargoRepository.findById(1)).thenReturn(Optional.of(cargoAdmin));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado1);

        // ==========================================
        // 2. ACT
        // ==========================================
        EmpleadoResponseDTO result = empleadoService.crearEmpleado(request);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals("12345678-5", result.getRunEmpleadoCompleto());
        assertEquals("Juan Carlos Pérez González", result.getNombreEmpleadoCompleto());
        verify(empleadoRepository, times(1)).existsById(12345678);
        verify(cargoRepository, times(1)).findById(1);
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }

    /**
     * deberiaFallarAlCrearRutInvalido:
     * Verifies BadRequestException when attempting to register an employee with an invalid RUT format.
     */
    @Test
    @DisplayName("Debería lanzar BadRequestException al crear con RUT inválido")
    void deberiaFallarAlCrearRutInvalido() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        EmpleadoRequestDTO request = EmpleadoRequestDTO.builder()
                .rutEmpleado("12.345-K")
                .build();

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () ->
                empleadoService.crearEmpleado(request)
        );

        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    /**
     * deberiaFallarAlCrearRunDuplicado:
     * Verifies BadRequestException when registering an employee with a duplicate RUN.
     */
    @Test
    @DisplayName("Debería lanzar BadRequestException al crear con un RUN duplicado")
    void deberiaFallarAlCrearRunDuplicado() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        EmpleadoRequestDTO request = EmpleadoRequestDTO.builder()
                .rutEmpleado("12.345.678-5")
                .build();

        when(empleadoRepository.existsById(12345678)).thenReturn(true);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () ->
                empleadoService.crearEmpleado(request)
        );

        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    /**
     * deberiaFallarAlCrearEmailDuplicado:
     * Verifies BadRequestException when registering an employee with a pre-existing email.
     */
    @Test
    @DisplayName("Debería lanzar BadRequestException al crear con un Email duplicado")
    void deberiaFallarAlCrearEmailDuplicado() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        EmpleadoRequestDTO request = EmpleadoRequestDTO.builder()
                .rutEmpleado("12.345.678-5")
                .emailEmpleado("ana.silva@empresa.com")
                .build();

        when(empleadoRepository.existsById(12345678)).thenReturn(false);
        when(empleadoRepository.findByEmailEmpleado("ana.silva@empresa.com")).thenReturn(empleado2);

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () ->
                empleadoService.crearEmpleado(request)
        );

        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    /**
     * deberiaFallarAlCrearCargoInexistente:
     * Verifies ResourceNotFoundException when referencing a Cargo ID that does not exist in the DB.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al crear con un Cargo inexistente")
    void deberiaFallarAlCrearCargoInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        EmpleadoRequestDTO request = EmpleadoRequestDTO.builder()
                .rutEmpleado("12.345.678-5")
                .emailEmpleado("juan.perez@empresa.com")
                .idCargo(99)
                .build();

        when(empleadoRepository.existsById(12345678)).thenReturn(false);
        when(empleadoRepository.findByEmailEmpleado("juan.perez@empresa.com")).thenReturn(null);
        when(cargoRepository.findById(99)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () ->
                empleadoService.crearEmpleado(request)
        );

        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    /**
     * deberiaListarTodos:
     * Verifies listing all active/inactive employee records.
     */
    @Test
    @DisplayName("Debería listar todos los empleados")
    void deberiaListarTodos() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(empleadoRepository.findAll()).thenReturn(List.of(empleado1, empleado2));

        // ==========================================
        // 2. ACT
        // ==========================================
        List<EmpleadoResponseDTO> result = empleadoService.buscarTodosEmpleados();

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(empleadoRepository, times(1)).findAll();
    }

    /**
     * deberiaBuscarPorRun:
     * Verifies finding a single employee by RUN works correctly.
     */
    @Test
    @DisplayName("Debería buscar empleado por RUN")
    void deberiaBuscarPorRun() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(empleadoRepository.findById(12345678)).thenReturn(Optional.of(empleado1));

        // ==========================================
        // 2. ACT
        // ==========================================
        EmpleadoResponseDTO result = empleadoService.buscarEmpleadoPorRun(12345678);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals("12345678-5", result.getRunEmpleadoCompleto());
        verify(empleadoRepository, times(1)).findById(12345678);
    }

    /**
     * deberiaFallarAlBuscarInexistente:
     * Verifies ResourceNotFoundException when searching a non-existent RUN.
     */
    @Test
    @DisplayName("Debería lanzar ResourceNotFoundException al buscar RUN inexistente")
    void deberiaFallarAlBuscarInexistente() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(empleadoRepository.findById(99)).thenReturn(Optional.empty());

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(ResourceNotFoundException.class, () ->
                empleadoService.buscarEmpleadoPorRun(99)
        );

        verify(empleadoRepository, times(1)).findById(99);
    }

    /**
     * deberiaActualizarEmpleado:
     * Verifies successful update of an existing employee's details.
     */
    @Test
    @DisplayName("Debería actualizar empleado existente exitosamente")
    void deberiaActualizarEmpleado() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        EmpleadoRequestDTO request = EmpleadoRequestDTO.builder()
                .rutEmpleado("12.345.678-5")
                .pnombreEmpleado("Juan Modificado")
                .appaternoEmpleado("Pérez")
                .apmaternoEmpleado("González")
                .emailEmpleado("juan.perez@empresa.com")
                .contrasenaEmpleado("password123")
                .sueldoBaseEmpleado(850000)
                .fechaContratacionEmpleado(LocalDate.of(2020, 1, 15))
                .telefonoEmpleado("987654321")
                .idCargo(1)
                .build();

        when(empleadoRepository.findById(12345678)).thenReturn(Optional.of(empleado1));
        when(empleadoRepository.findByEmailEmpleado("juan.perez@empresa.com")).thenReturn(null);
        when(cargoRepository.findById(1)).thenReturn(Optional.of(cargoAdmin));
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(i -> i.getArgument(0));

        // ==========================================
        // 2. ACT
        // ==========================================
        EmpleadoResponseDTO result = empleadoService.actualizarEmpleado(12345678, request);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertNotNull(result);
        assertEquals("Juan Modificado Pérez González", result.getNombreEmpleadoCompleto());
        verify(empleadoRepository, times(1)).findById(12345678);
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }

    /**
     * deberiaFallarAlActualizarMismatchedRun:
     * Verifies BadRequestException when URL RUN and request body RUN do not match.
     */
    @Test
    @DisplayName("Debería lanzar BadRequestException al actualizar con RUN URL diferente al RUN Body")
    void deberiaFallarAlActualizarMismatchedRun() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        EmpleadoRequestDTO request = EmpleadoRequestDTO.builder()
                .rutEmpleado("87.654.321-0") // RUN body: 87654321
                .build();

        when(empleadoRepository.findById(12345678)).thenReturn(Optional.of(empleado1));

        // ==========================================
        // 2. ACT & 3. ASSERT
        // ==========================================
        assertThrows(BadRequestException.class, () ->
                empleadoService.actualizarEmpleado(12345678, request) // URL run: 12345678
        );

        verify(empleadoRepository, never()).save(any(Empleado.class));
    }

    /**
     * deberiaDesactivarEmpleado:
     * Verifies logical deactivation (soft delete) of an existing employee.
     */
    @Test
    @DisplayName("Debería desactivar un empleado existente (baja lógica)")
    void deberiaDesactivarEmpleado() {
        // ==========================================
        // 1. ARRANGE
        // ==========================================
        when(empleadoRepository.findById(12345678)).thenReturn(Optional.of(empleado1));
        when(empleadoRepository.save(any(Empleado.class))).thenAnswer(i -> i.getArgument(0));

        // ==========================================
        // 2. ACT
        // ==========================================
        empleadoService.eliminarEmpleado(12345678);

        // ==========================================
        // 3. ASSERT
        // ==========================================
        assertFalse(empleado1.getActivoEmpleado());
        verify(empleadoRepository, times(1)).findById(12345678);
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }
}
