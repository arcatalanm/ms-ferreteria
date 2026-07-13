package ferrefix.ms_ventas.assembler;

import ferrefix.ms_ventas.controller.VentaController;
import ferrefix.ms_ventas.dto.DetalleVentaResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DetalleVentaAssembler implements RepresentationModelAssembler<DetalleVentaResponseDTO, EntityModel<DetalleVentaResponseDTO>> {

    @Override
    public EntityModel<DetalleVentaResponseDTO> toModel(DetalleVentaResponseDTO dto) {
        throw new UnsupportedOperationException("Use toModel(dto, idVenta) instead");
    }

    public EntityModel<DetalleVentaResponseDTO> toModel(DetalleVentaResponseDTO dto, Long idVenta) {
        return EntityModel.of(dto,
                linkTo(methodOn(VentaController.class).listarDetalles(idVenta, null)).withRel("detalles"),
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(idVenta, null)).withRel("venta")
        );
    }
}
