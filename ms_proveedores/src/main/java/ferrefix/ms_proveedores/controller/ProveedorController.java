package ferrefix.ms_proveedores.controller;

import ferrefix.ms_proveedores.assembler.ProveedorAssembler;
import ferrefix.ms_proveedores.dto.ProveedorRequestDTO;
import ferrefix.ms_proveedores.dto.ProveedorResponseDTO;
import ferrefix.ms_proveedores.service.ProveedorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);
    private final ProveedorService proveedorService;
    private final ProveedorAssembler proveedorAssembler;

    @PostMapping
    public ResponseEntity<EntityModel<ProveedorResponseDTO>> crearProveedor(
            @Valid @RequestBody ProveedorRequestDTO dto, HttpServletRequest request) {

        logger.info("POST /api/proveedores - RUT: {}", dto.getRutProveedor());
        ProveedorResponseDTO creado = proveedorService.guardar(dto);
        logger.info("POST /api/proveedores - Proveedor creado. ID: {}. Respondiendo 201 CREATED",
                creado.getIdProveedor());

        EntityModel<ProveedorResponseDTO> model = proveedorAssembler.toModel(creado);
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProveedorResponseDTO>>> listarProveedores(HttpServletRequest request) {
        logger.info("GET /api/proveedores - Listando todos los proveedores");
        List<ProveedorResponseDTO> lista = proveedorService.listarTodos();
        CollectionModel<EntityModel<ProveedorResponseDTO>> collection = proveedorAssembler.toCollectionModel(lista)
                .add(linkTo(methodOn(ProveedorController.class).listarProveedores(null)).withSelfRel());

        logger.info("GET /api/proveedores - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProveedorResponseDTO>> obtenerProveedor(@PathVariable Integer id, HttpServletRequest request) {
        logger.info("GET /api/proveedores/{} - Buscando proveedor", id);
        ProveedorResponseDTO response = proveedorService.buscarPorId(id);
        EntityModel<ProveedorResponseDTO> model = proveedorAssembler.toModel(response);

        logger.info("GET /api/proveedores/{} - Encontrado. Respondiendo 200 OK", id);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProveedorResponseDTO>> actualizarProveedor(
            @PathVariable Integer id,
            @Valid @RequestBody ProveedorRequestDTO dto, HttpServletRequest request) {

        logger.info("PUT /api/proveedores/{} - Actualizando proveedor", id);
        ProveedorResponseDTO actualizado = proveedorService.actualizar(id, dto);
        EntityModel<ProveedorResponseDTO> model = proveedorAssembler.toModel(actualizado);

        logger.info("PUT /api/proveedores/{} - Actualizado. Respondiendo 200 OK", id);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(
            @PathVariable Integer id,
            HttpServletRequest request) {

        logger.info("DELETE /api/proveedores/{} - Solicitud de eliminación", id);
        proveedorService.eliminar(id);

        logger.info("DELETE /api/proveedores/{} - Eliminado. Respondiendo 204 NO CONTENT", id);
        return ResponseEntity.noContent().build();
    }
}
