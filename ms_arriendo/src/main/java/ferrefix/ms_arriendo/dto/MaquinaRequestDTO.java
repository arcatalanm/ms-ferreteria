package ferrefix.ms_arriendo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaquinaRequestDTO {

    @NotBlank(message = "El código interno es obligatorio")
    private String codigoInterno;

    @NotBlank(message = "El nombre de la máquina es obligatorio")
    private String nombreMaquina;

    @NotNull(message = "El precio por día es obligatorio")
    private Integer precioPorDia;
}
