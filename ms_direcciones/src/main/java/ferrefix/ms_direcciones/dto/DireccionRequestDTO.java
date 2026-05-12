package ferrefix.ms_direcciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DireccionRequestDTO {

    @NotBlank(message = "La calle no puede estar vacía")
    private String calle;

    @NotNull(message = "El número no puede ser nulo")
    private Integer numero;

    // Opcional, por lo que no lleva validación
    private String departamento;

    @NotBlank(message = "La comuna no puede estar vacía")
    private String comuna;

    @NotBlank(message = "La ciudad no puede estar vacía")
    private String ciudad;
}