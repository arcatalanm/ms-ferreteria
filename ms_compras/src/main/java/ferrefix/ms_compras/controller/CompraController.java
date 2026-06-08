package ferrefix.ms_compras.controller;

import ferrefix.ms_compras.dto.CompraRequestDTO;
import ferrefix.ms_compras.dto.CompraResponseDTO;
import ferrefix.ms_compras.exception.ApiSuccessResponse;
import ferrefix.ms_compras.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @PostMapping
    public ResponseEntity<ApiSuccessResponse<CompraResponseDTO>> crearOrden(@Valid @RequestBody CompraRequestDTO request) {
        log.info("Petición recibida: POST /api/compras");
        CompraResponseDTO response = compraService.crearOrdenCompra(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiSuccessResponse.<CompraResponseDTO>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.CREATED.value())
                        .message("Orden de compra creada exitosamente")
                        .data(response)
                        .build()
        );
    }

    @PutMapping("/{id}/recibir")
    public ResponseEntity<ApiSuccessResponse<CompraResponseDTO>> recibirMercancia(@PathVariable Long id) {
        log.info("Petición recibida: PUT /api/compras/{}/recibir", id);
        CompraResponseDTO response = compraService.procesarRecepcionMercancia(id);
        return ResponseEntity.ok(
                ApiSuccessResponse.<CompraResponseDTO>builder()
                        .timestamp(LocalDateTime.now())
                        .status(HttpStatus.OK.value())
                        .message("Mercancía recibida e inventario actualizado")
                        .data(response)
                        .build()
        );
    }
}
