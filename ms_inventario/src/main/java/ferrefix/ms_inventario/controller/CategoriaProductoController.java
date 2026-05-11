package ferrefix.ms_inventario.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ferrefix.ms_inventario.dto.CategoriaProductoRequestDTO;
import ferrefix.ms_inventario.dto.CategoriaProductoResponseDTO;
import ferrefix.ms_inventario.model.CategoriaProducto;
import ferrefix.ms_inventario.service.CategoriaProductoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaProductoController {

    private final CategoriaProductoService categoriaProductoService;
    
    private static final Logger logger = LoggerFactory.getLogger(CategoriaProductoController.class);

    @PostMapping
    public ResponseEntity<CategoriaProductoResponseDTO> crearCategoriaProducto(@RequestBody CategoriaProductoRequestDTO dto) {
        logger.info("POST /api/categorias - Solicitud para crear categoría de producto: '{}'", dto.getNombreCategoria());
        
        CategoriaProducto categoriaCreada = categoriaProductoService.crearCategoriaProducto(dto);
        CategoriaProductoResponseDTO response = CategoriaProductoResponseDTO.builder()
                .idCategoria(categoriaCreada.getIdCategoria())
                .nombreCategoria(categoriaCreada.getNombreCategoria())
                .build();
        
        logger.info("Categoría de producto creada exitosamente. ID: {}, Nombre: '{}'", 
                    categoriaCreada.getIdCategoria(), categoriaCreada.getNombreCategoria());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaProductoResponseDTO>> buscarTodasCategorias() {
        logger.info("GET /api/categorias - Solicitud para obtener listado completo de categorías de productos");
        
        List<CategoriaProductoResponseDTO> categorias = categoriaProductoService.buscarTodasCategorias();
        
        logger.info("Listado de categorías obtenido exitosamente. Total de registros: {}", categorias.size());
        
        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaProductoResponseDTO> buscarCategoriaPorId(@PathVariable Integer id) {
        logger.info("GET /api/categorias/{} - Solicitud para obtener categoría de producto por ID", id);
        
        CategoriaProductoResponseDTO categoria = categoriaProductoService.buscarCategoriaPorId(id);
        
        logger.info("Categoría de producto obtenida exitosamente. ID: {}, Nombre: '{}'", 
                    categoria.getIdCategoria(), categoria.getNombreCategoria());
        
        return ResponseEntity.ok(categoria);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoriaProductoResponseDTO> actualizarCategoriaProducto(@PathVariable Integer id, @RequestBody CategoriaProductoRequestDTO dto) {
        logger.info("PUT /api/categorias/{} - Solicitud para actualizar categoría de producto. Nuevo nombre: '{}'", 
                    id, dto.getNombreCategoria());
        
        CategoriaProducto categoriaActualizada = categoriaProductoService.actualizarCategoriaProducto(id, dto);
        CategoriaProductoResponseDTO response = CategoriaProductoResponseDTO.builder()
                .idCategoria(categoriaActualizada.getIdCategoria())
                .nombreCategoria(categoriaActualizada.getNombreCategoria())
                .build();
        
        logger.info("Categoría de producto actualizada exitosamente. ID: {}, Nuevo nombre: '{}'", 
                    categoriaActualizada.getIdCategoria(), categoriaActualizada.getNombreCategoria());
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoriaProducto(@PathVariable Integer id) {
        logger.info("DELETE /api/categorias/{} - Solicitud para eliminar categoría de producto", id);
        
        categoriaProductoService.eliminarCategoriaProducto(id);
        
        logger.info("Categoría de producto eliminada exitosamente. ID: {}", id);
        
        return ResponseEntity.noContent().build();
    }
}
