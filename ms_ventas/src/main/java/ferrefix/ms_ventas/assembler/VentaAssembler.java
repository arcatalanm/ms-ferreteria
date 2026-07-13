package ferrefix.ms_ventas.assembler;

import ferrefix.ms_ventas.controller.VentaController;
import ferrefix.ms_ventas.dto.VentaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class VentaAssembler implements RepresentationModelAssembler<VentaResponseDTO, EntityModel<VentaResponseDTO>> {

    @Override
    public EntityModel<VentaResponseDTO> toModel(VentaResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(dto.getIdVenta(), null)).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas(null)).withRel("ventas"),
                linkTo(methodOn(VentaController.class).listarDetalles(dto.getIdVenta(), null)).withRel("detalles"),
                linkTo(methodOn(VentaController.class).eliminarVenta(dto.getIdVenta())).withRel("eliminar")
        );
    }
}
