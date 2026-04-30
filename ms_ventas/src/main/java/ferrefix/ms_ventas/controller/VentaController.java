package ferrefix.ms_ventas.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ferrefix.ms_ventas.dto.VentaRequestDTO;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import ferrefix.ms_ventas.model.Venta;
import ferrefix.ms_ventas.service.VentaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    // Endpoint para registrar una nueva venta
    @PostMapping
    public ResponseEntity<VentaResponseDTO> crearVenta(@RequestBody VentaRequestDTO request) {
        // Llamamos al servicio, que hará todo el trabajo pesado y las consultas por red
        VentaResponseDTO response = ventaService.guardar(request);
        
        // Devolvemos la boleta generada con un código 201 (Creado)
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Endpoint temporal para ver todas las ventas en crudo
    @GetMapping
    public ResponseEntity<List<Venta>> listarVentas() {
        return ResponseEntity.ok(ventaService.listar());
    }
}