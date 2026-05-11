package ferrefix.ms_ventas.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ferrefix.ms_ventas.dto.TipoPagoRequestDTO;
import ferrefix.ms_ventas.dto.TipoPagoResponseDTO;
import ferrefix.ms_ventas.service.TipoPagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador REST para la gestión de tipos de pago.
 * Maneja todas las solicitudes HTTP relacionadas con TipoPago.
 */
@RestController
@RequestMapping("/api/ventas/tipos-pago")
@RequiredArgsConstructor
public class TipoPagoController {
    
    private static final Logger logger = LoggerFactory.getLogger(TipoPagoController.class);

    private final TipoPagoService tipoPagoService;

    /**
     * POST - Crea un nuevo tipo de pago
     */
    @PostMapping
    public ResponseEntity<TipoPagoResponseDTO> crear(@Valid @RequestBody TipoPagoRequestDTO dto) {
        logger.info("POST /api/ventas/tipos-pago - Solicitud para crear nuevo tipo de pago: {}", dto.getNombreTipoPago());
        
        TipoPagoResponseDTO tipoPagoCreado = tipoPagoService.crear(dto);
        
        logger.info("POST /api/ventas/tipos-pago - Tipo de pago creado exitosamente con ID: {}", tipoPagoCreado.getIdTipoPago());
        return ResponseEntity.created(URI.create("/api/ventas/tipos-pago/" + tipoPagoCreado.getIdTipoPago()))
                .body(tipoPagoCreado);
    }

    /**
     * GET - Obtiene todos los tipos de pago
     */
    @GetMapping
    public ResponseEntity<List<TipoPagoResponseDTO>> obtenerTodos() {
        logger.info("GET /api/ventas/tipos-pago - Solicitud para obtener todos los tipos de pago");
        
        List<TipoPagoResponseDTO> tiposPago = tipoPagoService.obtenerTodos();
        
        logger.info("GET /api/ventas/tipos-pago - Respuesta exitosa. Total encontrado: {}", tiposPago.size());
        return ResponseEntity.ok(tiposPago);
    }

    /**
     * GET - Obtiene un tipo de pago por su ID
     */
    @GetMapping("/{idTipoPago}")
    public ResponseEntity<TipoPagoResponseDTO> obtenerPorId(@PathVariable Integer idTipoPago) {
        logger.info("GET /api/ventas/tipos-pago/{} - Solicitud para obtener tipo de pago", idTipoPago);
        
        TipoPagoResponseDTO tipoPago = tipoPagoService.obtenerPorId(idTipoPago);
        
        logger.info("GET /api/ventas/tipos-pago/{} - Respuesta exitosa", idTipoPago);
        return ResponseEntity.ok(tipoPago);
    }

    /**
     * PUT - Actualiza un tipo de pago existente
     */
    @PutMapping("/{idTipoPago}")
    public ResponseEntity<TipoPagoResponseDTO> actualizar(@PathVariable Integer idTipoPago, @Valid @RequestBody TipoPagoRequestDTO dto) {
        logger.info("PUT /api/ventas/tipos-pago/{} - Solicitud para actualizar tipo de pago", idTipoPago);
        
        TipoPagoResponseDTO tipoPagoActualizado = tipoPagoService.actualizar(idTipoPago, dto);
        
        logger.info("PUT /api/ventas/tipos-pago/{} - Tipo de pago actualizado exitosamente", idTipoPago);
        return ResponseEntity.ok(tipoPagoActualizado);
    }

    /**
     * DELETE - Elimina un tipo de pago
     */
    @DeleteMapping("/{idTipoPago}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idTipoPago) {
        logger.info("DELETE /api/ventas/tipos-pago/{} - Solicitud para eliminar tipo de pago", idTipoPago);
        
        tipoPagoService.eliminar(idTipoPago);
        
        logger.info("DELETE /api/ventas/tipos-pago/{} - Tipo de pago eliminado exitosamente", idTipoPago);
        return ResponseEntity.noContent().build();
    }
}
