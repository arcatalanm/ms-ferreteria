package ferrefix.ms_inventorario.controller;

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

import ferrefix.ms_inventorario.dto.UnidadMedidaRequestDTO;
import ferrefix.ms_inventorario.dto.UnidadMedidaResponseDTO;
import ferrefix.ms_inventorario.model.UnidadMedida;
import ferrefix.ms_inventorario.service.UnidadMedidaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventario/unidades")
@RequiredArgsConstructor
public class UnidadMedidaController {
    private final UnidadMedidaService unidadMedidaService;

    @PostMapping
    public ResponseEntity<UnidadMedida> crearUnidadMedida(@Valid @RequestBody UnidadMedidaRequestDTO dto) {
        UnidadMedida unidadCreada = unidadMedidaService.crearUnidadMedida(dto);
        return ResponseEntity.created(URI.create("/api/inventario/unidades/" + unidadCreada.getIdUnidadMedida()))
                .body(unidadCreada);
    }

    @GetMapping
    public ResponseEntity<List<UnidadMedidaResponseDTO>> listarUnidadesMedida() {
        return ResponseEntity.ok(unidadMedidaService.buscarTodasUnidadesMedida());
    }

    @GetMapping("/{idUnidadMedida}")
    public ResponseEntity<UnidadMedidaResponseDTO> obtenerUnidadMedidaPorId(@PathVariable Integer idUnidadMedida) {
        return ResponseEntity.ok(unidadMedidaService.buscarUnidadMedidaPorId(idUnidadMedida));
    }

    @PutMapping("/{idUnidadMedida}")
    public ResponseEntity<UnidadMedida> actualizarUnidadMedida(@PathVariable Integer idUnidadMedida,
            @Valid @RequestBody UnidadMedidaRequestDTO dto) {
        return ResponseEntity.ok(unidadMedidaService.actualizarUnidadMedida(idUnidadMedida, dto));
    }

    @DeleteMapping("/{idUnidadMedida}")
    public ResponseEntity<Void> eliminarUnidadMedida(@PathVariable Integer idUnidadMedida) {
        unidadMedidaService.eliminarUnidadMedida(idUnidadMedida);
        return ResponseEntity.noContent().build();
    }
}
