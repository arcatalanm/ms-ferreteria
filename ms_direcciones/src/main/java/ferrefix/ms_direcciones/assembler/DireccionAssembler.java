package ferrefix.ms_direcciones.assembler;

import ferrefix.ms_direcciones.controller.DireccionController;
import ferrefix.ms_direcciones.dto.DireccionResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DireccionAssembler implements RepresentationModelAssembler<DireccionResponseDTO, EntityModel<DireccionResponseDTO>> {

    @Override
    public EntityModel<DireccionResponseDTO> toModel(DireccionResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(DireccionController.class).obtenerDireccionPorId(dto.getIdDireccion())).withSelfRel(),
                linkTo(methodOn(DireccionController.class).listarDirecciones()).withRel("direcciones"),
                linkTo(methodOn(DireccionController.class).actualizarDireccion(dto.getIdDireccion(), null)).withRel("actualizar"),
                linkTo(methodOn(DireccionController.class).eliminarDireccion(dto.getIdDireccion())).withRel("eliminar")
        );
    }
}
