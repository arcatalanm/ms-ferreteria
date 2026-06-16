package ferrefix.ms_direcciones.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ferrefix.ms_direcciones.dto.DireccionRequestDTO;
import ferrefix.ms_direcciones.dto.DireccionResponseDTO;
import ferrefix.ms_direcciones.service.DireccionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/direcciones")
@RequiredArgsConstructor
public class DireccionController {

    private static final Logger logger = LoggerFactory.getLogger(DireccionController.class);
    private final DireccionService direccionService;

    @PostMapping
    public ResponseEntity<EntityModel<DireccionResponseDTO>> crearDireccion(@Valid @RequestBody DireccionRequestDTO dto) {
        logger.info("POST /api/direcciones - Solicitud recibida para crear dirección");
        DireccionResponseDTO response = direccionService.crearDireccion(dto);

        EntityModel<DireccionResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(DireccionController.class).obtenerDireccionPorId(response.getIdDireccion())).withSelfRel(),
                linkTo(methodOn(DireccionController.class).listarDirecciones()).withRel("direcciones")
        );

        logger.info("POST /api/direcciones - Dirección creada. Respondiendo 201 CREATED");
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<DireccionResponseDTO>>> listarDirecciones() {
        logger.info("GET /api/direcciones - Solicitud recibida para listar todas las direcciones");
        List<DireccionResponseDTO> direcciones = direccionService.buscarTodas();

        List<EntityModel<DireccionResponseDTO>> models = direcciones.stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(DireccionController.class).obtenerDireccionPorId(d.getIdDireccion())).withSelfRel()
                ))
                .toList();

        CollectionModel<EntityModel<DireccionResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(DireccionController.class).listarDirecciones()).withSelfRel()
        );

        logger.info("GET /api/direcciones - Respondiendo 200 OK with {} registros", direcciones.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{idDireccion}")
    public ResponseEntity<EntityModel<DireccionResponseDTO>> obtenerDireccionPorId(@PathVariable Long idDireccion) {
        logger.info("GET /api/direcciones/{} - Solicitud recibida para buscar dirección", idDireccion);
        DireccionResponseDTO response = direccionService.buscarPorId(idDireccion);

        EntityModel<DireccionResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(DireccionController.class).obtenerDireccionPorId(idDireccion)).withSelfRel(),
                linkTo(methodOn(DireccionController.class).listarDirecciones()).withRel("direcciones"),
                linkTo(methodOn(DireccionController.class).actualizarDireccion(idDireccion, null)).withRel("actualizar"),
                linkTo(methodOn(DireccionController.class).eliminarDireccion(idDireccion)).withRel("eliminar")
        );

        logger.info("GET /api/direcciones/{} - Dirección encontrada. Respondiendo 200 OK", idDireccion);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{idDireccion}")
    public ResponseEntity<EntityModel<DireccionResponseDTO>> actualizarDireccion(@PathVariable Long idDireccion, 
                                                                    @Valid @RequestBody DireccionRequestDTO dto) {
        logger.info("PUT /api/direcciones/{} - Solicitud recibida para actualizar dirección", idDireccion);
        DireccionResponseDTO response = direccionService.actualizarDireccion(idDireccion, dto);

        EntityModel<DireccionResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(DireccionController.class).obtenerDireccionPorId(idDireccion)).withSelfRel(),
                linkTo(methodOn(DireccionController.class).listarDirecciones()).withRel("direcciones")
        );

        logger.info("PUT /api/direcciones/{} - Dirección actualizada. Respondiendo 200 OK", idDireccion);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{idDireccion}")
    public ResponseEntity<Void> eliminarDireccion(@PathVariable Long idDireccion) {
        logger.info("DELETE /api/direcciones/{} - Solicitud recibida para eliminar dirección", idDireccion);
        direccionService.eliminarDireccion(idDireccion);
        logger.info("DELETE /api/direcciones/{} - Dirección eliminada exitosamente. Respondiendo 204 NO CONTENT", idDireccion);
        return ResponseEntity.noContent().build();
    }
}