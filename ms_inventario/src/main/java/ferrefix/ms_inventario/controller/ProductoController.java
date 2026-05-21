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

import ferrefix.ms_inventario.dto.ProductoRequestDTO;
import ferrefix.ms_inventario.dto.ProductoResponseDTO;
import ferrefix.ms_inventario.exception.ApiSuccessResponse;
import ferrefix.ms_inventario.service.ProductoService;

@RestController
@RequestMapping("/api/inventario/productos")
@RequiredArgsConstructor
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);
    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(@Valid @RequestBody ProductoRequestDTO dto) {

        logger.info("POST /api/inventario/productos - Nombre: '{}' | código: {}", dto.getNombre(), dto.getCodigoBarras());
        ProductoResponseDTO creado = productoService.crearProducto(dto);
        logger.info("POST /api/inventario/productos - Producto creado ID: {}. Respondiendo 201 CREATED", creado.getId());
        return ResponseEntity
                .created(URI.create("/api/inventario/productos/" + creado.getId()))
                .body(creado);
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> buscarTodosProductos() {
        logger.info("GET /api/inventario/productos - Listando todos los productos");
        List<ProductoResponseDTO> lista = productoService.buscarTodosProductos();
        logger.info("GET /api/inventario/productos - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarProductoPorId(@PathVariable Long id) {
        logger.info("GET /api/inventario/productos/{} - Buscando producto", id);
        ProductoResponseDTO producto = productoService.buscarProductoPorId(id);
        logger.info("GET /api/inventario/productos/{} - Encontrado. Respondiendo 200 OK", id);
        return ResponseEntity.ok(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestDTO dto) {

        logger.info("PUT /api/inventario/productos/{} - Actualizando producto", id);
        ProductoResponseDTO actualizado = productoService.actualizarProducto(id, dto);
        logger.info("PUT /api/inventario/productos/{} - Actualizado. Respondiendo 200 OK", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse> eliminarProducto(
            @PathVariable Long id,
            HttpServletRequest request) {

        logger.info("DELETE /api/inventario/productos/{} - Solicitud de eliminación", id);
        productoService.eliminarProducto(id);

        ApiSuccessResponse respuesta = ApiSuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("El producto con ID " + id + " fue eliminado correctamente.")
                .path(request.getRequestURI())
                .build();

        logger.info("DELETE /api/inventario/productos/{} - Eliminado. Respondiendo 200 OK", id);
        return ResponseEntity.ok(respuesta);
    }


    @PatchMapping("/{id}/descontar-stock")
    public ResponseEntity<Void> descontarStock(@PathVariable Long id, @RequestParam Integer cantidad) {
        logger.info("PATCH /api/inventario/productos/{}/descontar-stock - Cantidad: {}", id, cantidad);
        productoService.descontarStock(id, cantidad);
        logger.info("PATCH /api/inventario/productos/{} - Stock descontado exitosamente", id);
        return ResponseEntity.ok().build();
    }
}