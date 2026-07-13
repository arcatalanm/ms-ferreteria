package ferrefix.ms_usuarios.assembler;

import ferrefix.ms_usuarios.controller.ClienteController;
import ferrefix.ms_usuarios.dto.ClienteResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClienteAssembler implements RepresentationModelAssembler<ClienteResponseDTO, EntityModel<ClienteResponseDTO>> {

    @Override
    public EntityModel<ClienteResponseDTO> toModel(ClienteResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(ClienteController.class).obtenerPorRun(dto.getRunClienteCompleto(), null)).withSelfRel(),
                linkTo(methodOn(ClienteController.class).listarClientes(null)).withRel("clientes"),
                linkTo(methodOn(ClienteController.class).actualizarCliente(dto.getRunClienteCompleto(), null, null)).withRel("actualizar"),
                linkTo(methodOn(ClienteController.class).eliminarCliente(dto.getRunClienteCompleto(), null)).withRel("eliminar")
        );
    }
}
