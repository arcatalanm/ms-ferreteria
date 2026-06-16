package ferrefix.ms_usuarios.controller;

import java.time.LocalDateTime;
import java.util.List;

import ferrefix.ms_usuarios.mapper.CargoMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/usuarios/cargos")
@RequiredArgsConstructor
public class CargoController {

    private static final Logger logger = LoggerFactory.getLogger(CargoController.class);
    private final CargoService cargoService;
    private final CargoMapper cargoMapper;

    @PostMapping
    public ResponseEntity<EntityModel<CargoResponseDTO>> crearCargo(@Valid @RequestBody CargoRequestDTO dto, HttpServletRequest request) {
        logger.info("POST /api/usuarios/cargos - Solicitud para crear cargo: '{}'", dto.getNombreCargo());
        Cargo cargoCreado = cargoService.crearCargo(dto);
        CargoResponseDTO responseDTO = cargoMapper.toResponseDTO(cargoCreado);

        EntityModel<CargoResponseDTO> model = EntityModel.of(responseDTO,
                linkTo(methodOn(CargoController.class).obtenerCargoPorId(responseDTO.getIdCargo(), null)).withSelfRel(),
                linkTo(methodOn(CargoController.class).listarCargos(null)).withRel("cargos")
        );

        logger.info("POST /api/usuarios/cargos - Cargo creado exitosamente. Respondiendo 201 CREATED");
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CargoResponseDTO>>> listarCargos(HttpServletRequest request) {
        logger.info("GET /api/usuarios/cargos - Solicitud para listar todos los cargos");
        List<CargoResponseDTO> cargos = cargoService.buscarTodos();

        List<EntityModel<CargoResponseDTO>> models = cargos.stream()
                .map(c -> EntityModel.of(c,
                        linkTo(methodOn(CargoController.class).obtenerCargoPorId(c.getIdCargo(), null)).withSelfRel(),
                        linkTo(methodOn(CargoController.class).listarCargos(null)).withRel("cargos")
                ))
                .toList();

        CollectionModel<EntityModel<CargoResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(CargoController.class).listarCargos(null)).withSelfRel()
        );

        logger.info("GET /api/usuarios/cargos - Listado enviado con éxito. Respondiendo 200 OK");
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{idCargo}")
    public ResponseEntity<EntityModel<CargoResponseDTO>> obtenerCargoPorId(@PathVariable Integer idCargo, HttpServletRequest request) {
        logger.info("GET /api/usuarios/cargos/{} - Solicitud para buscar cargo por ID", idCargo);
        CargoResponseDTO cargo = cargoService.buscarCargoPorId(idCargo);

        EntityModel<CargoResponseDTO> model = EntityModel.of(cargo,
                linkTo(methodOn(CargoController.class).obtenerCargoPorId(idCargo, null)).withSelfRel(),
                linkTo(methodOn(CargoController.class).listarCargos(null)).withRel("cargos"),
                linkTo(methodOn(CargoController.class).actualizarCargo(idCargo, null, null)).withRel("actualizar"),
                linkTo(methodOn(CargoController.class).eliminarCargo(idCargo)).withRel("eliminar")
        );

        logger.info("GET /api/usuarios/cargos/{} - Cargo encontrado. Respondiendo 200 OK", idCargo);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{idCargo}")
    public ResponseEntity<EntityModel<CargoResponseDTO>> actualizarCargo(@PathVariable Integer idCargo, @Valid @RequestBody CargoRequestDTO dto, HttpServletRequest request) {
        logger.info("PUT /api/usuarios/cargos/{} - Solicitud para actualizar cargo", idCargo);
        Cargo cargoActualizado = cargoService.actualizarCargo(idCargo, dto);
        CargoResponseDTO responseDTO = cargoMapper.toResponseDTO(cargoActualizado);

        EntityModel<CargoResponseDTO> model = EntityModel.of(responseDTO,
                linkTo(methodOn(CargoController.class).obtenerCargoPorId(idCargo, null)).withSelfRel(),
                linkTo(methodOn(CargoController.class).listarCargos(null)).withRel("cargos")
        );

        logger.info("PUT /api/usuarios/cargos/{} - Cargo actualizado. Respondiendo 200 OK", idCargo);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{idCargo}")
    public ResponseEntity<Void> eliminarCargo(@PathVariable Integer idCargo) {
        logger.info("DELETE /api/usuarios/cargos/{} - Solicitud para eliminar cargo", idCargo);
        cargoService.eliminarCargo(idCargo);
        logger.info("DELETE /api/usuarios/cargos/{} - Cargo eliminado. Respondiendo 204 NO CONTENT", idCargo);
        return ResponseEntity.noContent().build();
    }
}