package ferrefix.ms_compras.assembler;

import ferrefix.ms_compras.controller.CompraController;
import ferrefix.ms_compras.dto.CompraResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CompraAssembler implements RepresentationModelAssembler<CompraResponseDTO, EntityModel<CompraResponseDTO>> {

    @Override
    public EntityModel<CompraResponseDTO> toModel(CompraResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(CompraController.class).obtenerPorId(dto.getIdCompra())).withSelfRel(),
                linkTo(methodOn(CompraController.class).listarTodas()).withRel("compras")
        );
    }
}
