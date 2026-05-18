package ferrefix.ms_ventas.controller;

import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ferrefix.ms_ventas.dto.VentaRequestDTO;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import ferrefix.ms_ventas.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {
    
    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);

    // Inyección por @RequiredArgsConstructor
    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaResponseDTO> crearVenta(@Valid @RequestBody VentaRequestDTO request) {
        logger.info("POST /api/ventas - Solicitud de creación de venta recibida");
        
        VentaResponseDTO response = ventaService.guardar(request);
        
        logger.info("Venta procesada. Respondiendo 201 CREATED");
        return ResponseEntity.created(URI.create("/api/ventas/" + response.getIdVenta()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        logger.info("GET /api/ventas - Solicitud de listado completo");
        List<VentaResponseDTO> ventas = ventaService.listarVentas();
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorId(@PathVariable Long idVenta) {
        logger.info("GET /api/ventas/{} - Solicitud de búsqueda por ID", idVenta);
        VentaResponseDTO venta = ventaService.buscarVentaPorId(idVenta);
        return ResponseEntity.ok(venta);
    }

    @DeleteMapping("/{idVenta}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long idVenta) {
        logger.info("DELETE /api/ventas/{} - Solicitud de eliminación", idVenta);
        ventaService.eliminarVenta(idVenta);
        
        logger.info("Eliminación exitosa. Respondiendo 204 NO CONTENT");
        // Operacion exitosa pero el cuerpo no tiene contenido
        return ResponseEntity.noContent().build();
    }
}