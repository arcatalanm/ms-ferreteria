package ferrefix.ms_arriendo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_arriendo.dto.MaquinaRequestDTO;
import ferrefix.ms_arriendo.dto.MaquinaResponseDTO;
import ferrefix.ms_arriendo.dto.ProcesarArriendoRequestDTO;
import ferrefix.ms_arriendo.exception.GlobalExceptionHandler;
import ferrefix.ms_arriendo.service.MaquinaArriendoService;
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
class MaquinaArriendoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MaquinaArriendoService maquinaArriendoService;

    @Spy
    private ferrefix.ms_arriendo.assembler.MaquinaArriendoAssembler maquinaArriendoAssembler;

    @InjectMocks
    private MaquinaArriendoController maquinaArriendoController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MaquinaResponseDTO maquinaDisponible;
    private MaquinaResponseDTO maquinaArrendada;

    @BeforeEach
    void setUp() {

        mockMvc = MockMvcBuilders.standaloneSetup(maquinaArriendoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        maquinaDisponible = MaquinaResponseDTO.builder()
                .idEquipo(1)
                .codigoInterno("MAQ-001")
                .nombreMaquina("Taladro Percutor")
                .precioPorDia(15000)
                .estado("DISPONIBLE")
                .build();

        maquinaArrendada = MaquinaResponseDTO.builder()
                .idEquipo(2)
                .codigoInterno("MAQ-002")
                .nombreMaquina("Esmeril Angular")
                .precioPorDia(12000)
                .estado("ARRENDADO")
                .runCliente(12345678)
                .fechaDevolucionPactada(LocalDate.now().plusDays(5))
                .build();
    }

    @Test
    @DisplayName("Debería listar todas las máquinas de arriendo")
    void deberiaListarTodas() throws Exception {

        when(maquinaArriendoService.listarTodas()).thenReturn(List.of(maquinaDisponible, maquinaArrendada));

        mockMvc.perform(get("/api/arriendos/maquinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idEquipo", is(1)))
                .andExpect(jsonPath("$.content[0].estado", is("DISPONIBLE")))
                .andExpect(jsonPath("$.content[1].idEquipo", is(2)))
                .andExpect(jsonPath("$.content[1].estado", is("ARRENDADO")));

        verify(maquinaArriendoService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Debería registrar una nueva máquina de arriendo")
    void deberiaRegistrar() throws Exception {

        MaquinaRequestDTO request = MaquinaRequestDTO.builder()
                .codigoInterno("MAQ-001")
                .nombreMaquina("Taladro Percutor")
                .precioPorDia(15000)
                .build();

        when(maquinaArriendoService.registrar(any(MaquinaRequestDTO.class))).thenReturn(maquinaDisponible);

        mockMvc.perform(post("/api/arriendos/maquinas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idEquipo", is(1)))
                .andExpect(jsonPath("$.codigoInterno", is("MAQ-001")))
                .andExpect(jsonPath("$.estado", is("DISPONIBLE")));

        verify(maquinaArriendoService, times(1)).registrar(any(MaquinaRequestDTO.class));
    }

    @Test
    @DisplayName("Debería arrendar una máquina disponible")
    void deberiaArrendar() throws Exception {

        ProcesarArriendoRequestDTO request = ProcesarArriendoRequestDTO.builder()
                .runCliente(12345678)
                .diasArriendo(5)
                .build();

        when(maquinaArriendoService.procesarArriendo(eq(1), any(ProcesarArriendoRequestDTO.class))).thenReturn(maquinaArrendada);

        mockMvc.perform(put("/api/arriendos/maquinas/1/arrendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEquipo", is(2)))
                .andExpect(jsonPath("$.estado", is("ARRENDADO")))
                .andExpect(jsonPath("$.runCliente", is(12345678)));

        verify(maquinaArriendoService, times(1)).procesarArriendo(eq(1), any(ProcesarArriendoRequestDTO.class));
    }

    @Test
    @DisplayName("Debería devolver una máquina arrendada")
    void deberiaDevolver() throws Exception {

        when(maquinaArriendoService.procesarDevolucion(2)).thenReturn(maquinaDisponible);

        mockMvc.perform(put("/api/arriendos/maquinas/2/devolver"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEquipo", is(1)))
                .andExpect(jsonPath("$.estado", is("DISPONIBLE")));

        verify(maquinaArriendoService, times(1)).procesarDevolucion(2);
    }
}
