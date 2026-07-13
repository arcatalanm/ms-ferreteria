package ferrefix.ms_ventas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ferrefix.ms_ventas.dto.DetalleVentaRequestDTO;
import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.dto.VentaRequestDTO;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import ferrefix.ms_ventas.exception.BadRequestException;
import ferrefix.ms_ventas.exception.GlobalExceptionHandler;
import ferrefix.ms_ventas.exception.ResourceNotFoundException;
import ferrefix.ms_ventas.service.VentaService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VentaService ventaService;

    @Spy
    private ferrefix.ms_ventas.assembler.VentaAssembler ventaAssembler;

    @Spy
    private ferrefix.ms_ventas.assembler.DetalleVentaAssembler detalleVentaAssembler;

    @InjectMocks
    private VentaController ventaController;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private VentaResponseDTO response1;
    private DetalleVentaResponseDTO detalleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(ventaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        detalleResponse = DetalleVentaResponseDTO.builder()
                .idProducto(101L)
                .nombreProducto("Martillo")
                .cantidad(2)
                .precioUnitario(10000)
                .subtotal(20000)
                .build();

        response1 = VentaResponseDTO.builder()
                .idVenta(1L)
                .runCliente("12345678")
                .runEmpleado("87654321")
                .fechaVenta(LocalDateTime.of(2026, 6, 20, 10, 30, 0))
                .totalVenta(20000)
                .neto(16807)
                .iva(3193)
                .nombreTipoPago("Efectivo")
                .detalles(List.of(detalleResponse))
                .build();
    }

    @Test
    @DisplayName("Debería crear una venta exitosamente (201 Created)")
    void deberiaCrearVenta() throws Exception {

        VentaRequestDTO request = VentaRequestDTO.builder()
                .runCliente("12.345.678-5")
                .runEmpleado("87.654.321-0")
                .idTipoPago(1)
                .detalles(List.of(DetalleVentaRequestDTO.builder()
                        .idProducto(101L)
                        .cantidad(2)
                        .build()))
                .build();

        when(ventaService.guardar(any(VentaRequestDTO.class))).thenReturn(response1);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idVenta", is(1)))
                .andExpect(jsonPath("$.runCliente", is("12345678")))
                .andExpect(jsonPath("$.totalVenta", is(20000)))
                .andExpect(jsonPath("$.neto", is(16807)))
                .andExpect(jsonPath("$.iva", is(3193)))
                .andExpect(jsonPath("$.detalles", hasSize(1)))
                .andExpect(jsonPath("$.detalles[0].nombreProducto", is("Martillo")));

        verify(ventaService, times(1)).guardar(any(VentaRequestDTO.class));
    }

    @Test
    @DisplayName("Debería fallar al crear si el cliente o empleado no existe (400 Bad Request)")
    void deberiaFallarAlCrearSiClienteOEmpleadoNoExiste() throws Exception {

        VentaRequestDTO request = VentaRequestDTO.builder()
                .runCliente("12.345.678-5")
                .runEmpleado("87.654.321-0")
                .idTipoPago(1)
                .detalles(List.of(DetalleVentaRequestDTO.builder()
                        .idProducto(101L)
                        .cantidad(2)
                        .build()))
                .build();

        when(ventaService.guardar(any(VentaRequestDTO.class)))
                .thenThrow(new BadRequestException("El cliente o empleado no existe."));

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("El cliente o empleado no existe.")));
    }

    @Test
    @DisplayName("Debería listar todas las ventas")
    void deberiaListarVentas() throws Exception {

        when(ventaService.listarVentas()).thenReturn(List.of(response1));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].idVenta", is(1)))
                .andExpect(jsonPath("$.content[0].runCliente", is("12345678")));

        verify(ventaService, times(1)).listarVentas();
    }

    @Test
    @DisplayName("Debería obtener venta por ID")
    void deberiaObtenerVentaPorId() throws Exception {

        when(ventaService.obtenerVentaPorId(1L)).thenReturn(response1);

        mockMvc.perform(get("/api/ventas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta", is(1)))
                .andExpect(jsonPath("$.runCliente", is("12345678")));

        verify(ventaService, times(1)).obtenerVentaPorId(1L);
    }

    @Test
    @DisplayName("Debería retornar 404 al buscar venta inexistente")
    void deberiaRetornar404AlBuscarInexistente() throws Exception {

        when(ventaService.obtenerVentaPorId(99L))
                .thenThrow(new ResourceNotFoundException("No se encontró la venta con ID: 99"));

        mockMvc.perform(get("/api/ventas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("No se encontró la venta con ID: 99")));
    }

    @Test
    @DisplayName("Debería obtener ventas por RUN de cliente")
    void deberiaObtenerVentasPorRunCliente() throws Exception {

        when(ventaService.buscarVentasPorRunCliente("12.345.678-5")).thenReturn(List.of(response1));

        mockMvc.perform(get("/api/ventas/run/12.345.678-5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].idVenta", is(1)));

        verify(ventaService, times(1)).buscarVentasPorRunCliente("12.345.678-5");
    }

    @Test
    @DisplayName("Debería listar detalles de una venta")
    void deberiaListarDetalles() throws Exception {

        when(ventaService.buscarDetallesPorVenta(1L)).thenReturn(List.of(detalleResponse));

        mockMvc.perform(get("/api/ventas/1/detalles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].idProducto", is(101)))
                .andExpect(jsonPath("$.content[0].nombreProducto", is("Martillo")));

        verify(ventaService, times(1)).buscarDetallesPorVenta(1L);
    }

    @Test
    @DisplayName("Debería obtener un detalle específico de una venta")
    void deberiaObtenerDetalleEspecifico() throws Exception {

        when(ventaService.buscarDetallePorId(1L, 10L)).thenReturn(detalleResponse);

        mockMvc.perform(get("/api/ventas/1/detalles/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto", is(101)))
                .andExpect(jsonPath("$.nombreProducto", is("Martillo")));

        verify(ventaService, times(1)).buscarDetallePorId(1L, 10L);
    }

    @Test
    @DisplayName("Debería eliminar una venta")
    void deberiaEliminarVenta() throws Exception {

        doNothing().when(ventaService).eliminarVenta(1L);

        mockMvc.perform(delete("/api/ventas/1"))
                .andExpect(status().isNoContent());

        verify(ventaService, times(1)).eliminarVenta(1L);
    }
}
