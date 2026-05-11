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

import ferrefix.ms_inventario.dto.ProductoRequestDTO;
import ferrefix.ms_inventario.dto.ProductoResponseDTO;
import ferrefix.ms_inventario.model.Producto;
import ferrefix.ms_inventario.service.ProductoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    
    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    @PostMapping
    public ResponseEntity<ProductoResponseDTO> crearProducto(@RequestBody ProductoRequestDTO dto) {
        logger.info("POST /api/productos - Solicitud para crear producto: '{}' con código de barras: {}", 
                    dto.getNombre(), dto.getCodigoBarras());
        
        Producto productoCreado = productoService.crearProducto(dto);
        ProductoResponseDTO response = ProductoResponseDTO.builder()
                .id(productoCreado.getIdProducto().intValue())
                .nombre(productoCreado.getNombreProducto())
                .codigoBarras(productoCreado.getCodigoBarrasProducto())
                .stock(productoCreado.getStockProducto())
                .precioVenta(productoCreado.getPrecioVentaProducto())
                .unidadMedida(productoCreado.getUnidadMedida().getIdUnidadMedida())
                .categoria(productoCreado.getCategoriaProducto().getNombreCategoria())
                .build();
        
        logger.info("Producto creado exitosamente. ID: {}, Nombre: '{}', Código de barras: {}", 
                    productoCreado.getIdProducto(), productoCreado.getNombreProducto(), 
                    productoCreado.getCodigoBarrasProducto());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> buscarTodosProductos() {
        logger.info("GET /api/productos - Solicitud para obtener listado completo de productos");
        
        List<ProductoResponseDTO> productos = productoService.buscarTodosProductos();
        
        logger.info("Listado de productos obtenido exitosamente. Total de registros: {}", productos.size());
        
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarProductoPorId(@PathVariable Long id) {
        logger.info("GET /api/productos/{} - Solicitud para obtener producto por ID", id);
        
        ProductoResponseDTO producto = productoService.buscarProductoPorId(id);
        
        logger.info("Producto obtenido exitosamente. ID: {}, Nombre: '{}', Código de barras: {}", 
                    producto.getId(), producto.getNombre(), producto.getCodigoBarras());
        
        return ResponseEntity.ok(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> actualizarProducto(@PathVariable Long id, @RequestBody ProductoRequestDTO dto) {
        logger.info("PUT /api/productos/{} - Solicitud para actualizar producto. Nuevo nombre: '{}', Código de barras: {}", 
                    id, dto.getNombre(), dto.getCodigoBarras());
        
        Producto productoActualizado = productoService.actualizarProducto(id, dto);
        ProductoResponseDTO response = ProductoResponseDTO.builder()
                .id(productoActualizado.getIdProducto().intValue())
                .nombre(productoActualizado.getNombreProducto())
                .codigoBarras(productoActualizado.getCodigoBarrasProducto())
                .stock(productoActualizado.getStockProducto())
                .precioVenta(productoActualizado.getPrecioVentaProducto())
                .unidadMedida(productoActualizado.getUnidadMedida().getIdUnidadMedida())
                .categoria(productoActualizado.getCategoriaProducto().getNombreCategoria())
                .build();
        
        logger.info("Producto actualizado exitosamente. ID: {}, Nuevo nombre: '{}', Código de barras: {}", 
                    productoActualizado.getIdProducto(), productoActualizado.getNombreProducto(), 
                    productoActualizado.getCodigoBarrasProducto());
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        logger.info("DELETE /api/productos/{} - Solicitud para eliminar producto", id);
        
        productoService.eliminarProducto(id);
        
        logger.info("Producto eliminado exitosamente. ID: {}", id);
        
        return ResponseEntity.noContent().build();
    }
}
