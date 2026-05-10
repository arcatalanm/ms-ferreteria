package ferrefix.ms_direcciones.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ferrefix.ms_direcciones.dto.DireccionRequestDTO;
import ferrefix.ms_direcciones.dto.DireccionResponseDTO;
import ferrefix.ms_direcciones.service.DireccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/direcciones")
@RequiredArgsConstructor
public class DireccionController {

    private static final Logger logger = LoggerFactory.getLogger(DireccionController.class);
    private final DireccionService direccionService;

    @PostMapping
    public ResponseEntity<DireccionResponseDTO> crearDireccion(@Valid @RequestBody DireccionRequestDTO dto) {
        logger.info("POST /api/direcciones - Solicitud recibida para crear dirección");
        DireccionResponseDTO response = direccionService.crearDireccion(dto);
        logger.info("POST /api/direcciones - Dirección creada. Respondiendo 201 CREATED");
        return ResponseEntity.created(URI.create("/api/direcciones/" + response.getIdDireccion()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<DireccionResponseDTO>> listarDirecciones() {
        logger.info("GET /api/direcciones - Solicitud recibida para listar todas las direcciones");
        List<DireccionResponseDTO> direcciones = direccionService.buscarTodas();
        logger.info("GET /api/direcciones - Respondiendo 200 OK con {} registros", direcciones.size());
        return ResponseEntity.ok(direcciones);
    }

    @GetMapping("/{idDireccion}")
    public ResponseEntity<DireccionResponseDTO> obtenerDireccionPorId(@PathVariable Long idDireccion) {
        logger.info("GET /api/direcciones/{} - Solicitud recibida para buscar dirección", idDireccion);
        DireccionResponseDTO response = direccionService.buscarPorId(idDireccion);
        logger.info("GET /api/direcciones/{} - Dirección encontrada. Respondiendo 200 OK", idDireccion);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{idDireccion}")
    public ResponseEntity<DireccionResponseDTO> actualizarDireccion(@PathVariable Long idDireccion, 
                                                                    @Valid @RequestBody DireccionRequestDTO dto) {
        logger.info("PUT /api/direcciones/{} - Solicitud recibida para actualizar dirección", idDireccion);
        DireccionResponseDTO response = direccionService.actualizarDireccion(idDireccion, dto);
        logger.info("PUT /api/direcciones/{} - Dirección actualizada. Respondiendo 200 OK", idDireccion);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{idDireccion}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long idDireccion) {
        logger.info("DELETE /api/direcciones/{} - Solicitud recibida para eliminar dirección", idDireccion);
        direccionService.eliminarDireccion(idDireccion);
        logger.info("DELETE /api/direcciones/{} - Dirección eliminada. Respondiendo 204 NO CONTENT", idDireccion);
        return ResponseEntity.noContent().build();
    }
}