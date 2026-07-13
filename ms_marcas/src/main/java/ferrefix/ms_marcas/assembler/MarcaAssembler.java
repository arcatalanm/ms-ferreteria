package ferrefix.ms_marcas.assembler;

import ferrefix.ms_marcas.controller.MarcaController;
import ferrefix.ms_marcas.dto.MarcaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MarcaAssembler implements RepresentationModelAssembler<MarcaResponseDTO, EntityModel<MarcaResponseDTO>> {

    @Override
    public EntityModel<MarcaResponseDTO> toModel(MarcaResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(MarcaController.class).obtenerPorId(dto.getIdMarca())).withSelfRel(),
                linkTo(methodOn(MarcaController.class).listarTodas()).withRel("marcas"),
                linkTo(methodOn(MarcaController.class).actualizar(dto.getIdMarca(), null)).withRel("actualizar"),
                linkTo(methodOn(MarcaController.class).eliminar(dto.getIdMarca())).withRel("eliminar")
        );
    }
}
