package ferrefix.ms_usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class DireccionRequestDTO {
    @NotBlank(message = "La calle no puede estar vacía")
    private String calle;

    @NotNull(message = "El número de la dirección es obligatorio")
    private Integer numero;

    private String departamento;

    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;

    // La ciudad no es obligatoria, hay zonas rurales sin ciudad definida
    private String ciudad;

    // Run especificado para asociar la dirección al cliente correspondiente
    @NotNull(message = "El run del cliente es obligatorio para asociar la dirección")
    private Integer runCliente;
}
