package ferrefix.ms_sugerencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SugerenciaRequestDTO {

    @NotBlank(message = "El contenido del mensaje es obligatorio")
    @Size(max = 1000, message = "El mensaje no puede exceder los 1000 caracteres")
    private String contenidoMensaje;
}
