package ferrefix.ms_ventas.controller;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ferrefix.ms_ventas.assembler.TipoPagoAssembler;
import ferrefix.ms_ventas.dto.TipoPagoRequestDTO;
import ferrefix.ms_ventas.dto.TipoPagoResponseDTO;
import ferrefix.ms_ventas.service.TipoPagoService;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/ventas/tipos-pago")
@RequiredArgsConstructor
public class TipoPagoController {

    private static final Logger logger = LoggerFactory.getLogger(TipoPagoController.class);
    private final TipoPagoService tipoPagoService;
    private final TipoPagoAssembler tipoPagoAssembler;

    @PostMapping
    public ResponseEntity<EntityModel<TipoPagoResponseDTO>> crear(@Valid @RequestBody TipoPagoRequestDTO dto, HttpServletRequest request) {
        logger.info("POST /api/ventas/tipos-pago - Nombre: '{}'", dto.getNombreTipoPago());
        TipoPagoResponseDTO creado = tipoPagoService.crear(dto);
        EntityModel<TipoPagoResponseDTO> model = tipoPagoAssembler.toModel(creado);

        logger.info("POST /api/ventas/tipos-pago - Creado ID: {}. Respondiendo 201 CREATED", creado.getIdTipoPago());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<TipoPagoResponseDTO>>> obtenerTodos(HttpServletRequest request) {
        logger.info("GET /api/ventas/tipos-pago - Listando tipos de pago");
        List<TipoPagoResponseDTO> lista = tipoPagoService.obtenerTodos();
        CollectionModel<EntityModel<TipoPagoResponseDTO>> collection = tipoPagoAssembler.toCollectionModel(lista)
                .add(linkTo(methodOn(TipoPagoController.class).obtenerTodos(null)).withSelfRel());

        logger.info("GET /api/ventas/tipos-pago - {} registros. Respondiendo 200 OK", lista.size());
        return ResponseEntity.ok(collection);
    }

    @GetMapping("/{idTipoPago}")
    public ResponseEntity<EntityModel<TipoPagoResponseDTO>> obtenerPorId(@PathVariable Integer idTipoPago, HttpServletRequest request) {
        logger.info("GET /api/ventas/tipos-pago/{} - Buscando tipo de pago", idTipoPago);
        TipoPagoResponseDTO tipoPago = tipoPagoService.obtenerPorId(idTipoPago);
        EntityModel<TipoPagoResponseDTO> model = tipoPagoAssembler.toModel(tipoPago);

        logger.info("GET /api/ventas/tipos-pago/{} - Encontrado. Respondiendo 200 OK", idTipoPago);
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{idTipoPago}")
    public ResponseEntity<EntityModel<TipoPagoResponseDTO>> actualizar(
            @PathVariable Integer idTipoPago,
            @Valid @RequestBody TipoPagoRequestDTO dto, HttpServletRequest request) {

        logger.info("PUT /api/ventas/tipos-pago/{} - Actualizando tipo de pago", idTipoPago);
        TipoPagoResponseDTO actualizado = tipoPagoService.actualizar(idTipoPago, dto);
        EntityModel<TipoPagoResponseDTO> model = tipoPagoAssembler.toModel(actualizado);

        logger.info("PUT /api/ventas/tipos-pago/{} - Actualizado. Respondiendo 200 OK", idTipoPago);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/{idTipoPago}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer idTipoPago) {
        logger.info("DELETE /api/ventas/tipos-pago/{} - Solicitud de eliminación", idTipoPago);
        tipoPagoService.eliminar(idTipoPago);
        logger.info("DELETE /api/ventas/tipos-pago/{} - Eliminado. Respondiendo 204 NO CONTENT", idTipoPago);
        return ResponseEntity.noContent().build();
    }
}