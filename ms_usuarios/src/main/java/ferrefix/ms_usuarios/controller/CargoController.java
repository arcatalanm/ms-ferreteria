package ferrefix.ms_usuarios.controller;

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

import ferrefix.ms_usuarios.dto.CargoRequestDTO;
import ferrefix.ms_usuarios.dto.CargoResponseDTO;
import ferrefix.ms_usuarios.model.Cargo;
import ferrefix.ms_usuarios.service.CargoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios/cargos")
@RequiredArgsConstructor
public class CargoController {
    private final CargoService cargoService;

    
    @PostMapping
    public ResponseEntity<Cargo> crearCargo(@Valid @RequestBody CargoRequestDTO dto) {
        Cargo cargoCreado = cargoService.crearCargo(dto);
        return ResponseEntity.created(URI.create("/api/usuarios/cargos/" + cargoCreado.getIdCargo()))
                .body(cargoCreado);
    }

    @GetMapping
    public ResponseEntity<List<CargoResponseDTO>> listarCargos() {
        return ResponseEntity.ok(cargoService.buscarTodos());
    }

    @GetMapping("/{idCargo}")
    public ResponseEntity<CargoResponseDTO> obtenerCargoPorId(@PathVariable Integer idCargo) {
        return ResponseEntity.ok(cargoService.buscarCargoPorId(idCargo));
    }

    @PutMapping("/{idCargo}")
    public ResponseEntity<Cargo> actualizarCargo(@PathVariable Integer idCargo,
            @Valid @RequestBody CargoRequestDTO dto) {
        return ResponseEntity.ok(cargoService.actualizarCargo(idCargo, dto));
    }

    @DeleteMapping("/{idCargo}")
    public ResponseEntity<Void> eliminarCargo(@PathVariable Integer idCargo) {
        cargoService.eliminarCargo(idCargo);
        return ResponseEntity.noContent().build();
    }
}
