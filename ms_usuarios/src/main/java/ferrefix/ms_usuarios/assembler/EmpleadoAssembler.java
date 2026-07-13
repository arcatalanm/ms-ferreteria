package ferrefix.ms_usuarios.assembler;

import ferrefix.ms_usuarios.controller.EmpleadoController;
import ferrefix.ms_usuarios.dto.EmpleadoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EmpleadoAssembler implements RepresentationModelAssembler<EmpleadoResponseDTO, EntityModel<EmpleadoResponseDTO>> {

    @Override
    public EntityModel<EmpleadoResponseDTO> toModel(EmpleadoResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(EmpleadoController.class).obtenerEmpleadoPorRun(dto.getRunEmpleadoCompleto(), null)).withSelfRel(),
                linkTo(methodOn(EmpleadoController.class).listarEmpleados(null)).withRel("empleados"),
                linkTo(methodOn(EmpleadoController.class).actualizarEmpleado(dto.getRunEmpleadoCompleto(), null, null)).withRel("actualizar"),
                linkTo(methodOn(EmpleadoController.class).eliminarEmpleado(dto.getRunEmpleadoCompleto(), null)).withRel("eliminar")
        );
    }
}
