package ferrefix.ms_ventas.controller;

import java.util.List;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import ferrefix.ms_ventas.dto.VentaRequestDTO;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import ferrefix.ms_ventas.service.VentaService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private static final Logger logger = LoggerFactory.getLogger(VentaController.class);
    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<EntityModel<VentaResponseDTO>> crearVenta(@Valid @RequestBody VentaRequestDTO request, HttpServletRequest httpRequest) {
        logger.info("POST /api/ventas - Cliente RUN: {} | Empleado RUN: {} | TipoPago ID: {}",
                request.getRunCliente(), request.getRunEmpleado(), request.getIdTipoPago());
        VentaResponseDTO response = ventaService.guardar(request);
        
        EntityModel<VentaResponseDTO> model = EntityModel.of(response,
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(response.getIdVenta(), null)).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas(null)).withRel("ventas"),
                linkTo(methodOn(VentaController.class).listarDetalles(response.getIdVenta(), null)).withRel("detalles")
        );

        logger.info("POST /api/ventas - Venta ID {} creada. Respondiendo 201 CREATED", response.getIdVenta());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<VentaResponseDTO>>> listarVentas(HttpServletRequest request) {
        logger.info("GET /api/ventas - Listando todas las ventas");
        List<VentaResponseDTO> ventas = ventaService.listarVentas();
        
        List<EntityModel<VentaResponseDTO>> models = ventas.stream()
                .map(v -> EntityModel.of(v,
                        linkTo(methodOn(VentaController.class).obtenerVentaPorId(v.getIdVenta(), null)).withSelfRel(),
                        linkTo(methodOn(VentaController.class).listarDetalles(v.getIdVenta(), null)).withRel("detalles")
                ))
                .toList();

        CollectionModel<EntityModel<VentaResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(VentaController.class).listarVentas(null)).withSelfRel()
        );

        logger.info("GET /api/ventas - {} registros. Respondiendo 200 OK", ventas.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<EntityModel<VentaResponseDTO>> obtenerVentaPorId(@PathVariable Long idVenta, HttpServletRequest request) {
        logger.info("GET /api/ventas/{} - Buscando venta por ID", idVenta);
        VentaResponseDTO venta = ventaService.obtenerVentaPorId(idVenta);
        
        EntityModel<VentaResponseDTO> model = EntityModel.of(venta,
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(idVenta, null)).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas(null)).withRel("ventas"),
                linkTo(methodOn(VentaController.class).listarDetalles(idVenta, null)).withRel("detalles"),
                linkTo(methodOn(VentaController.class).eliminarVenta(idVenta)).withRel("eliminar")
        );

        logger.info("GET /api/ventas/{} - Encontrada. Respondiendo 200 OK", idVenta);
        return ResponseEntity.ok(model);
    }

    @GetMapping("/run/{runCliente}")
    public ResponseEntity<CollectionModel<EntityModel<VentaResponseDTO>>> obtenerVentasPorRunCliente(
            @PathVariable String runCliente, HttpServletRequest request) {

        logger.info("GET /api/ventas/run/{} - Buscando ventas por RUN cliente", runCliente);
        List<VentaResponseDTO> ventas = ventaService.buscarVentasPorRunCliente(runCliente);
        
        List<EntityModel<VentaResponseDTO>> models = ventas.stream()
                .map(v -> EntityModel.of(v,
                        linkTo(methodOn(VentaController.class).obtenerVentaPorId(v.getIdVenta(), null)).withSelfRel(),
                        linkTo(methodOn(VentaController.class).listarDetalles(v.getIdVenta(), null)).withRel("detalles")
                ))
                .toList();

        CollectionModel<EntityModel<VentaResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(VentaController.class).obtenerVentasPorRunCliente(runCliente, null)).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas(null)).withRel("ventas")
        );

        logger.info("GET /api/ventas/run/{} - {} ventas encontradas. Respondiendo 200 OK",
                runCliente, ventas.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/buscar")
    public ResponseEntity<CollectionModel<EntityModel<VentaResponseDTO>>> buscarVentas(
            @RequestParam(required = false) String runCliente,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            HttpServletRequest request) {

        logger.info("GET /api/ventas/buscar - Filtros recibidos: RUN: {}, Inicio: {}, Fin: {}", runCliente, fechaInicio, fechaFin);
        List<VentaResponseDTO> ventas = ventaService.buscarVentasConFiltros(runCliente, fechaInicio, fechaFin);
        
        List<EntityModel<VentaResponseDTO>> models = ventas.stream()
                .map(v -> EntityModel.of(v,
                        linkTo(methodOn(VentaController.class).obtenerVentaPorId(v.getIdVenta(), null)).withSelfRel(),
                        linkTo(methodOn(VentaController.class).listarDetalles(v.getIdVenta(), null)).withRel("detalles")
                ))
                .toList();

        CollectionModel<EntityModel<VentaResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(VentaController.class).buscarVentas(runCliente, fechaInicio, fechaFin, null)).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas(null)).withRel("ventas")
        );

        logger.info("GET /api/ventas/buscar - {} ventas filtradas encontradas. Respondiendo 200 OK", ventas.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{idVenta}/detalles")
    public ResponseEntity<CollectionModel<EntityModel<DetalleVentaResponseDTO>>> listarDetalles(@PathVariable Long idVenta, HttpServletRequest request) {
        logger.info("GET /api/ventas/{}/detalles - Buscando detalles de venta", idVenta);
        List<DetalleVentaResponseDTO> detalles = ventaService.buscarDetallesPorVenta(idVenta);
        
        List<EntityModel<DetalleVentaResponseDTO>> models = detalles.stream()
                .map(d -> EntityModel.of(d,
                        linkTo(methodOn(VentaController.class).obtenerVentaPorId(idVenta, null)).withRel("venta")
                ))
                .toList();

        CollectionModel<EntityModel<DetalleVentaResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(VentaController.class).listarDetalles(idVenta, null)).withSelfRel()
        );

        logger.info("GET /api/ventas/{}/detalles - {} ítems. Respondiendo 200 OK", idVenta, detalles.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{idVenta}/detalles/{idDetalle}")
    public ResponseEntity<EntityModel<DetalleVentaResponseDTO>> obtenerDetalle(
            @PathVariable Long idVenta,
            @PathVariable Long idDetalle, HttpServletRequest request) {

        logger.info("GET /api/ventas/{}/detalles/{} - Buscando detalle", idVenta, idDetalle);
        DetalleVentaResponseDTO detalle = ventaService.buscarDetallePorId(idVenta, idDetalle);
        
        EntityModel<DetalleVentaResponseDTO> model = EntityModel.of(detalle,
                linkTo(methodOn(VentaController.class).listarDetalles(idVenta, null)).withRel("detalles"),
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(idVenta, null)).withRel("venta")
        );

        logger.info("GET /api/ventas/{}/detalles/{} - Encontrado. Respondiendo 200 OK", idVenta, idDetalle);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{idVenta}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long idVenta) {
        logger.info("DELETE /api/ventas/{} - Solicitud de eliminación", idVenta);
        ventaService.eliminarVenta(idVenta);
        logger.info("DELETE /api/ventas/{} - Eliminada. Respondiendo 204 NO CONTENT", idVenta);
        return ResponseEntity.noContent().build();
    }
}