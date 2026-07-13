package ferrefix.ms_ventas.assembler;

import ferrefix.ms_ventas.controller.TipoPagoController;
import ferrefix.ms_ventas.dto.TipoPagoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TipoPagoAssembler implements RepresentationModelAssembler<TipoPagoResponseDTO, EntityModel<TipoPagoResponseDTO>> {

    @Override
    public EntityModel<TipoPagoResponseDTO> toModel(TipoPagoResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(TipoPagoController.class).obtenerPorId(dto.getIdTipoPago(), null)).withSelfRel(),
                linkTo(methodOn(TipoPagoController.class).obtenerTodos(null)).withRel("tipos-pago"),
                linkTo(methodOn(TipoPagoController.class).actualizar(dto.getIdTipoPago(), null, null)).withRel("actualizar"),
                linkTo(methodOn(TipoPagoController.class).eliminar(dto.getIdTipoPago())).withRel("eliminar")
        );
    }
}
