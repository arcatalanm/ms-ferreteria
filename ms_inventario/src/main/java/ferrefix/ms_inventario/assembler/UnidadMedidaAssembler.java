package ferrefix.ms_inventario.assembler;

import ferrefix.ms_inventario.controller.UnidadMedidaController;
import ferrefix.ms_inventario.dto.UnidadMedidaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UnidadMedidaAssembler implements RepresentationModelAssembler<UnidadMedidaResponseDTO, EntityModel<UnidadMedidaResponseDTO>> {

    @Override
    public EntityModel<UnidadMedidaResponseDTO> toModel(UnidadMedidaResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(UnidadMedidaController.class).buscarUnidadMedidaPorId(dto.getIdUnidadMedida())).withSelfRel(),
                linkTo(methodOn(UnidadMedidaController.class).buscarTodasUnidadesMedida()).withRel("unidades_medida"),
                linkTo(methodOn(UnidadMedidaController.class).actualizarUnidadMedida(dto.getIdUnidadMedida(), null)).withRel("actualizar"),
                linkTo(methodOn(UnidadMedidaController.class).eliminarUnidadMedida(dto.getIdUnidadMedida(), null)).withRel("eliminar")
        );
    }
}
