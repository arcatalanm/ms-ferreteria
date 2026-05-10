package ferrefix.ms_usuarios.controller;

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

    private static final Logger logger = LoggerFactory.getLogger(CargoController.class);
    private final CargoService cargoService;

    @PostMapping
    public ResponseEntity<Cargo> crearCargo(@Valid @RequestBody CargoRequestDTO dto) {
        logger.info("POST /api/usuarios/cargos - Solicitud para crear cargo: '{}'", dto.getNombreCargo());
        Cargo cargoCreado = cargoService.crearCargo(dto);
        logger.info("POST /api/usuarios/cargos - Cargo creado exitosamente. Respondiendo 201 CREATED");
        return ResponseEntity.created(URI.create("/api/usuarios/cargos/" + cargoCreado.getIdCargo()))
                .body(cargoCreado);
    }

    @GetMapping
    public ResponseEntity<List<CargoResponseDTO>> listarCargos() {
        logger.info("GET /api/usuarios/cargos - Solicitud para listar todos los cargos");
        List<CargoResponseDTO> cargos = cargoService.buscarTodos();
        logger.info("GET /api/usuarios/cargos - Listado enviado con éxito. Respondiendo 200 OK");
        return ResponseEntity.ok(cargos);
    }

    @GetMapping("/{idCargo}")
    public ResponseEntity<CargoResponseDTO> obtenerCargoPorId(@PathVariable Integer idCargo) {
        logger.info("GET /api/usuarios/cargos/{} - Solicitud para buscar cargo por ID", idCargo);
        CargoResponseDTO cargo = cargoService.buscarCargoPorId(idCargo);
        logger.info("GET /api/usuarios/cargos/{} - Cargo encontrado. Respondiendo 200 OK", idCargo);
        return ResponseEntity.ok(cargo);
    }

    @PutMapping("/{idCargo}")
    public ResponseEntity<Cargo> actualizarCargo(@PathVariable Integer idCargo, @Valid @RequestBody CargoRequestDTO dto) {
        logger.info("PUT /api/usuarios/cargos/{} - Solicitud para actualizar cargo", idCargo);
        Cargo cargoActualizado = cargoService.actualizarCargo(idCargo, dto);
        logger.info("PUT /api/usuarios/cargos/{} - Cargo actualizado. Respondiendo 200 OK", idCargo);
        return ResponseEntity.ok(cargoActualizado);
    }

    @DeleteMapping("/{idCargo}")
    public ResponseEntity<Void> eliminarCargo(@PathVariable Integer idCargo) {
        logger.info("DELETE /api/usuarios/cargos/{} - Solicitud para eliminar cargo", idCargo);
        cargoService.eliminarCargo(idCargo);
        logger.info("DELETE /api/usuarios/cargos/{} - Cargo eliminado. Respondiendo 204 NO CONTENT", idCargo);
        return ResponseEntity.noContent().build();
    }
}