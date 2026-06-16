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

import ferrefix.ms_inventario.dto.CategoriaProductoRequestDTO;
import ferrefix.ms_inventario.dto.CategoriaProductoResponseDTO;
import ferrefix.ms_inventario.service.CategoriaProductoService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/inventario/categorias")
@RequiredArgsConstructor
public class CategoriaProductoController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaProductoController.class);
    private final CategoriaProductoService categoriaProductoService;

    @PostMapping
    public ResponseEntity<EntityModel<CategoriaProductoResponseDTO>> crearCategoriaProducto(
            @Valid @RequestBody CategoriaProductoRequestDTO dto) {

        logger.info("POST /api/inventario/categorias - Nombre: '{}'", dto.getNombreCategoria());
        CategoriaProductoResponseDTO creada = categoriaProductoService.crearCategoriaProducto(dto);

        EntityModel<CategoriaProductoResponseDTO> model = EntityModel.of(creada,
                linkTo(methodOn(CategoriaProductoController.class).buscarCategoriaPorId(creada.getIdCategoria())).withSelfRel(),
                linkTo(methodOn(CategoriaProductoController.class).buscarTodasCategorias()).withRel("categorias")
        );

        logger.info("POST /api/inventario/categorias - Categoría creada ID: {}. Respondiendo 201 CREATED", creada.getIdCategoria());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CategoriaProductoResponseDTO>>> buscarTodasCategorias() {
        logger.info("GET /api/inventario/categorias - Listando todas las categorías");
        List<CategoriaProductoResponseDTO> lista = categoriaProductoService.buscarTodasCategorias();

        List<EntityModel<CategoriaProductoResponseDTO>> models = lista.stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(CategoriaProductoController.class).buscarCategoriaPorId(c.getIdCategoria())).withSelfRel(),
                        linkTo(methodOn(CategoriaProductoController.class).buscarTodasCategorias()).withRel("categorias")
                ))
                .toList();

        CollectionModel<EntityModel<CategoriaProductoResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(CategoriaProductoController.class).buscarTodasCategorias()).withSelfRel()
        );

        logger.info("GET /api/inventario/categorias - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CategoriaProductoResponseDTO>> buscarCategoriaPorId(@PathVariable Integer id) {
        logger.info("GET /api/inventario/categorias/{} - Buscando categoría", id);
        CategoriaProductoResponseDTO categoria = categoriaProductoService.buscarCategoriaPorId(id);

        EntityModel<CategoriaProductoResponseDTO> model = EntityModel.of(categoria,
                linkTo(methodOn(CategoriaProductoController.class).buscarCategoriaPorId(id)).withSelfRel(),
                linkTo(methodOn(CategoriaProductoController.class).buscarTodasCategorias()).withRel("categorias"),
                linkTo(methodOn(CategoriaProductoController.class).actualizarCategoriaProducto(id, null)).withRel("actualizar"),
                linkTo(methodOn(CategoriaProductoController.class).eliminarCategoriaProducto(id, null)).withRel("eliminar")
        );

        logger.info("GET /api/inventario/categorias/{} - Encontrada. Respondiendo 200 OK", id);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<CategoriaProductoResponseDTO>> actualizarCategoriaProducto(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaProductoRequestDTO dto) {

        logger.info("PUT /api/inventario/categorias/{} - Actualizando categoría", id);
        CategoriaProductoResponseDTO actualizada = categoriaProductoService.actualizarCategoriaProducto(id, dto);

        EntityModel<CategoriaProductoResponseDTO> model = EntityModel.of(actualizada,
                linkTo(methodOn(CategoriaProductoController.class).buscarCategoriaPorId(id)).withSelfRel(),
                linkTo(methodOn(CategoriaProductoController.class).buscarTodasCategorias()).withRel("categorias")
        );

        logger.info("PUT /api/inventario/categorias/{} - Actualizada. Respondiendo 200 OK", id);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoriaProducto(
            @PathVariable Integer id,
            HttpServletRequest request) {

        logger.info("DELETE /api/inventario/categorias/{} - Solicitud de eliminación", id);
        categoriaProductoService.eliminarCategoriaProducto(id);

        logger.info("DELETE /api/inventario/categorias/{} - Eliminada. Respondiendo 204 NO CONTENT", id);
        return ResponseEntity.noContent().build();
    }
}
