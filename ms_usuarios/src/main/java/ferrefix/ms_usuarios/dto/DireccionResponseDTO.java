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
public class DireccionResponseDTO {
    private Long idDireccion;

    private String calle;
    private Integer numero;
    private String departamento;
    private String comuna;
    private String ciudad;

    // Direccion Completa mas que nada para inspeccion 
    private String direccionCompleta;
}
