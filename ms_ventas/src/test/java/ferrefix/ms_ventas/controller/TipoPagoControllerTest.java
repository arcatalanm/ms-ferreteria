package ferrefix.ms_ventas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_ventas.dto.TipoPagoRequestDTO;
import ferrefix.ms_ventas.dto.TipoPagoResponseDTO;
import ferrefix.ms_ventas.exception.BadRequestException;
import ferrefix.ms_ventas.exception.GlobalExceptionHandler;
import ferrefix.ms_ventas.exception.ResourceNotFoundException;
import ferrefix.ms_ventas.service.TipoPagoService;
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
class TipoPagoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TipoPagoService tipoPagoService;

    @Spy
    private ferrefix.ms_ventas.assembler.TipoPagoAssembler tipoPagoAssembler;

    @InjectMocks
    private TipoPagoController tipoPagoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private TipoPagoResponseDTO response1;
    private TipoPagoResponseDTO response2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tipoPagoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        response1 = TipoPagoResponseDTO.builder()
                .idTipoPago(1)
                .nombreTipoPago("Efectivo")
                .build();

        response2 = TipoPagoResponseDTO.builder()
                .idTipoPago(2)
                .nombreTipoPago("Tarjeta de Crédito")
                .build();
    }

    @Test
    @DisplayName("Debería registrar un tipo de pago")
    void deberiaCrear() throws Exception {

        TipoPagoRequestDTO request = TipoPagoRequestDTO.builder()
                .nombreTipoPago("Efectivo")
                .build();

        when(tipoPagoService.crear(any(TipoPagoRequestDTO.class))).thenReturn(response1);

        mockMvc.perform(post("/api/ventas/tipos-pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTipoPago", is(1)))
                .andExpect(jsonPath("$.nombreTipoPago", is("Efectivo")));

        verify(tipoPagoService, times(1)).crear(any(TipoPagoRequestDTO.class));
    }

    @Test
    @DisplayName("Debería fallar al crear si el nombre ya existe (400 BadRequest)")
    void deberiaFallarAlCrearSiNombreExiste() throws Exception {

        TipoPagoRequestDTO request = TipoPagoRequestDTO.builder()
                .nombreTipoPago("Efectivo")
                .build();

        when(tipoPagoService.crear(any(TipoPagoRequestDTO.class)))
                .thenThrow(new BadRequestException("Ya existe un tipo de pago con el nombre: Efectivo"));

        mockMvc.perform(post("/api/ventas/tipos-pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Ya existe un tipo de pago con el nombre: Efectivo")));
    }

    @Test
    @DisplayName("Debería listar todos los tipos de pago")
    void deberiaListarTodos() throws Exception {

        when(tipoPagoService.obtenerTodos()).thenReturn(List.of(response1, response2));

        mockMvc.perform(get("/api/ventas/tipos-pago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idTipoPago", is(1)))
                .andExpect(jsonPath("$.content[0].nombreTipoPago", is("Efectivo")))
                .andExpect(jsonPath("$.content[1].idTipoPago", is(2)))
                .andExpect(jsonPath("$.content[1].nombreTipoPago", is("Tarjeta de Crédito")));

        verify(tipoPagoService, times(1)).obtenerTodos();
    }

    @Test
    @DisplayName("Debería obtener un tipo de pago por ID")
    void deberiaObtenerPorId() throws Exception {

        when(tipoPagoService.obtenerPorId(1)).thenReturn(response1);

        mockMvc.perform(get("/api/ventas/tipos-pago/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTipoPago", is(1)))
                .andExpect(jsonPath("$.nombreTipoPago", is("Efectivo")));

        verify(tipoPagoService, times(1)).obtenerPorId(1);
    }

    @Test
    @DisplayName("Debería retornar 404 si el tipo de pago no existe")
    void deberiaRetornar404SiNoExiste() throws Exception {

        when(tipoPagoService.obtenerPorId(99))
                .thenThrow(new ResourceNotFoundException("Tipo de pago no encontrado con ID: 99"));

        mockMvc.perform(get("/api/ventas/tipos-pago/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Tipo de pago no encontrado con ID: 99")));
    }

    @Test
    @DisplayName("Debería actualizar un tipo de pago")
    void deberiaActualizar() throws Exception {

        TipoPagoRequestDTO request = TipoPagoRequestDTO.builder()
                .nombreTipoPago("Efectivo Modificado")
                .build();

        TipoPagoResponseDTO actualizada = TipoPagoResponseDTO.builder()
                .idTipoPago(1)
                .nombreTipoPago("Efectivo Modificado")
                .build();

        when(tipoPagoService.actualizar(eq(1), any(TipoPagoRequestDTO.class))).thenReturn(actualizada);

        mockMvc.perform(put("/api/ventas/tipos-pago/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTipoPago", is(1)))
                .andExpect(jsonPath("$.nombreTipoPago", is("Efectivo Modificado")));

        verify(tipoPagoService, times(1)).actualizar(eq(1), any(TipoPagoRequestDTO.class));
    }

    @Test
    @DisplayName("Debería eliminar un tipo de pago")
    void deberiaEliminar() throws Exception {

        doNothing().when(tipoPagoService).eliminar(1);

        mockMvc.perform(delete("/api/ventas/tipos-pago/1"))
                .andExpect(status().isNoContent());

        verify(tipoPagoService, times(1)).eliminar(1);
    }
}
