package ferrefix.ms_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

// Hacemos referencia a la direccion del ms_direcciones

public class DireccionDTO {
    private Long idDireccion;
    private String calle;
    private Integer numero;
    private String departamento;
    private String comuna;
    private String ciudad;
}