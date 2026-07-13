package ferrefix.ms_proveedores.assembler;

import ferrefix.ms_proveedores.controller.ProveedorController;
import ferrefix.ms_proveedores.dto.ProveedorResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProveedorAssembler implements RepresentationModelAssembler<ProveedorResponseDTO, EntityModel<ProveedorResponseDTO>> {

    @Override
    public EntityModel<ProveedorResponseDTO> toModel(ProveedorResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(ProveedorController.class).obtenerProveedor(dto.getIdProveedor(), null)).withSelfRel(),
                linkTo(methodOn(ProveedorController.class).listarProveedores(null)).withRel("proveedores"),
                linkTo(methodOn(ProveedorController.class).actualizarProveedor(dto.getIdProveedor(), null, null)).withRel("actualizar"),
                linkTo(methodOn(ProveedorController.class).eliminarProveedor(dto.getIdProveedor(), null)).withRel("eliminar")
        );
    }
}
