package ferrefix.ms_arriendo.assembler;

import ferrefix.ms_arriendo.controller.MaquinaArriendoController;
import ferrefix.ms_arriendo.dto.MaquinaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MaquinaArriendoAssembler implements RepresentationModelAssembler<MaquinaResponseDTO, EntityModel<MaquinaResponseDTO>> {

    @Override
    public EntityModel<MaquinaResponseDTO> toModel(MaquinaResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(MaquinaArriendoController.class).listarTodas()).withRel("maquinas"),
                linkTo(methodOn(MaquinaArriendoController.class).arrendar(dto.getIdEquipo(), null)).withRel("arrendar"),
                linkTo(methodOn(MaquinaArriendoController.class).devolver(dto.getIdEquipo())).withRel("devolver")
        );
    }
}
