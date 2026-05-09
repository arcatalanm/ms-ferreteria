package ferrefix.ms_proveedores.controller;

import ferrefix.ms_proveedores.dto.ProveedorRequestDTO;
import ferrefix.ms_proveedores.dto.ProveedorResponseDTO;
import ferrefix.ms_proveedores.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);

    private final ProveedorService proveedorService;

    @PostMapping
    public ResponseEntity<ProveedorResponseDTO> crearProveedor(@Valid @RequestBody ProveedorRequestDTO requestDTO) {
        logger.info("POST /api/proveedores - Solicitud para crear proveedor");
        ProveedorResponseDTO responseDTO = proveedorService.guardar(requestDTO);
        logger.info("POST /api/proveedores - Proveedor creado con éxito. id={}", responseDTO.getIdProveedor());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ProveedorResponseDTO>> listarProveedores() {
        logger.info("GET /api/proveedores - Solicitud para listar proveedores");
        List<ProveedorResponseDTO> proveedores = proveedorService.listarTodos();
        logger.info("GET /api/proveedores - Listado devuelto con éxito. total={}", proveedores.size());
        return ResponseEntity.ok(proveedores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> obtenerProveedor(@PathVariable Integer id) {
        logger.info("GET /api/proveedores/{} - Solicitud para obtener proveedor", id);
        ProveedorResponseDTO responseDTO = proveedorService.buscarPorId(id);
        logger.info("GET /api/proveedores/{} - Proveedor obtenido con éxito", id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> actualizarProveedor(@PathVariable Integer id,
                                                                     @Valid @RequestBody ProveedorRequestDTO requestDTO) {
        logger.info("PUT /api/proveedores/{} - Solicitud para actualizar proveedor", id);
        ProveedorResponseDTO responseDTO = proveedorService.actualizar(id, requestDTO);
        logger.info("PUT /api/proveedores/{} - Proveedor actualizado con éxito", id);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Integer id) {
        logger.info("DELETE /api/proveedores/{} - Solicitud para eliminar proveedor", id);
        proveedorService.eliminar(id);
        logger.info("DELETE /api/proveedores/{} - Proveedor eliminado con éxito", id);
        return ResponseEntity.noContent().build();
    }
}