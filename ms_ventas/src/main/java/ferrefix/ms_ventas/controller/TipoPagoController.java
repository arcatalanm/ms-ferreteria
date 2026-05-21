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

import ferrefix.ms_ventas.dto.TipoPagoRequestDTO;
import ferrefix.ms_ventas.dto.TipoPagoResponseDTO;
import ferrefix.ms_ventas.exception.ApiSuccessResponse;
import ferrefix.ms_ventas.service.TipoPagoService;

@RestController
@RequestMapping("/api/ventas/tipos-pago")
@RequiredArgsConstructor
public class TipoPagoController {

    private static final Logger logger = LoggerFactory.getLogger(TipoPagoController.class);
    private final TipoPagoService tipoPagoService;

    @PostMapping
    public ResponseEntity<TipoPagoResponseDTO> crear(@Valid @RequestBody TipoPagoRequestDTO dto) {
        logger.info("POST /api/ventas/tipos-pago - Nombre: '{}'", dto.getNombreTipoPago());
        TipoPagoResponseDTO creado = tipoPagoService.crear(dto);
        logger.info("POST /api/ventas/tipos-pago - Creado ID: {}. Respondiendo 201 CREATED", creado.getIdTipoPago());
        return ResponseEntity
                .created(URI.create("/api/ventas/tipos-pago/" + creado.getIdTipoPago()))
                .body(creado);
    }

    @GetMapping
    public ResponseEntity<List<TipoPagoResponseDTO>> obtenerTodos() {
        logger.info("GET /api/ventas/tipos-pago - Listando tipos de pago");
        List<TipoPagoResponseDTO> lista = tipoPagoService.obtenerTodos();
        logger.info("GET /api/ventas/tipos-pago - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{idTipoPago}")
    public ResponseEntity<TipoPagoResponseDTO> obtenerPorId(@PathVariable Integer idTipoPago) {
        logger.info("GET /api/ventas/tipos-pago/{} - Buscando tipo de pago", idTipoPago);
        TipoPagoResponseDTO tipoPago = tipoPagoService.obtenerPorId(idTipoPago);
        logger.info("GET /api/ventas/tipos-pago/{} - Encontrado. Respondiendo 200 OK", idTipoPago);
        return ResponseEntity.ok(tipoPago);
    }

    @PutMapping("/{idTipoPago}")
    public ResponseEntity<TipoPagoResponseDTO> actualizar(
            @PathVariable Integer idTipoPago,
            @Valid @RequestBody TipoPagoRequestDTO dto) {

        logger.info("PUT /api/ventas/tipos-pago/{} - Actualizando tipo de pago", idTipoPago);
        TipoPagoResponseDTO actualizado = tipoPagoService.actualizar(idTipoPago, dto);
        logger.info("PUT /api/ventas/tipos-pago/{} - Actualizado. Respondiendo 200 OK", idTipoPago);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{idTipoPago}")
    public ResponseEntity<ApiSuccessResponse> eliminar(
            @PathVariable Integer idTipoPago,
            HttpServletRequest request) {

        logger.info("DELETE /api/ventas/tipos-pago/{} - Solicitud de eliminación", idTipoPago);
        tipoPagoService.eliminar(idTipoPago);

        ApiSuccessResponse respuesta = ApiSuccessResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .message("El tipo de pago con ID " + idTipoPago + " fue eliminado correctamente.")
                .path(request.getRequestURI())
                .build();

        logger.info("DELETE /api/ventas/tipos-pago/{} - Eliminado. Respondiendo 200 OK", idTipoPago);
        return ResponseEntity.ok(respuesta);
    }
}