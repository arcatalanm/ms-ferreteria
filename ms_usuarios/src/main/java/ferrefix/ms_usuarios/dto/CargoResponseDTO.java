package ferrefix.ms_usuarios.dto;

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
public class CargoResponseDTO {
    // Datos Ya validados provenientes de la base de datos
    private Integer idCargo;
    private String nombreCargo;
}
