package ferrefix.ms_ventas.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.service.DetalleVentaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas/detalles")
@RequiredArgsConstructor
public class DetalleVentaController {
    
    private static final Logger logger = LoggerFactory.getLogger(DetalleVentaController.class);
    private final DetalleVentaService detalleVentaService;

    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<List<DetalleVentaResponseDTO>> listarDetallesPorVenta(@PathVariable Long idVenta) {
        logger.info("GET /api/ventas/detalles/venta/{} - Solicitud para obtener detalles de venta", idVenta);
        return ResponseEntity.ok(detalleVentaService.buscarDetallesPorVenta(idVenta));
    }

    @GetMapping("/{idDetalle}")
    public ResponseEntity<DetalleVentaResponseDTO> obtenerDetallePorId(@PathVariable Long idDetalle) {
        logger.info("GET /api/ventas/detalles/{} - Solicitud para obtener detalle", idDetalle);
        return ResponseEntity.ok(detalleVentaService.buscarDetallePorId(idDetalle));
    }
}