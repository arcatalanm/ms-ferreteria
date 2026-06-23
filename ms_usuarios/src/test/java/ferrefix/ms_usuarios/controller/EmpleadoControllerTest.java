package ferrefix.ms_usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ferrefix.ms_usuarios.dto.EmpleadoRequestDTO;
import ferrefix.ms_usuarios.dto.EmpleadoResponseDTO;
import ferrefix.ms_usuarios.exception.GlobalExceptionHandler;
import ferrefix.ms_usuarios.service.EmpleadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmpleadoService empleadoService;

    @InjectMocks
    private EmpleadoController empleadoController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private EmpleadoResponseDTO response1;
    private EmpleadoResponseDTO response2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(empleadoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        response1 = EmpleadoResponseDTO.builder()
                .runEmpleadoCompleto("12345678-5")
                .nombreEmpleadoCompleto("Juan Carlos Pérez González")
                .emailEmpleado("juan.perez@empresa.com")
                .telefonoEmpleado("987654321")
                .nombreCargo("Administrador")
                .fechaContratacionEmpleado(LocalDate.of(2020, 1, 15))
                .build();

        response2 = EmpleadoResponseDTO.builder()
                .runEmpleadoCompleto("87654321-0")
                .nombreEmpleadoCompleto("Ana Silva Rojas")
                .emailEmpleado("ana.silva@empresa.com")
                .telefonoEmpleado("912345678")
                .nombreCargo("Vendedor")
                .fechaContratacionEmpleado(LocalDate.of(2021, 6, 1))
                .build();
    }

    @Test
    @DisplayName("Debería crear un empleado")
    void deberiaCrear() throws Exception {

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

        when(empleadoService.crearEmpleado(any(EmpleadoRequestDTO.class))).thenReturn(response1);

        mockMvc.perform(post("/api/usuarios/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.runEmpleadoCompleto", is("12345678-5")))
                .andExpect(jsonPath("$.nombreEmpleadoCompleto", is("Juan Carlos Pérez González")));

        verify(empleadoService, times(1)).crearEmpleado(any(EmpleadoRequestDTO.class));
    }

    @Test
    @DisplayName("Debería listar todos los empleados")
    void deberiaListarTodos() throws Exception {

        when(empleadoService.buscarTodosEmpleados()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/api/usuarios/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].runEmpleadoCompleto", is("12345678-5")))
                .andExpect(jsonPath("$.content[0].nombreEmpleadoCompleto", is("Juan Carlos Pérez González")))
                .andExpect(jsonPath("$.content[1].runEmpleadoCompleto", is("87654321-0")))
                .andExpect(jsonPath("$.content[1].nombreEmpleadoCompleto", is("Ana Silva Rojas")));

        verify(empleadoService, times(1)).buscarTodosEmpleados();
    }

    @Test
    @DisplayName("Debería obtener un empleado por RUN")
    void deberiaObtenerPorRun() throws Exception {

        when(empleadoService.buscarEmpleadoPorRun(12345678)).thenReturn(response1);

        mockMvc.perform(get("/api/usuarios/empleados/run/12.345.678-5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.runEmpleadoCompleto", is("12345678-5")))
                .andExpect(jsonPath("$.nombreEmpleadoCompleto", is("Juan Carlos Pérez González")));

        verify(empleadoService, times(1)).buscarEmpleadoPorRun(12345678);
    }

    @Test
    @DisplayName("Debería actualizar un empleado")
    void deberiaActualizar() throws Exception {

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

        EmpleadoResponseDTO actualizada = EmpleadoResponseDTO.builder()
                .runEmpleadoCompleto("12345678-5")
                .nombreEmpleadoCompleto("Juan Modificado Pérez González")
                .emailEmpleado("juan.perez@empresa.com")
                .telefonoEmpleado("987654321")
                .nombreCargo("Administrador")
                .fechaContratacionEmpleado(LocalDate.of(2020, 1, 15))
                .build();

        when(empleadoService.actualizarEmpleado(eq(12345678), any(EmpleadoRequestDTO.class))).thenReturn(actualizada);

        mockMvc.perform(put("/api/usuarios/empleados/run/12.345.678-5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.runEmpleadoCompleto", is("12345678-5")))
                .andExpect(jsonPath("$.nombreEmpleadoCompleto", is("Juan Modificado Pérez González")));

        verify(empleadoService, times(1)).actualizarEmpleado(eq(12345678), any(EmpleadoRequestDTO.class));
    }

    @Test
    @DisplayName("Debería eliminar un empleado")
    void deberiaEliminar() throws Exception {

        doNothing().when(empleadoService).eliminarEmpleado(12345678);

        mockMvc.perform(delete("/api/usuarios/empleados/run/12.345.678-5"))
                .andExpect(status().isNoContent());

        verify(empleadoService, times(1)).eliminarEmpleado(12345678);
    }
}
