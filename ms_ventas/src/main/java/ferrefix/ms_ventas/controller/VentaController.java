package ferrefix.ms_ventas.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.dto.VentaRequestDTO;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import ferrefix.ms_ventas.exception.ApiSuccessResponse;
import ferrefix.ms_ventas.service.VentaService;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);
    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaResponseDTO> crearVenta(@Valid @RequestBody VentaRequestDTO request) {
        logger.info("POST /api/ventas - Cliente RUN: {} | Empleado RUN: {} | TipoPago ID: {}",
                request.getRunCliente(), request.getRunEmpleado(), request.getIdTipoPago());
        VentaResponseDTO response = ventaService.guardar(request);
        logger.info("POST /api/ventas - Venta ID {} creada. Respondiendo 201 CREATED", response.getIdVenta());
        return ResponseEntity
                .created(URI.create("/api/ventas/" + response.getIdVenta()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        logger.info("GET /api/ventas - Listando todas las ventas");
        List<VentaResponseDTO> ventas = ventaService.listarVentas();
        logger.info("GET /api/ventas - {} registros. Respondiendo 200 OK", ventas.size());
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/run/{runCliente}")
    public ResponseEntity<List<VentaResponseDTO>> obtenerVentasPorRunCliente(
            @PathVariable String runCliente) {

        logger.info("GET /api/ventas/run/{} - Buscando ventas por RUN cliente", runCliente);
        List<VentaResponseDTO> ventas = ventaService.buscarVentasPorRunCliente(runCliente);
        logger.info("GET /api/ventas/run/{} - {} ventas encontradas. Respondiendo 200 OK",
                runCliente, ventas.size());
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/{idVenta}/detalles")
    public ResponseEntity<List<DetalleVentaResponseDTO>> listarDetalles(@PathVariable Long idVenta) {
        logger.info("GET /api/ventas/{}/detalles - Buscando detalles de venta", idVenta);
        List<DetalleVentaResponseDTO> detalles = ventaService.buscarDetallesPorVenta(idVenta);
        logger.info("GET /api/ventas/{}/detalles - {} ítems. Respondiendo 200 OK", idVenta, detalles.size());
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{idVenta}/detalles/{idDetalle}")
    public ResponseEntity<DetalleVentaResponseDTO> obtenerDetalle(
            @PathVariable Long idVenta,
            @PathVariable Long idDetalle) {

        logger.info("GET /api/ventas/{}/detalles/{} - Buscando detalle", idVenta, idDetalle);
        DetalleVentaResponseDTO detalle = ventaService.buscarDetallePorId(idVenta, idDetalle);
        logger.info("GET /api/ventas/{}/detalles/{} - Encontrado. Respondiendo 200 OK", idVenta, idDetalle);
        return ResponseEntity.ok(detalle);
    }

    @DeleteMapping("/{idVenta}")
    public ResponseEntity<ApiSuccessResponse> eliminarVenta(
            @PathVariable Long idVenta,
            HttpServletRequest request) {

        logger.info("DELETE /api/ventas/{} - Solicitud de eliminación", idVenta);
        ventaService.eliminarVenta(idVenta);

        ApiSuccessResponse respuesta = ApiSuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("La venta con ID " + idVenta + " fue eliminada correctamente.")
                .path(request.getRequestURI())
                .build();

        logger.info("DELETE /api/ventas/{} - Eliminada. Respondiendo 200 OK", idVenta);
        return ResponseEntity.ok(respuesta);
    }
}