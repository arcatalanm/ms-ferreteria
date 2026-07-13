package ferrefix.ms_inventario.assembler;

import ferrefix.ms_inventario.controller.ProductoController;
import ferrefix.ms_inventario.dto.ProductoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ProductoAssembler implements RepresentationModelAssembler<ProductoResponseDTO, EntityModel<ProductoResponseDTO>> {

    @Override
    public EntityModel<ProductoResponseDTO> toModel(ProductoResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(ProductoController.class).buscarProductoPorId(dto.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).buscarTodosProductos()).withRel("productos"),
                linkTo(methodOn(ProductoController.class).actualizarProducto(dto.getId(), null)).withRel("actualizar"),
                linkTo(methodOn(ProductoController.class).eliminarProducto(dto.getId(), null)).withRel("eliminar")
        );
    }
}
