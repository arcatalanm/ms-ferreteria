package ferrefix.ms_ventas.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ferrefix.ms_ventas.dto.VentaRequestDTO;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import ferrefix.ms_ventas.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<VentaResponseDTO> crearVenta(@Valid @RequestBody VentaRequestDTO request) {
        VentaResponseDTO response = ventaService.guardar(request);
        return ResponseEntity.created(URI.create("/api/ventas/" + response.getIdVenta()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @GetMapping("/{idVenta}")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorId(@PathVariable Long idVenta) {
        return ResponseEntity.ok(ventaService.buscarVentaPorId(idVenta));
    }

    @GetMapping("/clientes/{runCliente}")
    public ResponseEntity<List<VentaResponseDTO>> listarVentasPorCliente(@PathVariable Integer runCliente) {
        return ResponseEntity.ok(ventaService.buscarVentasPorCliente(runCliente));
    }

    @GetMapping("/empleados/{runEmpleado}")
    public ResponseEntity<List<VentaResponseDTO>> listarVentasPorEmpleado(@PathVariable Integer runEmpleado) {
        return ResponseEntity.ok(ventaService.buscarVentasPorEmpleado(runEmpleado));
    }

    @DeleteMapping("/{idVenta}")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long idVenta) {
        ventaService.eliminarVenta(idVenta);
        return ResponseEntity.noContent().build();
    }
}