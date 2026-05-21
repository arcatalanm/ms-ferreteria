package ferrefix.ms_inventario.controller;


import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ferrefix.ms_inventario.dto.UnidadMedidaRequestDTO;
import ferrefix.ms_inventario.dto.UnidadMedidaResponseDTO;
import ferrefix.ms_inventario.exception.ApiSuccessResponse;
import ferrefix.ms_inventario.service.UnidadMedidaService;

@RestController
@RequestMapping("/api/inventario/unidades_medida")
@RequiredArgsConstructor
public class UnidadMedidaController {

    private static final Logger logger = LoggerFactory.getLogger(UnidadMedidaController.class);
    private final UnidadMedidaService unidadMedidaService;

    @PostMapping
    public ResponseEntity<UnidadMedidaResponseDTO> crearUnidadMedida(
            @Valid @RequestBody UnidadMedidaRequestDTO dto) {

        logger.info("POST /api/inventario/unidades_medida - Nombre: '{}'", dto.getNombreUnidadMedida());
        UnidadMedidaResponseDTO creada = unidadMedidaService.crearUnidadMedida(dto);
        logger.info("POST /api/inventario/unidades_medida - Unidad creada ID: {}. Respondiendo 201 CREATED", creada.getIdUnidadMedida());
        return ResponseEntity
                .created(URI.create("/api/inventario/unidades_medida/" + creada.getIdUnidadMedida()))
                .body(creada);
    }

    @GetMapping
    public ResponseEntity<List<UnidadMedidaResponseDTO>> buscarTodasUnidadesMedida() {
        logger.info("GET /api/inventario/unidades_medida - Listando todas las unidades");
        List<UnidadMedidaResponseDTO> lista = unidadMedidaService.buscarTodasUnidadesMedida();
        logger.info("GET /api/inventario/unidades_medida - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UnidadMedidaResponseDTO> buscarUnidadMedidaPorId(@PathVariable Integer id) {
        logger.info("GET /api/inventario/unidades_medida/{} - Buscando unidad", id);
        UnidadMedidaResponseDTO unidad = unidadMedidaService.buscarUnidadMedidaPorId(id);
        logger.info("GET /api/inventario/unidades_medida/{} - Encontrada. Respondiendo 200 OK", id);
        return ResponseEntity.ok(unidad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadMedidaResponseDTO> actualizarUnidadMedida(
            @PathVariable Integer id,
            @Valid @RequestBody UnidadMedidaRequestDTO dto) {

        logger.info("PUT /api/inventario/unidades_medida/{} - Actualizando unidad", id);
        UnidadMedidaResponseDTO actualizada = unidadMedidaService.actualizarUnidadMedida(id, dto);
        logger.info("PUT /api/inventario/unidades_medida/{} - Actualizada. Respondiendo 200 OK", id);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse> eliminarUnidadMedida(
            @PathVariable Integer id,
            HttpServletRequest request) {

        logger.info("DELETE /api/inventario/unidades_medida/{} - Solicitud de eliminación", id);
        unidadMedidaService.eliminarUnidadMedida(id);

        ApiSuccessResponse respuesta = ApiSuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("La unidad de medida con ID " + id + " fue eliminada correctamente.")
                .path(request.getRequestURI())
                .build();

        logger.info("DELETE /api/inventario/unidades_medida/{} - Eliminada. Respondiendo 200 OK", id);
        return ResponseEntity.ok(respuesta);
    }
}