package ferrefix.ms_inventario.assembler;

import ferrefix.ms_inventario.controller.CategoriaProductoController;
import ferrefix.ms_inventario.dto.CategoriaProductoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoriaProductoAssembler implements RepresentationModelAssembler<CategoriaProductoResponseDTO, EntityModel<CategoriaProductoResponseDTO>> {

    @Override
    public EntityModel<CategoriaProductoResponseDTO> toModel(CategoriaProductoResponseDTO dto) {
        return EntityModel.of(dto,
                linkTo(methodOn(CategoriaProductoController.class).buscarCategoriaPorId(dto.getIdCategoria())).withSelfRel(),
                linkTo(methodOn(CategoriaProductoController.class).buscarTodasCategorias()).withRel("categorias"),
                linkTo(methodOn(CategoriaProductoController.class).actualizarCategoriaProducto(dto.getIdCategoria(), null)).withRel("actualizar"),
                linkTo(methodOn(CategoriaProductoController.class).eliminarCategoriaProducto(dto.getIdCategoria(), null)).withRel("eliminar")
        );
    }
}
