package ferrefix.ms_marcas.controller;

import ferrefix.ms_marcas.dto.MarcaRequestDTO;
import ferrefix.ms_marcas.dto.MarcaResponseDTO;
import ferrefix.ms_marcas.exception.ApiSuccessResponse;
import ferrefix.ms_marcas.service.MarcaService;
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
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MarcaResponseDTO>>> listarTodas() {
        List<MarcaResponseDTO> marcas = marcaService.listarTodas();

        List<EntityModel<MarcaResponseDTO>> models = marcas.stream()
                .map(m -> EntityModel.of(m,
                        linkTo(methodOn(MarcaController.class).obtenerPorId(m.getIdMarca())).withSelfRel(),
                        linkTo(methodOn(MarcaController.class).listarTodas()).withRel("marcas")
                ))
                .toList();

        CollectionModel<EntityModel<MarcaResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(MarcaController.class).listarTodas()).withSelfRel()
        );

        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<MarcaResponseDTO>> obtenerPorId(@PathVariable Integer id) {
        MarcaResponseDTO marca = marcaService.obtenerPorId(id);

        EntityModel<MarcaResponseDTO> model = EntityModel.of(marca,
                linkTo(methodOn(MarcaController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(MarcaController.class).listarTodas()).withRel("marcas"),
                linkTo(methodOn(MarcaController.class).actualizar(id, null)).withRel("actualizar"),
                linkTo(methodOn(MarcaController.class).eliminar(id)).withRel("eliminar")
        );

        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<MarcaResponseDTO>> crear(@Valid @RequestBody MarcaRequestDTO dto) {
        MarcaResponseDTO creada = marcaService.crear(dto);

        EntityModel<MarcaResponseDTO> model = EntityModel.of(creada,
                linkTo(methodOn(MarcaController.class).obtenerPorId(creada.getIdMarca())).withSelfRel(),
                linkTo(methodOn(MarcaController.class).listarTodas()).withRel("marcas")
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<MarcaResponseDTO>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody MarcaRequestDTO dto) {
        MarcaResponseDTO actualizada = marcaService.actualizar(id, dto);

        EntityModel<MarcaResponseDTO> model = EntityModel.of(actualizada,
                linkTo(methodOn(MarcaController.class).obtenerPorId(id)).withSelfRel(),
                linkTo(methodOn(MarcaController.class).listarTodas()).withRel("marcas")
        );

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        marcaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
