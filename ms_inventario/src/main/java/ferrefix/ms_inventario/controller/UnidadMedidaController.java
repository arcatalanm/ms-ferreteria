package ferrefix.ms_inventario.controller;


import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ferrefix.ms_inventario.assembler.UnidadMedidaAssembler;
import ferrefix.ms_inventario.dto.UnidadMedidaRequestDTO;
import ferrefix.ms_inventario.dto.UnidadMedidaResponseDTO;
import ferrefix.ms_inventario.service.UnidadMedidaService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/inventario/unidades_medida")
@RequiredArgsConstructor
public class UnidadMedidaController {

    private static final Logger logger = LoggerFactory.getLogger(UnidadMedidaController.class);
    private final UnidadMedidaService unidadMedidaService;
    private final UnidadMedidaAssembler unidadMedidaAssembler;

    @PostMapping
    public ResponseEntity<EntityModel<UnidadMedidaResponseDTO>> crearUnidadMedida(
            @Valid @RequestBody UnidadMedidaRequestDTO dto) {

        logger.info("POST /api/inventario/unidades_medida - Nombre: '{}'", dto.getNombreUnidadMedida());
        UnidadMedidaResponseDTO creada = unidadMedidaService.crearUnidadMedida(dto);
        EntityModel<UnidadMedidaResponseDTO> model = unidadMedidaAssembler.toModel(creada);

        logger.info("POST /api/inventario/unidades_medida - Unidad creada ID: {}. Respondiendo 201 CREATED", creada.getIdUnidadMedida());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UnidadMedidaResponseDTO>>> buscarTodasUnidadesMedida() {
        logger.info("GET /api/inventario/unidades_medida - Listando todas las unidades");
        List<UnidadMedidaResponseDTO> lista = unidadMedidaService.buscarTodasUnidadesMedida();
        CollectionModel<EntityModel<UnidadMedidaResponseDTO>> collection = unidadMedidaAssembler.toCollectionModel(lista)
                .add(linkTo(methodOn(UnidadMedidaController.class).buscarTodasUnidadesMedida()).withSelfRel());

        logger.info("GET /api/inventario/unidades_medida - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UnidadMedidaResponseDTO>> buscarUnidadMedidaPorId(@PathVariable Integer id) {
        logger.info("GET /api/inventario/unidades_medida/{} - Buscando unidad", id);
        UnidadMedidaResponseDTO unidad = unidadMedidaService.buscarUnidadMedidaPorId(id);
        EntityModel<UnidadMedidaResponseDTO> model = unidadMedidaAssembler.toModel(unidad);

        logger.info("GET /api/inventario/unidades_medida/{} - Encontrada. Respondiendo 200 OK", id);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UnidadMedidaResponseDTO>> actualizarUnidadMedida(
            @PathVariable Integer id,
            @Valid @RequestBody UnidadMedidaRequestDTO dto) {

        logger.info("PUT /api/inventario/unidades_medida/{} - Actualizando unidad", id);
        UnidadMedidaResponseDTO actualizada = unidadMedidaService.actualizarUnidadMedida(id, dto);
        EntityModel<UnidadMedidaResponseDTO> model = unidadMedidaAssembler.toModel(actualizada);

        logger.info("PUT /api/inventario/unidades_medida/{} - Actualizada. Respondiendo 200 OK", id);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUnidadMedida(
            @PathVariable Integer id,
            HttpServletRequest request) {

        logger.info("DELETE /api/inventario/unidades_medida/{} - Solicitud de eliminación", id);
        unidadMedidaService.eliminarUnidadMedida(id);

        logger.info("DELETE /api/inventario/unidades_medida/{} - Eliminada. Respondiendo 204 NO CONTENT", id);
        return ResponseEntity.noContent().build();
    }
}
