package ferrefix.ms_compras.controller;

import ferrefix.ms_compras.assembler.CompraAssembler;
import ferrefix.ms_compras.dto.CompraRequestDTO;
import ferrefix.ms_compras.dto.CompraResponseDTO;
import ferrefix.ms_compras.service.CompraService;
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
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;
    private final CompraAssembler compraAssembler;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<CompraResponseDTO>>> listarTodas() {
        log.info("Petición recibida: GET /api/compras");
        List<CompraResponseDTO> compras = compraService.listarTodas();
        CollectionModel<EntityModel<CompraResponseDTO>> collection = compraAssembler.toCollectionModel(compras)
                .add(linkTo(methodOn(CompraController.class).listarTodas()).withSelfRel());

        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CompraResponseDTO>> obtenerPorId(@PathVariable Long id) {
        log.info("Petición recibida: GET /api/compras/{}", id);
        CompraResponseDTO compra = compraService.obtenerPorId(id);
        EntityModel<CompraResponseDTO> model = compraAssembler.toModel(compra);

        return ResponseEntity.ok(model);
    }

    @PostMapping
    public ResponseEntity<EntityModel<CompraResponseDTO>> crearOrden(@Valid @RequestBody CompraRequestDTO request) {
        log.info("Petición recibida: POST /api/compras");
        CompraResponseDTO response = compraService.crearOrdenCompra(request);
        EntityModel<CompraResponseDTO> model = compraAssembler.toModel(response);

        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PutMapping("/{id}/recibir")
    public ResponseEntity<EntityModel<CompraResponseDTO>> recibirMercancia(@PathVariable Long id) {
        log.info("Petición recibida: PUT /api/compras/{}/recibir", id);
        CompraResponseDTO response = compraService.procesarRecepcionMercancia(id);
        EntityModel<CompraResponseDTO> model = compraAssembler.toModel(response);

        return ResponseEntity.ok(model);
    }
}
