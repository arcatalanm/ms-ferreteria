package ferrefix.ms_inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_inventario.dto.ProductoRequestDTO;
import ferrefix.ms_inventario.dto.ProductoResponseDTO;
import ferrefix.ms_inventario.exception.GlobalExceptionHandler;
import ferrefix.ms_inventario.service.ProductoService;
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
class ProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductoService productoService;

    @InjectMocks
    private ProductoController productoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ProductoResponseDTO p1;
    private ProductoResponseDTO p2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        p1 = ProductoResponseDTO.builder()
                .id(101L)
                .codigoBarras("123456789")
                .nombre("Martillo de Uña")
                .stock(10)
                .precioVenta(5000)
                .categoria("Herramientas")
                .unidadMedida("Unidad")
                .build();

        p2 = ProductoResponseDTO.builder()
                .id(102L)
                .codigoBarras("987654321")
                .nombre("Alicate Universal")
                .stock(15)
                .precioVenta(6000)
                .categoria("Herramientas")
                .unidadMedida("Unidad")
                .build();
    }

    @Test
    @DisplayName("Debería crear un producto")
    void deberiaCrear() throws Exception {

        ProductoRequestDTO request = ProductoRequestDTO.builder()
                .categoria(1)
                .unidadMedida(1)
                .codigoBarras("123456789")
                .nombre("Martillo de Uña")
                .stock(10)
                .precioVenta(5000)
                .build();

        when(productoService.crearProducto(any(ProductoRequestDTO.class))).thenReturn(p1);

        mockMvc.perform(post("/api/inventario/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.nombre", is("Martillo de Uña")))
                .andExpect(jsonPath("$.codigoBarras", is("123456789")));

        verify(productoService, times(1)).crearProducto(any(ProductoRequestDTO.class));
    }

    @Test
    @DisplayName("Debería listar todos los productos")
    void deberiaListarTodos() throws Exception {

        when(productoService.buscarTodosProductos()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/inventario/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(101)))
                .andExpect(jsonPath("$.content[0].nombre", is("Martillo de Uña")))
                .andExpect(jsonPath("$.content[1].id", is(102)))
                .andExpect(jsonPath("$.content[1].nombre", is("Alicate Universal")));

        verify(productoService, times(1)).buscarTodosProductos();
    }

    @Test
    @DisplayName("Debería obtener un producto por ID")
    void deberiaObtenerPorId() throws Exception {

        when(productoService.buscarProductoPorId(101L)).thenReturn(p1);

        mockMvc.perform(get("/api/inventario/productos/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.nombre", is("Martillo de Uña")))
                .andExpect(jsonPath("$.codigoBarras", is("123456789")));

        verify(productoService, times(1)).buscarProductoPorId(101L);
    }

    @Test
    @DisplayName("Debería actualizar un producto")
    void deberiaActualizar() throws Exception {

        ProductoRequestDTO request = ProductoRequestDTO.builder()
                .categoria(1)
                .unidadMedida(1)
                .codigoBarras("123456789")
                .nombre("Martillo de Uña Modificado")
                .stock(12)
                .precioVenta(5500)
                .build();

        ProductoResponseDTO actualizada = ProductoResponseDTO.builder()
                .id(101L)
                .codigoBarras("123456789")
                .nombre("Martillo de Uña Modificado")
                .stock(12)
                .precioVenta(5500)
                .categoria("Herramientas")
                .unidadMedida("Unidad")
                .build();

        when(productoService.actualizarProducto(eq(101L), any(ProductoRequestDTO.class))).thenReturn(actualizada);

        mockMvc.perform(put("/api/inventario/productos/101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.nombre", is("Martillo de Uña Modificado")))
                .andExpect(jsonPath("$.stock", is(12)));

        verify(productoService, times(1)).actualizarProducto(eq(101L), any(ProductoRequestDTO.class));
    }

    @Test
    @DisplayName("Debería eliminar un producto")
    void deberiaEliminar() throws Exception {

        doNothing().when(productoService).eliminarProducto(101L);

        mockMvc.perform(delete("/api/inventario/productos/101"))
                .andExpect(status().isNoContent());

        verify(productoService, times(1)).eliminarProducto(101L);
    }

    @Test
    @DisplayName("Debería descontar stock de un producto")
    void deberiaDescontarStock() throws Exception {

        doNothing().when(productoService).descontarStock(101L, 5);

        mockMvc.perform(patch("/api/inventario/productos/101/descontar-stock")
                        .param("cantidad", "5"))
                .andExpect(status().isOk());

        verify(productoService, times(1)).descontarStock(101L, 5);
    }
}
