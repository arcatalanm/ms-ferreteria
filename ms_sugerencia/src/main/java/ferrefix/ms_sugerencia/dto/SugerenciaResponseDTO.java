package ferrefix.ms_sugerencia.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SugerenciaResponseDTO {
    private Long idSugerencia;
    private String contenidoMensaje;
    private LocalDateTime fechaIngreso;
}
