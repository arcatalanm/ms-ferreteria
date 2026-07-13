package ferrefix.ms_usuarios.assembler;

import ferrefix.ms_usuarios.controller.CargoController;
import ferrefix.ms_usuarios.dto.CargoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CargoAssembler implements RepresentationModelAssembler<CargoResponseDTO, EntityModel<CargoResponseDTO>> {

    @Override
    public EntityModel<CargoResponseDTO> toModel(CargoResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(CargoController.class).obtenerCargoPorId(dto.getIdCargo(), null)).withSelfRel(),
                linkTo(methodOn(CargoController.class).listarCargos(null)).withRel("cargos"),
                linkTo(methodOn(CargoController.class).actualizarCargo(dto.getIdCargo(), null, null)).withRel("actualizar"),
                linkTo(methodOn(CargoController.class).eliminarCargo(dto.getIdCargo())).withRel("eliminar")
        );
    }
}
