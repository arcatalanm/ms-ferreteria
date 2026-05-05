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

import ferrefix.ms_inventorario.dto.CategoriaProductoRequestDTO;
import ferrefix.ms_inventorario.dto.CategoriaProductoResponseDTO;
import ferrefix.ms_inventorario.model.CategoriaProducto;
import ferrefix.ms_inventorario.service.CategoriaProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario/categorias")
@RequiredArgsConstructor
public class CategoriaProductoController {
    private final CategoriaProductoService categoriaProductoService;

    @PostMapping
    public ResponseEntity<CategoriaProducto> crearCategoriaProducto(@Valid @RequestBody CategoriaProductoRequestDTO dto) {
        CategoriaProducto categoriaCreada = categoriaProductoService.crearCategoriaProducto(dto);
        return ResponseEntity.created(URI.create("/api/inventario/categorias/" + categoriaCreada.getIdCategoria()))
                .body(categoriaCreada);
    }

    @GetMapping
    public ResponseEntity<List<CategoriaProductoResponseDTO>> listarCategorias() {
        return ResponseEntity.ok(categoriaProductoService.buscarTodasCategorias());
    }

    @GetMapping("/{idCategoria}")
    public ResponseEntity<CategoriaProductoResponseDTO> obtenerCategoriaPorId(@PathVariable Integer idCategoria) {
        return ResponseEntity.ok(categoriaProductoService.buscarCategoriaPorId(idCategoria));
    }

    @PutMapping("/{idCategoria}")
    public ResponseEntity<CategoriaProducto> actualizarCategoriaProducto(@PathVariable Integer idCategoria,
            @Valid @RequestBody CategoriaProductoRequestDTO dto) {
        return ResponseEntity.ok(categoriaProductoService.actualizarCategoriaProducto(idCategoria, dto));
    }

    @DeleteMapping("/{idCategoria}")
    public ResponseEntity<Void> eliminarCategoriaProducto(@PathVariable Integer idCategoria) {
        categoriaProductoService.eliminarCategoriaProducto(idCategoria);
        return ResponseEntity.noContent().build();
    }
}
