package ferrefix.ms_inventario.mapper;

import org.springframework.stereotype.Component;

import ferrefix.ms_inventario.dto.UnidadMedidaRequestDTO;
import ferrefix.ms_inventario.dto.UnidadMedidaResponseDTO;
import ferrefix.ms_inventario.model.UnidadMedida;

@Component
public class UnidadMedidaMapper {
    // Metodo para convertir un DTO a Entity
    public UnidadMedida toEntity(UnidadMedidaRequestDTO dto) {
        return UnidadMedida.builder()
            .nombreUnidadMedida(dto.getNombreUnidadMedida())
            .build();
    }

    // Metodo para convertir un Entity a DTO
    public UnidadMedidaResponseDTO toDTO(UnidadMedida entity) {
        return UnidadMedidaResponseDTO.builder()
            .idUnidadMedida(entity.getIdUnidadMedida())
            .nombreUnidadMedida(entity.getNombreUnidadMedida())
            .build();
    }

    // Metodo para actualizar Entity x DTO
    public void updateEntity(UnidadMedida entity, UnidadMedidaRequestDTO dto) {
        entity.setNombreUnidadMedida(dto.getNombreUnidadMedida());
    }
}
