package ferrefix.ms_direcciones.dto;

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
public class DireccionResponseDTO {
    private Long idDireccion;
    private String calle;
    private Integer numero;
    private String departamento;
    private String comuna;
    private String ciudad;
    
    // Campo enriquecido para mostrar en el frontend de forma limpia
    private String direccionCompleta;
}