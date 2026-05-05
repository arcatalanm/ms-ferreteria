package ferrefix.ms_inventorario.dto;

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

public class UnidadMedidaResponseDTO {
    // No es necesario validar ya que viene validado desde la base de datos

    private Integer idUnidadMedida;

    private String nombreUnidadMedida;
}
