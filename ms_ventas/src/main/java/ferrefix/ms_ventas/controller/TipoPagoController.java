package ferrefix.ms_ventas.controller;

import java.net.URI;
import java.util.List;

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
import ferrefix.ms_ventas.model.TipoPago;
import ferrefix.ms_ventas.service.TipoPagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ventas/tipos-pago")
@RequiredArgsConstructor
public class TipoPagoController {

    private final TipoPagoService tipoPagoService;

    @PostMapping
    public ResponseEntity<TipoPago> crearTipoPago(@Valid @RequestBody TipoPagoRequestDTO dto) {
        TipoPago tipoPagoCreado = tipoPagoService.crearTipoPago(dto);
        return ResponseEntity.created(URI.create("/api/ventas/tipos-pago/" + tipoPagoCreado.getIdTipoPago()))
                .body(tipoPagoCreado);
    }

    @GetMapping
    public ResponseEntity<List<TipoPagoResponseDTO>> listarTiposPago() {
        return ResponseEntity.ok(tipoPagoService.listarTiposPago());
    }

    @GetMapping("/{idTipoPago}")
    public ResponseEntity<TipoPagoResponseDTO> obtenerTipoPagoPorId(@PathVariable Integer idTipoPago) {
        return ResponseEntity.ok(tipoPagoService.buscarTipoPagoPorId(idTipoPago));
    }

    @PutMapping("/{idTipoPago}")
    public ResponseEntity<TipoPago> actualizarTipoPago(@PathVariable Integer idTipoPago,
            @Valid @RequestBody TipoPagoRequestDTO dto) {
        return ResponseEntity.ok(tipoPagoService.actualizarTipoPago(idTipoPago, dto));
    }

    @DeleteMapping("/{idTipoPago}")
    public ResponseEntity<Void> eliminarTipoPago(@PathVariable Integer idTipoPago) {
        tipoPagoService.eliminarTipoPago(idTipoPago);
        return ResponseEntity.noContent().build();
    }
}
