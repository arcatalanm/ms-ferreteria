package ferrefix.ms_marcas.mapper;

import ferrefix.ms_marcas.dto.MarcaRequestDTO;
import ferrefix.ms_marcas.dto.MarcaResponseDTO;
import ferrefix.ms_marcas.model.Marca;
import org.springframework.stereotype.Component;

@Component
public class MarcaMapper {

    public Marca toEntity(MarcaRequestDTO dto) {
        return Marca.builder()
                .nombreMarca(dto.getNombreMarca())
                .build();
    }

    public MarcaResponseDTO toResponseDTO(Marca entity) {
        return MarcaResponseDTO.builder()
                .idMarca(entity.getIdMarca())
                .nombreMarca(entity.getNombreMarca())
                .build();
    }

    public void updateEntity(Marca entity, MarcaRequestDTO dto) {
        entity.setNombreMarca(dto.getNombreMarca());
    }
}
