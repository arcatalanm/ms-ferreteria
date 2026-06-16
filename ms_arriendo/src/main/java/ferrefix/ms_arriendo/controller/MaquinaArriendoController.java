package ferrefix.ms_arriendo.controller;

import ferrefix.ms_arriendo.dto.MaquinaRequestDTO;
import ferrefix.ms_arriendo.dto.MaquinaResponseDTO;
import ferrefix.ms_arriendo.dto.ProcesarArriendoRequestDTO;
import ferrefix.ms_arriendo.service.MaquinaArriendoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/arriendos/maquinas")
@RequiredArgsConstructor
public class MaquinaArriendoController {

    private final MaquinaArriendoService maquinaArriendoService;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MaquinaResponseDTO>>> listarTodas() {
        List<MaquinaResponseDTO> maquinas = maquinaArriendoService.listarTodas();

        List<EntityModel<MaquinaResponseDTO>> models = maquinas.stream()
                .map(m -> EntityModel.of(m,
                        linkTo(methodOn(MaquinaArriendoController.class).listarTodas()).withRel("maquinas"),
                        linkTo(methodOn(MaquinaArriendoController.class).arrendar(m.getIdEquipo(), null)).withRel("arrendar"),
                        linkTo(methodOn(MaquinaArriendoController.class).devolver(m.getIdEquipo())).withRel("devolver")
                ))
                .toList();

        CollectionModel<EntityModel<MaquinaResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(MaquinaArriendoController.class).listarTodas()).withSelfRel()
        );

        return ResponseEntity.ok(collection);
    }

    @PostMapping
    public ResponseEntity<EntityModel<MaquinaResponseDTO>> registrar(@Valid @RequestBody MaquinaRequestDTO dto) {
        MaquinaResponseDTO registrada = maquinaArriendoService.registrar(dto);

        EntityModel<MaquinaResponseDTO> model = EntityModel.of(registrada,
                linkTo(methodOn(MaquinaArriendoController.class).listarTodas()).withRel("maquinas"),
                linkTo(methodOn(MaquinaArriendoController.class).arrendar(registrada.getIdEquipo(), null)).withRel("arrendar")
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{idEquipo}/arrendar")
    public ResponseEntity<EntityModel<MaquinaResponseDTO>> arrendar(
            @PathVariable Integer idEquipo,
            @Valid @RequestBody ProcesarArriendoRequestDTO dto) {
        
        MaquinaResponseDTO arrendada = maquinaArriendoService.procesarArriendo(idEquipo, dto);

        EntityModel<MaquinaResponseDTO> model = EntityModel.of(arrendada,
                linkTo(methodOn(MaquinaArriendoController.class).listarTodas()).withRel("maquinas"),
                linkTo(methodOn(MaquinaArriendoController.class).devolver(idEquipo)).withRel("devolver")
        );

        return ResponseEntity.ok(model);
    }

    @PutMapping("/{idEquipo}/devolver")
    public ResponseEntity<EntityModel<MaquinaResponseDTO>> devolver(@PathVariable Integer idEquipo) {
        MaquinaResponseDTO devuelta = maquinaArriendoService.procesarDevolucion(idEquipo);

        EntityModel<MaquinaResponseDTO> model = EntityModel.of(devuelta,
                linkTo(methodOn(MaquinaArriendoController.class).listarTodas()).withRel("maquinas"),
                linkTo(methodOn(MaquinaArriendoController.class).arrendar(idEquipo, null)).withRel("arrendar")
        );

        return ResponseEntity.ok(model);
    }
}
