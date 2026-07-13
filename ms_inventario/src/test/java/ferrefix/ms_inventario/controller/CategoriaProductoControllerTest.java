package ferrefix.ms_inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_inventario.dto.CategoriaProductoRequestDTO;
import ferrefix.ms_inventario.dto.CategoriaProductoResponseDTO;
import ferrefix.ms_inventario.exception.GlobalExceptionHandler;
import ferrefix.ms_inventario.service.CategoriaProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
class CategoriaProductoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoriaProductoService categoriaProductoService;

    @Spy
    private ferrefix.ms_inventario.assembler.CategoriaProductoAssembler categoriaProductoAssembler;

    @InjectMocks
    private CategoriaProductoController categoriaProductoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CategoriaProductoResponseDTO cat1;
    private CategoriaProductoResponseDTO cat2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoriaProductoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        cat1 = CategoriaProductoResponseDTO.builder()
                .idCategoria(1)
                .nombreCategoria("Herramientas")
                .build();

        cat2 = CategoriaProductoResponseDTO.builder()
                .idCategoria(2)
                .nombreCategoria("Pinturas")
                .build();
    }

    @Test
    @DisplayName("Debería crear una categoría de producto")
    void deberiaCrear() throws Exception {

        CategoriaProductoRequestDTO request = CategoriaProductoRequestDTO.builder()
                .nombreCategoria("Herramientas")
                .build();

        when(categoriaProductoService.crearCategoriaProducto(any(CategoriaProductoRequestDTO.class))).thenReturn(cat1);

        mockMvc.perform(post("/api/inventario/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCategoria", is(1)))
                .andExpect(jsonPath("$.nombreCategoria", is("Herramientas")));

        verify(categoriaProductoService, times(1)).crearCategoriaProducto(any(CategoriaProductoRequestDTO.class));
    }

    @Test
    @DisplayName("Debería listar todas las categorías")
    void deberiaListarTodas() throws Exception {

        when(categoriaProductoService.buscarTodasCategorias()).thenReturn(List.of(cat1, cat2));

        mockMvc.perform(get("/api/inventario/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idCategoria", is(1)))
                .andExpect(jsonPath("$.content[0].nombreCategoria", is("Herramientas")))
                .andExpect(jsonPath("$.content[1].idCategoria", is(2)))
                .andExpect(jsonPath("$.content[1].nombreCategoria", is("Pinturas")));

        verify(categoriaProductoService, times(1)).buscarTodasCategorias();
    }

    @Test
    @DisplayName("Debería obtener categoría por ID")
    void deberiaObtenerPorId() throws Exception {

        when(categoriaProductoService.buscarCategoriaPorId(1)).thenReturn(cat1);

        mockMvc.perform(get("/api/inventario/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria", is(1)))
                .andExpect(jsonPath("$.nombreCategoria", is("Herramientas")));

        verify(categoriaProductoService, times(1)).buscarCategoriaPorId(1);
    }

    @Test
    @DisplayName("Debería actualizar una categoría")
    void deberiaActualizar() throws Exception {

        CategoriaProductoRequestDTO request = CategoriaProductoRequestDTO.builder()
                .nombreCategoria("Herramientas Eléctricas")
                .build();

        CategoriaProductoResponseDTO actualizada = CategoriaProductoResponseDTO.builder()
                .idCategoria(1)
                .nombreCategoria("Herramientas Eléctricas")
                .build();

        when(categoriaProductoService.actualizarCategoriaProducto(eq(1), any(CategoriaProductoRequestDTO.class))).thenReturn(actualizada);

        mockMvc.perform(put("/api/inventario/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria", is(1)))
                .andExpect(jsonPath("$.nombreCategoria", is("Herramientas Eléctricas")));

        verify(categoriaProductoService, times(1)).actualizarCategoriaProducto(eq(1), any(CategoriaProductoRequestDTO.class));
    }

    @Test
    @DisplayName("Debería eliminar una categoría")
    void deberiaEliminar() throws Exception {

        doNothing().when(categoriaProductoService).eliminarCategoriaProducto(1);

        mockMvc.perform(delete("/api/inventario/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaProductoService, times(1)).eliminarCategoriaProducto(1);
    }
}
