package ferrefix.ms_marcas.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarcaRequestDTO {

    @NotBlank(message = "El nombre de la marca es obligatorio")
    private String nombreMarca;
}
