package ferrefix.ms_inventorario.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ferrefix.ms_inventorario.dto.ProductoRequestDTO;
import ferrefix.ms_inventorario.dto.ProductoResponseDTO;
import ferrefix.ms_inventorario.model.Producto;
import ferrefix.ms_inventorario.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<Producto> crearProducto(@Valid @RequestBody ProductoRequestDTO dto) {
        Producto productoCreado = productoService.crearProducto(dto);
        return ResponseEntity.created(URI.create("/api/inventario/productos/" + productoCreado.getIdProducto()))
                .body(productoCreado);
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarProductos() {
        return ResponseEntity.ok(productoService.buscarTodosProductos());
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<ProductoResponseDTO> obtenerProductoPorId(@PathVariable Long idProducto) {
        return ResponseEntity.ok(productoService.buscarProductoPorId(idProducto));
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long idProducto,
            @Valid @RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.ok(productoService.actualizarProducto(idProducto, dto));
    }

    @DeleteMapping("/{idProducto}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long idProducto) {
        productoService.eliminarProducto(idProducto);
        return ResponseEntity.noContent().build();
    }
}
