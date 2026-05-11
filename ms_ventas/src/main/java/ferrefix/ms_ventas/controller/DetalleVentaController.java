package ferrefix.ms_ventas.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.service.DetalleVentaService;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de detalles de ventas.
 * Maneja todas las solicitudes HTTP relacionadas con DetalleVenta.
 */
@RestController
@RequestMapping("/api/ventas/detalles")
@RequiredArgsConstructor
public class DetalleVentaController {
    
    private static final Logger logger = LoggerFactory.getLogger(DetalleVentaController.class);

    private final DetalleVentaService detalleVentaService;

    /**
     * GET - Obtiene todos los detalles de una venta específica
     */
    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<List<DetalleVentaResponseDTO>> listarDetallesPorVenta(@PathVariable Long idVenta) {
        logger.info("GET /api/ventas/detalles/venta/{} - Solicitud para obtener detalles de venta", idVenta);
        
        List<DetalleVentaResponseDTO> detalles = detalleVentaService.buscarDetallesPorVenta(idVenta);
        
        logger.info("GET /api/ventas/detalles/venta/{} - Respuesta exitosa. Total encontrado: {}", idVenta, detalles.size());
        return ResponseEntity.ok(detalles);
    }

    /**
     * GET - Obtiene un detalle de venta por su ID
     */
    @GetMapping("/{idDetalle}")
    public ResponseEntity<DetalleVentaResponseDTO> obtenerDetallePorId(@PathVariable Long idDetalle) {
        logger.info("GET /api/ventas/detalles/{} - Solicitud para obtener detalle de venta", idDetalle);
        
        DetalleVentaResponseDTO detalle = detalleVentaService.buscarDetallePorId(idDetalle);
        
        logger.info("GET /api/ventas/detalles/{} - Respuesta exitosa", idDetalle);
        return ResponseEntity.ok(detalle);
    }
}
