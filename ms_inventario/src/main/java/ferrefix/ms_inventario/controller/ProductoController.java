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

import ferrefix.ms_inventario.assembler.ProductoAssembler;
import ferrefix.ms_inventario.dto.ProductoRequestDTO;
import ferrefix.ms_inventario.dto.ProductoResponseDTO;
import ferrefix.ms_inventario.service.ProductoService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/inventario/productos")
@RequiredArgsConstructor
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoService productoService;
    private final ProductoAssembler productoAssembler;

    @PostMapping
    public ResponseEntity<EntityModel<ProductoResponseDTO>> crearProducto(@Valid @RequestBody ProductoRequestDTO dto) {
        logger.info("POST /api/inventario/productos - Nombre: '{}' | código: {}", dto.getNombre(), dto.getCodigoBarras());
        ProductoResponseDTO creado = productoService.crearProducto(dto);
        EntityModel<ProductoResponseDTO> model = productoAssembler.toModel(creado);

        logger.info("POST /api/inventario/productos - Producto creado ID: {}. Respondiendo 201 CREATED", creado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<ProductoResponseDTO>>> buscarTodosProductos() {
        logger.info("GET /api/inventario/productos - Listando todos los productos");
        List<ProductoResponseDTO> lista = productoService.buscarTodosProductos();
        CollectionModel<EntityModel<ProductoResponseDTO>> collection = productoAssembler.toCollectionModel(lista)
                .add(linkTo(methodOn(ProductoController.class).buscarTodosProductos()).withSelfRel());

        logger.info("GET /api/inventario/productos - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> buscarProductoPorId(@PathVariable Long id) {
        logger.info("GET /api/inventario/productos/{} - Buscando producto", id);
        ProductoResponseDTO producto = productoService.buscarProductoPorId(id);
        EntityModel<ProductoResponseDTO> model = productoAssembler.toModel(producto);

        logger.info("GET /api/inventario/productos/{} - Encontrado. Respondiendo 200 OK", id);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ProductoResponseDTO>> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {
        logger.info("PUT /api/inventario/productos/{} - Actualizando producto", id);
        ProductoResponseDTO actualizado = productoService.actualizarProducto(id, dto);
        EntityModel<ProductoResponseDTO> model = productoAssembler.toModel(actualizado);

        logger.info("PUT /api/inventario/productos/{} - Actualizado. Respondiendo 200 OK", id);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @PathVariable Long id,
            HttpServletRequest request) {

        logger.info("DELETE /api/inventario/productos/{} - Solicitud de eliminación", id);
        productoService.eliminarProducto(id);

        logger.info("DELETE /api/inventario/productos/{} - Eliminado. Respondiendo 204 NO CONTENT", id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/descontar-stock")
    public ResponseEntity<Void> descontarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        logger.info("PATCH /api/inventario/productos/{}/descontar-stock - Cantidad: {}", id, cantidad);
        productoService.descontarStock(id, cantidad);
        logger.info("PATCH /api/inventario/productos/{} - Stock descontado exitosamente", id);
        return ResponseEntity.ok().build();
    }
}
