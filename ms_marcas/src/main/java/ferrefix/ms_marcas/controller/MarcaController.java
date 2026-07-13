package ferrefix.ms_marcas.controller;

import ferrefix.ms_marcas.assembler.MarcaAssembler;
import ferrefix.ms_marcas.dto.MarcaRequestDTO;
import ferrefix.ms_marcas.dto.MarcaResponseDTO;
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
    private final MarcaAssembler marcaAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MarcaResponseDTO>>> listarTodas() {
        List<MarcaResponseDTO> marcas = marcaService.listarTodas();
        CollectionModel<EntityModel<MarcaResponseDTO>> collection = marcaAssembler.toCollectionModel(marcas)
                .add(linkTo(methodOn(MarcaController.class).listarTodas()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<MarcaResponseDTO>> obtenerPorId(@PathVariable Integer id) {
        MarcaResponseDTO marca = marcaService.obtenerPorId(id);
        EntityModel<MarcaResponseDTO> model = marcaAssembler.toModel(marca);

        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<MarcaResponseDTO>> crear(@Valid @RequestBody MarcaRequestDTO dto) {
        MarcaResponseDTO creada = marcaService.crear(dto);
        EntityModel<MarcaResponseDTO> model = marcaAssembler.toModel(creada);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<MarcaResponseDTO>> actualizar(
            @PathVariable Integer id, @Valid @RequestBody MarcaRequestDTO dto) {
        MarcaResponseDTO actualizada = marcaService.actualizar(id, dto);
        EntityModel<MarcaResponseDTO> model = marcaAssembler.toModel(actualizada);

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        marcaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
