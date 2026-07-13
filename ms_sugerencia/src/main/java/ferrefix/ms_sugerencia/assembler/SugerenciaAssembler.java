package ferrefix.ms_sugerencia.assembler;

import ferrefix.ms_sugerencia.controller.SugerenciaController;
import ferrefix.ms_sugerencia.dto.SugerenciaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SugerenciaAssembler implements RepresentationModelAssembler<SugerenciaResponseDTO, EntityModel<SugerenciaResponseDTO>> {

    @Override
    public EntityModel<SugerenciaResponseDTO> toModel(SugerenciaResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(SugerenciaController.class).listarTodas()).withRel("sugerencias"),
                linkTo(methodOn(SugerenciaController.class).eliminar(dto.getIdSugerencia())).withRel("eliminar")
        );
    }
}
