package ferrefix.ms_compras.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_compras.dto.CompraRequestDTO;
import ferrefix.ms_compras.dto.CompraResponseDTO;
import ferrefix.ms_compras.dto.DetalleCompraRequestDTO;
import ferrefix.ms_compras.dto.DetalleCompraResponseDTO;
import ferrefix.ms_compras.exception.GlobalExceptionHandler;
import ferrefix.ms_compras.service.CompraService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CompraControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CompraService compraService;

    @InjectMocks
    private CompraController compraController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private CompraResponseDTO compra1;
    private CompraResponseDTO compra2;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(compraController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        compra1 = CompraResponseDTO.builder()
                .idCompra(1L)
                .idProveedor(10)
                .fechaCompra(LocalDateTime.now())
                .totalCompra(50000)
                .estado("SOLICITADO")
                .detalles(List.of(
                        DetalleCompraResponseDTO.builder()
                                .idDetalleCompra(1L)
                                .idProducto(101L)
                                .cantidad(5)
                                .precioCompraUnitario(10000)
                                .build()
                ))
                .build();

        compra2 = CompraResponseDTO.builder()
                .idCompra(2L)
                .idProveedor(20)
                .fechaCompra(LocalDateTime.now())
                .totalCompra(30000)
                .estado("RECIBIDO")
                .detalles(List.of(
                        DetalleCompraResponseDTO.builder()
                                .idDetalleCompra(2L)
                                .idProducto(102L)
                                .cantidad(3)
                                .precioCompraUnitario(10000)
                                .build()
                ))
                .build();
    }

    @Test
    @DisplayName("Debería listar todas las compras")
    void deberiaListarTodas() throws Exception {

        when(compraService.listarTodas()).thenReturn(List.of(compra1, compra2));

        mockMvc.perform(get("/api/compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idCompra", is(1)))
                .andExpect(jsonPath("$.content[0].estado", is("SOLICITADO")))
                .andExpect(jsonPath("$.content[1].idCompra", is(2)))
                .andExpect(jsonPath("$.content[1].estado", is("RECIBIDO")));

        verify(compraService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Debería obtener compra por ID")
    void deberiaObtenerPorId() throws Exception {

        when(compraService.obtenerPorId(1L)).thenReturn(compra1);

        mockMvc.perform(get("/api/compras/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompra", is(1)))
                .andExpect(jsonPath("$.estado", is("SOLICITADO")))
                .andExpect(jsonPath("$.totalCompra", is(50000)));

        verify(compraService, times(1)).obtenerPorId(1L);
    }

    @Test
    @DisplayName("Debería crear una orden de compra")
    void deberiaCrearOrden() throws Exception {

        CompraRequestDTO request = CompraRequestDTO.builder()
                .idProveedor(10)
                .detalles(List.of(
                        DetalleCompraRequestDTO.builder()
                                .idProducto(101L)
                                .cantidad(5)
                                .precioCompraUnitario(10000)
                                .build()
                ))
                .build();

        when(compraService.crearOrdenCompra(any(CompraRequestDTO.class))).thenReturn(compra1);

        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCompra", is(1)))
                .andExpect(jsonPath("$.estado", is("SOLICITADO")));

        verify(compraService, times(1)).crearOrdenCompra(any(CompraRequestDTO.class));
    }

    @Test
    @DisplayName("Debería recibir mercancía (procesar recepción)")
    void deberiaRecibirMercancia() throws Exception {

        CompraResponseDTO recibida = CompraResponseDTO.builder()
                .idCompra(1L)
                .idProveedor(10)
                .fechaCompra(LocalDateTime.now())
                .totalCompra(50000)
                .estado("RECIBIDO")
                .detalles(List.of(
                        DetalleCompraResponseDTO.builder()
                                .idDetalleCompra(1L)
                                .idProducto(101L)
                                .cantidad(5)
                                .precioCompraUnitario(10000)
                                .build()
                ))
                .build();

        when(compraService.procesarRecepcionMercancia(1L)).thenReturn(recibida);

        mockMvc.perform(put("/api/compras/1/recibir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCompra", is(1)))
                .andExpect(jsonPath("$.estado", is("RECIBIDO")));

        verify(compraService, times(1)).procesarRecepcionMercancia(1L);
    }
}
