package ferrefix.ms_ventas.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.service.DetalleVentaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas/detalles")
@RequiredArgsConstructor
public class DetalleVentaController {

    private final DetalleVentaService detalleVentaService;

    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<List<DetalleVentaResponseDTO>> listarDetallesPorVenta(@PathVariable Long idVenta) {
        return ResponseEntity.ok(detalleVentaService.buscarDetallesPorVenta(idVenta));
    }

    @GetMapping("/{idDetalle}")
    public ResponseEntity<DetalleVentaResponseDTO> obtenerDetallePorId(@PathVariable Long idDetalle) {
        return ResponseEntity.ok(detalleVentaService.buscarDetallePorId(idDetalle));
    }
}
