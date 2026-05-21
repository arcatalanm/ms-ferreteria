package ferrefix.ms_proveedores.controller;


import ferrefix.ms_proveedores.dto.ProveedorRequestDTO;
import ferrefix.ms_proveedores.dto.ProveedorResponseDTO;
import ferrefix.ms_proveedores.exception.ApiSuccessResponse;
import ferrefix.ms_proveedores.service.ProveedorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);
    private final ProveedorService proveedorService;

    @PostMapping
    public ResponseEntity<ProveedorResponseDTO> crearProveedor(
            @Valid @RequestBody ProveedorRequestDTO dto) {

        logger.info("POST /api/proveedores - RUT: {}", dto.getRutProveedor());
        ProveedorResponseDTO creado = proveedorService.guardar(dto);
        logger.info("POST /api/proveedores - Proveedor creado. ID: {}. Respondiendo 201 CREATED",
                creado.getIdProveedor());
        return ResponseEntity
                .created(URI.create("/api/proveedores/" + creado.getIdProveedor()))
                .body(creado);
    }

    @GetMapping
    public ResponseEntity<List<ProveedorResponseDTO>> listarProveedores() {
        logger.info("GET /api/proveedores - Listando todos los proveedores");
        List<ProveedorResponseDTO> lista = proveedorService.listarTodos();
        logger.info("GET /api/proveedores - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> obtenerProveedor(@PathVariable Integer id) {
        logger.info("GET /api/proveedores/{} - Buscando proveedor", id);
        ProveedorResponseDTO response = proveedorService.buscarPorId(id);
        logger.info("GET /api/proveedores/{} - Encontrado. Respondiendo 200 OK", id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponseDTO> actualizarProveedor(
            @PathVariable Integer id,
            @Valid @RequestBody ProveedorRequestDTO dto) {

        logger.info("PUT /api/proveedores/{} - Actualizando proveedor", id);
        ProveedorResponseDTO actualizado = proveedorService.actualizar(id, dto);
        logger.info("PUT /api/proveedores/{} - Actualizado. Respondiendo 200 OK", id);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiSuccessResponse> eliminarProveedor(
            @PathVariable Integer id,
            HttpServletRequest request) {

        logger.info("DELETE /api/proveedores/{} - Solicitud de eliminación", id);
        proveedorService.eliminar(id);

        ApiSuccessResponse respuesta = ApiSuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("El proveedor con ID " + id + " fue eliminado correctamente.")
                .path(request.getRequestURI())
                .build();

        logger.info("DELETE /api/proveedores/{} - Eliminado. Respondiendo 200 OK", id);
        return ResponseEntity.ok(respuesta);
    }
}