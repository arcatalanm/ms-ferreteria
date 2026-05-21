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

import ferrefix.ms_inventario.dto.CategoriaProductoRequestDTO;
import ferrefix.ms_inventario.dto.CategoriaProductoResponseDTO;
import ferrefix.ms_inventario.exception.ApiSuccessResponse;
import ferrefix.ms_inventario.service.CategoriaProductoService;

@RestController
@RequestMapping("/api/inventario/categorias")
@RequiredArgsConstructor
public class CategoriaProductoController {

    private static final Logger logger = LoggerFactory.getLogger(CategoriaProductoController.class);
    private final CategoriaProductoService categoriaProductoService;

    @PostMapping
    public ResponseEntity<CategoriaProductoResponseDTO> crearCategoriaProducto(
            @Valid @RequestBody CategoriaProductoRequestDTO dto) {

        logger.info("POST /api/inventario/categorias - Nombre: '{}'", dto.getNombreCategoria());
        CategoriaProductoResponseDTO creada = categoriaProductoService.crearCategoriaProducto(dto);
        logger.info("POST /api/inventario/categorias - Categoría creada ID: {}. Respondiendo 201 CREATED", creada.getIdCategoria());
        return ResponseEntity
                .created(URI.create("/api/inventario/categorias/" + creada.getIdCategoria()))
                .body(creada);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaProductoResponseDTO>> buscarTodasCategorias() {
        logger.info("GET /api/inventario/categorias - Listando todas las categorías");
        List<CategoriaProductoResponseDTO> lista = categoriaProductoService.buscarTodasCategorias();
        logger.info("GET /api/inventario/categorias - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProductoResponseDTO> buscarCategoriaPorId(@PathVariable Integer id) {
        logger.info("GET /api/inventario/categorias/{} - Buscando categoría", id);
        CategoriaProductoResponseDTO categoria = categoriaProductoService.buscarCategoriaPorId(id);
        logger.info("GET /api/inventario/categorias/{} - Encontrada. Respondiendo 200 OK", id);
        return ResponseEntity.ok(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProductoResponseDTO> actualizarCategoriaProducto(
            @PathVariable Integer id,
            @Valid @RequestBody CategoriaProductoRequestDTO dto) {

        logger.info("PUT /api/inventario/categorias/{} - Actualizando categoría", id);
        CategoriaProductoResponseDTO actualizada = categoriaProductoService.actualizarCategoriaProducto(id, dto);
        logger.info("PUT /api/inventario/categorias/{} - Actualizada. Respondiendo 200 OK", id);
        return ResponseEntity.ok(actualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse> eliminarCategoriaProducto(
            @PathVariable Integer id,
            HttpServletRequest request) {

        logger.info("DELETE /api/inventario/categorias/{} - Solicitud de eliminación", id);
        categoriaProductoService.eliminarCategoriaProducto(id);

        ApiSuccessResponse respuesta = ApiSuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("La categoría con ID " + id + " fue eliminada correctamente.")
                .path(request.getRequestURI())
                .build();

        logger.info("DELETE /api/inventario/categorias/{} - Eliminada. Respondiendo 200 OK", id);
        return ResponseEntity.ok(respuesta);
    }
}
