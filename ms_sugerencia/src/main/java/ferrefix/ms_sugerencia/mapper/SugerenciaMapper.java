package ferrefix.ms_sugerencia.mapper;

import ferrefix.ms_sugerencia.dto.SugerenciaRequestDTO;
import ferrefix.ms_sugerencia.dto.SugerenciaResponseDTO;
import ferrefix.ms_sugerencia.model.Sugerencia;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SugerenciaMapper {

    public Sugerencia toEntity(SugerenciaRequestDTO dto) {
        return Sugerencia.builder()
                .contenidoMensaje(dto.getContenidoMensaje())
                .fechaIngreso(LocalDateTime.now())
                .build();
    }

    public SugerenciaResponseDTO toResponseDTO(Sugerencia entity) {
        return SugerenciaResponseDTO.builder()
                .idSugerencia(entity.getIdSugerencia())
                .contenidoMensaje(entity.getContenidoMensaje())
                .fechaIngreso(entity.getFechaIngreso())
                .build();
    }
}
