package ferrefix.ms_sugerencia.controller;

import ferrefix.ms_sugerencia.assembler.SugerenciaAssembler;
import ferrefix.ms_sugerencia.dto.SugerenciaRequestDTO;
import ferrefix.ms_sugerencia.dto.SugerenciaResponseDTO;
import ferrefix.ms_sugerencia.service.SugerenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/api/sugerencias")
@RequiredArgsConstructor
public class SugerenciaController {

    private final SugerenciaService sugerenciaService;
    private final SugerenciaAssembler sugerenciaAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<SugerenciaResponseDTO>>> listarTodas() {
        log.info("Petición recibida: GET /api/sugerencias");
        List<SugerenciaResponseDTO> sugerencias = sugerenciaService.listarTodas();
        CollectionModel<EntityModel<SugerenciaResponseDTO>> collection = sugerenciaAssembler.toCollectionModel(sugerencias)
                .add(linkTo(methodOn(SugerenciaController.class).listarTodas()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @PostMapping
    public ResponseEntity<EntityModel<SugerenciaResponseDTO>> crear(@Valid @RequestBody SugerenciaRequestDTO dto) {
        log.info("Petición recibida: POST /api/sugerencias");
        SugerenciaResponseDTO response = sugerenciaService.crear(dto);
        EntityModel<SugerenciaResponseDTO> model = sugerenciaAssembler.toModel(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Petición recibida: DELETE /api/sugerencias/{}", id);
        sugerenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
