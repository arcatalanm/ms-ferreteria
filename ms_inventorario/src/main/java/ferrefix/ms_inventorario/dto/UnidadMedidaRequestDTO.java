package ferrefix.ms_inventorario.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UnidadMedidaRequestDTO {
    @NotBlank(message = "El nombre de la unidad de medida no puede estar vacío")
    private String nombreUnidadMedida;
}
