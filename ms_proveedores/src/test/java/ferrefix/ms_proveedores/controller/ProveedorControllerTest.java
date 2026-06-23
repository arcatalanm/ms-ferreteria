package ferrefix.ms_proveedores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_proveedores.dto.ProveedorRequestDTO;
import ferrefix.ms_proveedores.dto.ProveedorResponseDTO;
import ferrefix.ms_proveedores.exception.GlobalExceptionHandler;
import ferrefix.ms_proveedores.service.ProveedorService;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProveedorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProveedorService proveedorService;

    @InjectMocks
    private ProveedorController proveedorController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProveedorResponseDTO response1;
    private ProveedorResponseDTO response2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(proveedorController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        response1 = ProveedorResponseDTO.builder()
                .idProveedor(1)
                .rutProveedor("12.345.678-5")
                .nombreProveedor("Proveedor Uno")
                .giroProveedor("Ferretería")
                .telefonoProveedor("987654321")
                .correoProveedor("uno@proveedor.com")
                .build();

        response2 = ProveedorResponseDTO.builder()
                .idProveedor(2)
                .rutProveedor("87.654.321-0")
                .nombreProveedor("Proveedor Dos")
                .giroProveedor("Construcción")
                .telefonoProveedor("123456789")
                .correoProveedor("dos@proveedor.com")
                .build();
    }

    @Test
    @DisplayName("Debería crear un proveedor")
    void deberiaCrear() throws Exception {

        ProveedorRequestDTO request = ProveedorRequestDTO.builder()
                .rutProveedor("12.345.678-5")
                .nombreProveedor("Proveedor Uno")
                .giroProveedor("Ferretería")
                .direccionProveedor(10L)
                .telefonoProveedor("987654321")
                .correoProveedor("uno@proveedor.com")
                .build();

        when(proveedorService.guardar(any(ProveedorRequestDTO.class))).thenReturn(response1);

        mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProveedor", is(1)))
                .andExpect(jsonPath("$.nombreProveedor", is("Proveedor Uno")));

        verify(proveedorService, times(1)).guardar(any(ProveedorRequestDTO.class));
    }

    @Test
    @DisplayName("Debería listar todos los proveedores")
    void deberiaListarTodos() throws Exception {

        when(proveedorService.listarTodos()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/api/proveedores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idProveedor", is(1)))
                .andExpect(jsonPath("$.content[0].nombreProveedor", is("Proveedor Uno")))
                .andExpect(jsonPath("$.content[1].idProveedor", is(2)))
                .andExpect(jsonPath("$.content[1].nombreProveedor", is("Proveedor Dos")));

        verify(proveedorService, times(1)).listarTodos();
    }

    @Test
    @DisplayName("Debería obtener proveedor por ID")
    void deberiaObtenerPorId() throws Exception {

        when(proveedorService.buscarPorId(1)).thenReturn(response1);

        mockMvc.perform(get("/api/proveedores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProveedor", is(1)))
                .andExpect(jsonPath("$.nombreProveedor", is("Proveedor Uno")));

        verify(proveedorService, times(1)).buscarPorId(1);
    }

    @Test
    @DisplayName("Debería actualizar un proveedor")
    void deberiaActualizar() throws Exception {

        ProveedorRequestDTO request = ProveedorRequestDTO.builder()
                .rutProveedor("12.345.678-5")
                .nombreProveedor("Proveedor Modificado")
                .giroProveedor("Ferretería")
                .direccionProveedor(10L)
                .telefonoProveedor("987654321")
                .correoProveedor("uno@proveedor.com")
                .build();

        ProveedorResponseDTO actualizada = ProveedorResponseDTO.builder()
                .idProveedor(1)
                .rutProveedor("12.345.678-5")
                .nombreProveedor("Proveedor Modificado")
                .giroProveedor("Ferretería")
                .telefonoProveedor("987654321")
                .correoProveedor("uno@proveedor.com")
                .build();

        when(proveedorService.actualizar(eq(1), any(ProveedorRequestDTO.class))).thenReturn(actualizada);

        mockMvc.perform(put("/api/proveedores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProveedor", is(1)))
                .andExpect(jsonPath("$.nombreProveedor", is("Proveedor Modificado")));

        verify(proveedorService, times(1)).actualizar(eq(1), any(ProveedorRequestDTO.class));
    }

    @Test
    @DisplayName("Debería eliminar un proveedor")
    void deberiaEliminar() throws Exception {

        doNothing().when(proveedorService).eliminar(1);

        mockMvc.perform(delete("/api/proveedores/1"))
                .andExpect(status().isNoContent());

        verify(proveedorService, times(1)).eliminar(1);
    }
}
