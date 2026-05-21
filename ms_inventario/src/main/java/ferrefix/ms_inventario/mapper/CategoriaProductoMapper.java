package ferrefix.ms_inventario.mapper;

import org.springframework.stereotype.Component;

import ferrefix.ms_inventario.dto.CategoriaProductoRequestDTO;
import ferrefix.ms_inventario.dto.CategoriaProductoResponseDTO;
import ferrefix.ms_inventario.model.CategoriaProducto;

@Component
public class CategoriaProductoMapper {
    // Metodo para convertir un DTO a Entity
    public CategoriaProducto toEntity(CategoriaProductoRequestDTO dto) {
        return CategoriaProducto.builder()
            .nombreCategoria(dto.getNombreCategoria())
            .build();
    }

    // Metodo para convertir un Entity a DTO
    public CategoriaProductoResponseDTO toResponseDTO(CategoriaProducto entity) {
        return CategoriaProductoResponseDTO.builder()
            .idCategoria(entity.getIdCategoria())
            .nombreCategoria(entity.getNombreCategoria())
            .build();
    }

    // Metodo para actualizar una entidad con datos de un ResponseDTO
    public void updateEntity(CategoriaProducto entity, CategoriaProductoRequestDTO dto) {
        entity.setNombreCategoria(dto.getNombreCategoria());
    }
}
