package ferrefix.ms_marcas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ferrefix.ms_marcas.dto.MarcaRequestDTO;
import ferrefix.ms_marcas.dto.MarcaResponseDTO;
import ferrefix.ms_marcas.exception.GlobalExceptionHandler;
import ferrefix.ms_marcas.service.MarcaService;
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

/**
 * ╔═══════════════════════════════════════════════════════════════════════╗
 * ║                         MarcaControllerTest                           ║
 * ║                                                                       ║
 * ║  Clase de pruebas unitarias para el controlador de Marcas (REST API).  ║
 * ║  Utiliza JUnit 5 y Mockito para pruebas puramente aisladas.           ║
 * ║                                                                       ║
 * ║               ARQUITECTURA DE SIMULACIÓN DE PRUEBAS:                  ║
 * ║                                                                       ║
 * ║  [ Test Request ] ────────► [ MockMvc (Standalone Setup) ]            ║
 * ║                                      │                                 ║
 * ║                                      ▼                                 ║
 * ║                        [ GlobalExceptionHandler ] ◄──── (En de error)  ║
 * ║                                      │                                 ║
 * ║                                      ▼                                 ║
 * ║                             [ MarcaController ]                        ║
 * ║                                      │                                 ║
 * ║                                (Simulación)                            ║
 * ║                                      ▼                                 ║
 * ║                              [ MarcaService ] (Mock)                   ║
 * ╚═══════════════════════════════════════════════════════════════════════╝
 */
@ExtendWith(MockitoExtension.class)
class MarcaControllerTest {

    // Herramienta de simulación de peticiones HTTP (GET, POST, PUT, DELETE, etc.)
    private MockMvc mockMvc;

    // @Mock crea un simulador de comportamiento para la capa de servicios.
    // Evita cualquier interacción real con la base de datos.
    @Mock
    private MarcaService marcaService;

    // @InjectMocks inyecta el mock de MarcaService dentro de este controlador.
    @InjectMocks
    private MarcaController marcaController;

    // Utilidad para serializar y deserializar DTOs a JSON y viceversa.
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Objetos base que servirán de respuesta predeterminada para las simulaciones.
    private MarcaResponseDTO marcaBosch;
    private MarcaResponseDTO marcaStanley;

    @BeforeEach
    void setUp() {
        // Inicializa el contexto standalone del controlador registrando el Exception Handler Global
        mockMvc = MockMvcBuilders.standaloneSetup(marcaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Construcción de los objetos DTO base usando el patrón Builder de Lombok
        marcaBosch = MarcaResponseDTO.builder()
                .idMarca(1)
                .nombreMarca("Bosch")
                .build();

        marcaStanley = MarcaResponseDTO.builder()
                .idMarca(2)
                .nombreMarca("Stanley")
                .build();
    }

    /**
     * ┌────────────────────────────────────────────────────────┐
     * │ 1. ORGANIZAR (Arrange) - Preparar datos y mockings     │
     * ├────────────────────────────────────────────────────────┤
     * │ 2. ACTUAR (Act)        - Ejecutar petición con MockMvc │
     * ├────────────────────────────────────────────────────────┤
     * │ 3. VERIFICAR (Assert)  - Validar estatus, JSON y mocks │
     * └────────────────────────────────────────────────────────┘
     */
    @Test
    @DisplayName("Debería listar todas las marcas")
    void deberiaListarTodasLasMarcas() throws Exception {
        // [ARRANGE]
        // Declaramos que cuando se llame al Service, éste retorne los datos base (sin tocar base de datos)
        when(marcaService.listarTodas()).thenReturn(List.of(marcaBosch, marcaStanley));

        // [ACT & ASSERT]
        mockMvc.perform(get("/api/marcas"))
                .andExpect(status().isOk()) // Espera HTTP Status 200 OK
                /*
                 * Estructura esperada de la respuesta:
                 * $ (Raíz del JSON)
                 * └── content (ArrayList)
                 *         ├── [0] (Primer elemento)
                 *         │    ├── idMarca: 1
                 *         │    └── nombreMarca: "Bosch"
                 *         └── [1] (Segundo elemento)
                 *                 ├── idMarca: 2
                 *                 └── nombreMarca: "Stanley"
                 */
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].idMarca", is(1)))
                .andExpect(jsonPath("$.content[0].nombreMarca", is("Bosch")))
                .andExpect(jsonPath("$.content[1].idMarca", is(2)))
                .andExpect(jsonPath("$.content[1].nombreMarca", is("Stanley")));

        // Verifica que el servicio de marcas fue invocado exactamente una vez
        verify(marcaService, times(1)).listarTodas();
    }

    @Test
    @DisplayName("Debería obtener marca por ID")
    void deberiaObtenerMarcaPorId() throws Exception {
        // [ARRANGE]
        // Cuando se solicite la marca 1, devolvemos la simulación de Bosch
        when(marcaService.obtenerPorId(1)).thenReturn(marcaBosch);

        // [ACT & ASSERT]
        mockMvc.perform(get("/api/marcas/1"))
                .andExpect(status().isOk())
                /*
                 * Estructura esperada de la respuesta (EntityModel simple):
                 * $ (Raíz del JSON)
                 * ├── idMarca: 1
                 * └── nombreMarca: "Bosch"
                 */
                .andExpect(jsonPath("$.idMarca", is(1)))
                .andExpect(jsonPath("$.nombreMarca", is("Bosch")));

        // Verifica la correcta invocación del método en el service
        verify(marcaService, times(1)).obtenerPorId(1);
    }

    @Test
    @DisplayName("Debería crear una marca")
    void deberiaCrearMarca() throws Exception {
        // [ARRANGE]
        MarcaRequestDTO request = MarcaRequestDTO.builder()
                .nombreMarca("Bosch")
                .build();

        // Cuando el controller llame al service para crear cualquier objeto de tipo MarcaRequestDTO,
        // responderemos con el objeto marcaBosch (simulando que la base de datos le asignó ID: 1)
        when(marcaService.crear(any(MarcaRequestDTO.class))).thenReturn(marcaBosch);

        // [ACT & ASSERT]
        /*
         * FLUJO DE CREACIÓN DE MARCA:
         *
         * [MarcaRequestDTO] ────► (Jackson) ────► JSON {"nombreMarca": "Bosch"}
         *                                               │
         *                                               ▼
         *                                    [POST /api/marcas]
         *                                               │
         *                                               ▼
         * [MarcaResponseDTO] ◄── (ResponseEntity) ◄─ [MarcaController]
         * {"idMarca": 1, "nombreMarca": "Bosch"}
         */
        mockMvc.perform(post("/api/marcas")
                        .contentType(MediaType.APPLICATION_JSON) // Especifica el Header de tipo de contenido
                        .content(objectMapper.writeValueAsString(request))) // Serializa DTO a String JSON
                .andExpect(status().isCreated()) // Espera HTTP Status 201 Created y avanza
                .andExpect(jsonPath("$.idMarca", is(1)))
                .andExpect(jsonPath("$.nombreMarca", is("Bosch")));

        verify(marcaService, times(1)).crear(any(MarcaRequestDTO.class));
    }

    @Test
    @DisplayName("Debería actualizar una marca")
    void deberiaActualizarMarca() throws Exception {
        // [ARRANGE]
        MarcaRequestDTO request = MarcaRequestDTO.builder()
                .nombreMarca("Bosch Professional")
                .build();

        MarcaResponseDTO actualizada = MarcaResponseDTO.builder()
                .idMarca(1)
                .nombreMarca("Bosch Professional")
                .build();

        when(marcaService.actualizar(eq(1), any(MarcaRequestDTO.class))).thenReturn(actualizada);

        // [ACT & ASSERT]
        /*
         * FLUJO DE ACTUALIZACIÓN DE MARCA:
         *
         * Petición PUT a /api/marcas/1
         * Envia JSON: {"nombreMarca": "Bosch Professional"}
         *
         * Retorno esperado:
         * $ (Raíz del JSON)
         * ├── idMarca: 1
         * └── nombreMarca: "Bosch Professional" (Modificado)
         */
        mockMvc.perform(put("/api/marcas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Espera HTTP Status 200 OK
                .andExpect(jsonPath("$.idMarca", is(1)))
                .andExpect(jsonPath("$.nombreMarca", is("Bosch Professional")));

        verify(marcaService, times(1)).actualizar(eq(1), any(MarcaRequestDTO.class));
    }

    @Test
    @DisplayName("Debería eliminar una marca")
    void deberiaEliminarMarca() throws Exception {
        // [ARRANGE]
        // Para métodos void, indicamos que Mockito no haga nada (doNothing)
        doNothing().when(marcaService).eliminar(1);

        // [ACT & ASSERT]
        /*
         * FLUJO DE ELIMINACIÓN DE MARCA:
         *
         * Petición DELETE a /api/marcas/1 ──► [MarcaController] ──► [MarcaService.eliminar(1)]
         *                                                                     │
         *                                                                     ▼
         *                                                       (No retorna nada / doNothing)
         *                                                                     │
         *                                                                     ▼
         *                                                            Status: 204 NO CONTENT
         */
        mockMvc.perform(delete("/api/marcas/1"))
                .andExpect(status().isNoContent()); // Espera HTTP Status 204 No Content

        verify(marcaService, times(1)).eliminar(1);
    }
}
